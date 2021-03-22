package com.littleyes.common.core;

import lombok.extern.slf4j.Slf4j;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import static com.littleyes.common.config.HawkEyeConfig.HAWK_EYE_COMMON;

/**
 * <p> <b> 插件加载器 </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-02-18
 */
@Slf4j
public class PluginLoader<T> {

    private static final ConcurrentMap<Class<?>, PluginLoader<?>> EXTENSION_PLUGIN_LOADERS
            = new ConcurrentHashMap<>(128);

    private static final ConcurrentMap<Class<?>, Holder<Object>> EXTENSION_PLUGIN_INSTANCES
            = new ConcurrentHashMap<>(128);

    private static AtomicInteger counter = new AtomicInteger();

    private final Class<T> type;

    private PluginLoader(Class<T> type) {
        this.type = type;
    }

    @SuppressWarnings("unchecked")
    public static <T> PluginLoader<T> of(Class<T> type) {
        if (type == null) {
            throw new IllegalArgumentException("Plugin's type is null");
        }
        if (!type.isInterface()) {
            throw new IllegalArgumentException("Plugin's type (" + type + ") is not an interface!");
        }
        if (!hasSpiAnnotation(type)) {
            throw new IllegalStateException("Plugin must annotate @" + spiFullName() + "!");
        }

        PluginLoader<T> loader = (PluginLoader<T>) EXTENSION_PLUGIN_LOADERS.get(type);
        if (loader == null) {
            EXTENSION_PLUGIN_LOADERS.putIfAbsent(type, new PluginLoader<>(type));
            loader = (PluginLoader<T>) EXTENSION_PLUGIN_LOADERS.get(type);
        }

        return loader;
    }

    @SuppressWarnings("unchecked")
    public T load() {
        return load(false);
    }

    @SuppressWarnings("unchecked")
    public T load(boolean wrap) {
        final Holder<Object> holder = getOrCreateHolder();
        Object instance = holder.get();

        if (instance == null) {
            synchronized (holder) {
                instance = holder.get();
                if (instance == null) {
                    log.info("{} Load Plugin of [{}]", HAWK_EYE_COMMON, type);
                    instance = loadExtensionPlugin(wrap);
                    holder.set(instance);
                    log.info("{} Loaded [NO.{}] Plugin [{}] with Provider[{}]",
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

    private T loadExtensionPlugin(boolean wrap) {
        List<T> instances = new LinkedList<>();

        try {
            boolean loaded = false;
            ServiceLoader<T> loader = ServiceLoader.load(type, getDefaultClassLoader());

            for (T t : loader) {
                // must Annotate @SPI
                if (hasSpiAnnotation(t.getClass())) {
                    instances.add(t);
                }
                loaded = true;
            }

            if (!loaded) {
                log.error("{} Plugin[{}] without Provider", HAWK_EYE_COMMON, type);
            } else if (instances.isEmpty()) {
                log.error("{} Plugin[{}] must annotate @{}", HAWK_EYE_COMMON, type, spiFullName());
            }
        } catch (Exception e) {
            log.error("{} Load plugin[{}] with error [{}]", HAWK_EYE_COMMON, type, e.getMessage(), e);
        }

        T instance = instances.stream()
                .min(Comparator.comparing(e -> e.getClass().getAnnotation(SPI.class).order()))
                .orElseThrow(() -> new NullPointerException("Plugin of type[" + type.getName() + "] not found!!!"));

        instance = injectPlugin(instance);
        if (wrap) {
            instance = wrapPlugin(instance);
        }

        return instance;
    }

    private T injectPlugin(T instance) {
        // TODO
        return instance;
    }

    private T wrapPlugin(T instance) {
        // TODO
        return instance;
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
                cl = PluginLoader.class.getClassLoader();
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

    private static boolean hasSpiAnnotation(Class<?> type) {
        return type.isAnnotationPresent(SPI.class);
    }

    private static String spiFullName() {
        return SPI.class.getName();
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
