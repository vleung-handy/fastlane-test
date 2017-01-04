package com.handy.portal.core.model;

import com.google.common.collect.Maps;

import java.util.HashMap;
import java.util.Map;

public class TypeSafeMap<K extends Enum>
{
    private Map<String, String> params;

    public TypeSafeMap()
    {
        params = new HashMap<>();
    }

    public TypeSafeMap<K> put(K key, String value)
    {
        params.put(key.toString(), value);
        return this;
    }

    public String get(K key)
    {
        return params.get(key.toString());
    }

    public Map<String, String> toStringMap()
    {
        return Maps.newHashMap(params);
    }
}
