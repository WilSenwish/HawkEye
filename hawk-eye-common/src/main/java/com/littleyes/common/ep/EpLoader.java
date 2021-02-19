package com.littleyes.common.ep;

import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import static com.littleyes.common.config.HawkEyeConfig.HAWK_EYE_COMMON;

/**
 * <p> <b> 拓展插件服务加载器 </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-02-18
 */
@Slf4j
public class EpLoader<T> {

    private static final ConcurrentMap<Class<?>, EpLoader<?>> EXTENSION_PLUGIN_LOADERS
            = new ConcurrentHashMap<>(64);

    private static final ConcurrentMap<Class<?>, Holder<Object>> EXTENSION_PLUGIN_INSTANCES
            = new ConcurrentHashMap<>(64);

    private static AtomicInteger counter = new AtomicInteger();

    private final Class<T> type;

    private EpLoader(Class<T> type) {
        this.type = type;
    }

    @SuppressWarnings("unchecked")
    public static <T> EpLoader<T> of(Class<T> type) {
        if (type == null) {
            throw new IllegalArgumentException("Extension Plugin type == null");
        }
        if (!type.isInterface()) {
            throw new IllegalArgumentException("Extension Plugin type (" + type + ") is not an interface!");
        }
        if (!type.isAnnotationPresent(SPI.class)) {
            throw new IllegalStateException("Extension Plugin " + type + " must annotate @" + SPI.class.getName() + "!");
        }

        EpLoader<T> loader = (EpLoader<T>) EXTENSION_PLUGIN_LOADERS.get(type);
        if (loader == null) {
            EXTENSION_PLUGIN_LOADERS.putIfAbsent(type, new EpLoader<>(type));
            loader = (EpLoader<T>) EXTENSION_PLUGIN_LOADERS.get(type);
        }

        return loader;
    }

    @SuppressWarnings("unchecked")
    public T load() {
        final Holder<Object> holder = getOrCreateHolder();
        Object instance = holder.get();

        if (instance == null) {
            synchronized (holder) {
                instance = holder.get();
                if (instance == null) {
                    instance = loadExtensionPlugin();
                    holder.set(instance);
                    log.info("{} Loaded [NO.{}] extension plugin [{}] with provider[{}]",
                            HAWK_EYE_COMMON, counter.incrementAndGet(), type, instance);
                }
            }
        }

        return (T) instance;
    }

    private Holder<Object> getOrCreateHolder() {
        Holder<Object> holder = EXTENSION_PLUGIN_INSTANCES.get(type);

        if (holder == null) {
            EXTENSION_PLUGIN_INSTANCES.putIfAbsent(type, new Holder<>());
            holder = EXTENSION_PLUGIN_INSTANCES.get(type);
        }

        return holder;
    }

    private T loadExtensionPlugin() {
        List<T> instances = new LinkedList<>();

        try {
            ServiceLoader<T> loader = ServiceLoader.load(type, getDefaultClassLoader());

            for (T t : loader) {
                // must Annotate @SPI
                if (t.getClass().isAnnotationPresent(SPI.class)) {
                    instances.add(t);
                }
            }

            // TODO extra extends for EpFactory

            if (!instances.isEmpty()) {
                Optional<T> t = instances.stream()
                        .min(Comparator.comparing(e -> e.getClass().getAnnotation(SPI.class).order()));
                if (t.isPresent()) {
                    // return the highest precedence one
                    return t.get();
                }
            }
        } catch (Exception e) {
            log.error("{} Load extension plugin[{}] with error [{}]", HAWK_EYE_COMMON, type, e.getMessage(), e);
        }

        throw new NullPointerException("Extension Plugin of type[" + type.getName() + "] not found!!!");
    }

    private ClassLoader getDefaultClassLoader() {
        ClassLoader cl = null;

        try {
            Class.forName("org.springframework.boot.devtools.restart.classloader.RestartClassLoader");
            cl = type.getClassLoader();
        } catch (ClassNotFoundException ignore) {
            // Without spring boot devtools
        }

        if (cl == null) {
            try {
                cl = Thread.currentThread().getContextClassLoader();
            } catch (Throwable ex) {
                // Cannot access thread context ClassLoader - falling back...
            }

            if (cl == null) {
                // No thread context class loader -> use class loader of this class.
                cl = EpLoader.class.getClassLoader();
                if (cl == null) {
                    // getClassLoader() returning null indicates the bootstrap ClassLoader
                    try {
                        cl = ClassLoader.getSystemClassLoader();
                    } catch (Throwable ex) {
                        // Cannot access system ClassLoader - oh well, maybe the caller can live with null...
                    }
                }
            }
        }

        return cl;
    }

    private static class Holder<T> {
        private volatile T value;

        void set(T value) {
            this.value = value;
        }

        T get() {
            return value;
        }
    }

}
