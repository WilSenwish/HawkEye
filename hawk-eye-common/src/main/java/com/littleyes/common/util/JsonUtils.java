package com.littleyes.common.util;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * <p> <b> JSON 工具类 </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-02-19
 */
@Slf4j
public class JsonUtils {

    private JsonUtils() {
    }

    /**
     * 对象转 json 格式字符串
     *
     * @param object
     * @return
     */
    public static String toString(Object object) {
        return JSON.toJSONString(object);
    }

    /**
     * json 格式字符串转对象
     *
     * @param jsonString
     * @param clazz
     * @return
     */
    public static <T> T parseObject(String jsonString, Class<T> clazz) {
        return JSON.parseObject(jsonString, clazz);
    }

    /**
     * json 格式字符串转对象列表
     *
     * @param jsonString
     * @param clazz
     * @return
     */
    public static <T> List<T> parseList(String jsonString, Class<T> clazz) {
        return JSON.parseArray(jsonString, clazz);
    }

    /**
     * 对象转换
     *
     * @param obj
     * @param clazz
     * @return
     */
    public static <T> T convertObject(Object obj, Class<T> clazz) {
        return parseObject(toString(obj), clazz);
    }

    /**
     * 对象列表转换
     *
     * @param objects
     * @param clazz
     * @return
     */
    public static <T> List<T> convertList(List<?> objects, Class<T> clazz) {
        return parseList(toString(objects), clazz);
    }

    /**
     * 对象复制
     *
     * @param obj
     * @return
     */
    public static <T> T copyObject(T obj) {
        if (Objects.isNull(obj)) {
            return null;
        }

        return parseObject(toString(obj), (Class<T>) obj.getClass());
    }

    /**
     * 对象列表复制
     *
     * @param objects
     * @return
     */
    public static <T> List<T> copyList(List<T> objects) {
        if (Objects.isNull(objects)) {
            return null;
        }

        if (objects.isEmpty()) {
            return Collections.emptyList();
        }

        return parseList(toString(objects), (Class<T>) objects.get(0).getClass());
    }

}
