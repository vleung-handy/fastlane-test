package com.handy.portal.util;

import java.util.Map;

/**
 * A utility class that contains methods to check if things are valid
 */
public class ValidationUtils
{
    /**
     * @param key
     * @param map
     * @return true if given key, map and map.get(key) are non-null
     */
    public static boolean isMapKeyEntryValid(final Object key, final Map<?, ?> map)
    {
        return key != null && map != null && map.get(key) != null;
    }

    public static boolean isNullOrEmpty(final String string)
    {
        return string == null || string.isEmpty();
    }
}
