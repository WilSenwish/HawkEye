package com.littleyes.common.util;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.objenesis.Objenesis;
import org.springframework.objenesis.ObjenesisStd;
import org.springframework.util.CollectionUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * <p> <b>ProtoStuff 序列化工具类</b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-02-19
 */
@Slf4j
public class ProtoStuffUtils {

    private static final Objenesis OBJENESIS = new ObjenesisStd(true);

    /**
     * 需要使用包装类进行序列化/反序列化的class集合
     */
    private static final Set<Class<?>> WRAPPER_SET = new LinkedHashSet<>();

    /**
     * 序列化/反序列化包装类 Class 对象
     */
    private static final Class<ProtoStuffSerializeDeserializeWrapper> WRAPPER_CLASS = ProtoStuffSerializeDeserializeWrapper.class;

    /**
     * 序列化/反序列化包装类 Schema 对象
     */
    private static final Schema<ProtoStuffSerializeDeserializeWrapper> WRAPPER_SCHEMA = RuntimeSchema.createFrom(WRAPPER_CLASS);

    /**
     * 序列化/反序列化类 Schema 对象缓存
     */
    private static final ConcurrentMap<Class<?>, Schema<?>> SCHEMA_CACHE = new ConcurrentHashMap<>();

    private static final ThreadLocal<LinkedBuffer> THREAD_LOCAL_BUFFER = ThreadLocal
            .withInitial(LinkedBuffer::allocate);

    /**
     * 预定义一些 ProtoStuff 无法直接序列化/反序列化的对象
     */
    static {
        WRAPPER_SET.add(List.class);
        WRAPPER_SET.add(ArrayList.class);
        WRAPPER_SET.add(LinkedList.class);
        WRAPPER_SET.add(CopyOnWriteArrayList.class);
        WRAPPER_SET.add(Stack.class);
        WRAPPER_SET.add(Vector.class);

        WRAPPER_SET.add(Map.class);
        WRAPPER_SET.add(HashMap.class);
        WRAPPER_SET.add(TreeMap.class);
        WRAPPER_SET.add(SortedMap.class);
        WRAPPER_SET.add(LinkedHashMap.class);
        WRAPPER_SET.add(ConcurrentHashMap.class);
        WRAPPER_SET.add(Hashtable.class);

        WRAPPER_SET.add(Set.class);
        WRAPPER_SET.add(HashSet.class);
        WRAPPER_SET.add(TreeSet.class);
        WRAPPER_SET.add(SortedSet.class);
        WRAPPER_SET.add(LinkedHashSet.class);

        WRAPPER_SET.add(Object.class);
    }

    private ProtoStuffUtils() {
    }

    @SuppressWarnings("unchecked")
    private static <T> Schema<T> getSchema(Class<T> clazz) {
        Schema<T> schema = (Schema<T>) SCHEMA_CACHE.get(clazz);
        if (Objects.isNull(schema)) {
            Schema<T> newSchema = RuntimeSchema.createFrom(clazz);
            schema = (Schema<T>) SCHEMA_CACHE.putIfAbsent(clazz, newSchema);
            if (Objects.isNull(schema)) {
                schema = newSchema;
            }
        }

        return schema;
    }

    /**
     * 序列化
     *
     * @param obj
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> byte[] serialize(T obj) {
        if (Objects.isNull(obj)) {
            throw new IllegalArgumentException("序列化对象参数错误！");
        }

        Class<T> clazz = (Class<T>) obj.getClass();
        LinkedBuffer buf = THREAD_LOCAL_BUFFER.get();

        try {
            Object serializeObject = obj;
            Schema schema = WRAPPER_SCHEMA;

            if (!WRAPPER_SET.contains(clazz)) {
                schema = getSchema(clazz);
            } else {
                serializeObject = ProtoStuffSerializeDeserializeWrapper.wrap(obj);
            }
            return ProtostuffIOUtil.toByteArray(serializeObject, schema, buf);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            buf.clear();
        }
    }

    /**
     * 序列化
     *
     * @param list
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> byte[] serializeList(List<T> list) {
        if (CollectionUtils.isEmpty(list)) {
            throw new IllegalArgumentException("序列化对象列表参数错误！");
        }

        Class<T> clazz = (Class<T>) list.get(0).getClass();
        LinkedBuffer buf = THREAD_LOCAL_BUFFER.get();
        ByteArrayOutputStream bos = null;

        try {
            Schema<T> schema = getSchema(clazz);
            bos = new ByteArrayOutputStream();
            ProtostuffIOUtil.writeListTo(bos, list, schema, buf);
            return bos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            buf.clear();

            try {
                if (Objects.nonNull(bos)) {
                    bos.close();
                }
            } catch (IOException e) {
                log.error("HEC ===> | {}", e.getMessage(), e);
            }
        }
    }

    /**
     * 反序列化
     *
     * @param bytes
     * @param clazz
     * @return
     */
    public static <T> T deserialize(byte[] bytes, Class<T> clazz) {
        if (Objects.isNull(bytes) || bytes.length == 0) {
            throw new IllegalArgumentException("反序列化byte数组参数错误！");
        }
        if (Objects.isNull(clazz)) {
            throw new IllegalArgumentException("反序列化对象类参数错误！");
        }

        try {
            if (!WRAPPER_SET.contains(clazz)) {
                T object = OBJENESIS.newInstance(clazz);
                Schema<T> schema = getSchema(clazz);
                ProtostuffIOUtil.mergeFrom(bytes, object, schema);
                return object;
            } else {
                ProtoStuffSerializeDeserializeWrapper<T> wrapper = new ProtoStuffSerializeDeserializeWrapper<>();
                ProtostuffIOUtil.mergeFrom(bytes, wrapper, WRAPPER_SCHEMA);
                return wrapper.getData();
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * 反序列化
     *
     * @param bytes
     * @param clazz
     * @return
     */
    public static <T> List<T> deserializeList(byte[] bytes, Class<T> clazz) {
        if (Objects.isNull(bytes) || bytes.length == 0) {
            throw new IllegalArgumentException("反序列化byte数组参数错误！");
        }
        if (Objects.isNull(clazz)) {
            throw new IllegalArgumentException("反序列化对象类参数错误！");
        }

        try {
            Schema<T> schema = getSchema(clazz);
            return ProtostuffIOUtil.parseListFrom(new ByteArrayInputStream(bytes), schema);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * 对象深复制
     *
     * @param obj
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T copy(T obj) {
        if (Objects.isNull(obj)) {
            return null;
        }

        return deserialize(serialize(obj), (Class<T>) obj.getClass());
    }

    /**
     * 对象深复制
     *
     * @param list
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> copyList(List<T> list) {
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyList();
        }

        return deserializeList(serializeList(list), (Class<T>) list.get(0).getClass());
    }

    static class ProtoStuffSerializeDeserializeWrapper<T> {
        @Getter
        private T data;

        static <T> ProtoStuffSerializeDeserializeWrapper<T> wrap(T data) {
            ProtoStuffSerializeDeserializeWrapper<T> wrapper = new ProtoStuffSerializeDeserializeWrapper<>();
            wrapper.data = data;
            return wrapper;
        }
    }

}
