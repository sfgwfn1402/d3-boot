package com.dddframework.core.utils;

import lombok.experimental.UtilityClass;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 用于对象映射，按biz隔离
 */
@UtilityClass
public final class MappingKit {
    // Bean容器
    private final Map<String, Map<Object, Object>> BEAN_MAPPINGS = new ConcurrentHashMap<>();

    public <K, V> void map(String biz, K key, V value) {
        Map<Object, Object> mappings = BEAN_MAPPINGS.get(biz);
        if (mappings == null) {
            mappings = new ConcurrentHashMap<>();
            BEAN_MAPPINGS.put(biz, mappings);
        }
        mappings.put(key, value);
    }

    public <K, V> V get(String field, K source) {
        Map<Object, Object> mappings = BEAN_MAPPINGS.get(field);
        if (mappings == null) return null;
        return (V) mappings.get(source);
    }

}