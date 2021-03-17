package com.littleyes.common.util;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * <p> <b> LRU 缓存 </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-03-17
 */
public class LRUCache<T> {

    private final LinkedHashMap<String, T> caches;

    public LRUCache(int maxCapacity) {
        this.caches = new LinkedHashMap<String, T>((maxCapacity << 1), 1F, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, T> eldest) {
                return size() > maxCapacity;
            }
        };
    }

    public synchronized T put(String key, T value) {
        return caches.put(key, value);
    }

    public synchronized T get(String key) {
        return caches.get(key);
    }

}