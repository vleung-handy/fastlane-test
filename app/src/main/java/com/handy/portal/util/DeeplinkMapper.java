package com.handy.portal.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.common.collect.ImmutableMap;
import com.handy.portal.constant.DeeplinkConstants;
import com.handy.portal.constant.MainViewTab;

import java.util.HashMap;
import java.util.Map;

public class DeeplinkMapper
{
    private static final ImmutableMap<String, MainViewTab> DEEPLINK_MAP;
    static
    {
        final Map<String, MainViewTab> deeplinkMap = new HashMap<>();
        deeplinkMap.put(DeeplinkConstants.DEEPLINK_BOOKING_DETAILS, MainViewTab.DETAILS);
        DEEPLINK_MAP = ImmutableMap.copyOf(deeplinkMap);
    }

    @Nullable
    public static MainViewTab getTabForDeeplink(@NonNull final String deeplink)
    {
        return DEEPLINK_MAP.get(deeplink);
    }
}
