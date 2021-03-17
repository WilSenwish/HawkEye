package com.littleyes.common.util;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * <p> <b> LRU 缓存 </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-03-17
 */
public class LRUCache<T> extends LinkedHashMap<String, T> {

    private final int maxCapacity;

    public LRUCache(int maxCapacity) {
        super((maxCapacity << 1), 1F, true);
        this.maxCapacity = maxCapacity;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<String, T> eldest) {
        return size() > maxCapacity;
    }

}