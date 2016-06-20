package com.handy.portal.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.common.collect.ImmutableMap;
import com.handy.portal.constant.MainViewTab;

import java.util.HashMap;
import java.util.Map;

public class DeeplinkMapper
{
    private static final ImmutableMap<String, MainViewTab> DEEPLINK_MAP;

    static
    {
        final Map<String, MainViewTab> deeplinkMap = new HashMap<>();

        deeplinkMap.put("booking_details", MainViewTab.JOB_DETAILS);
        deeplinkMap.put("available_jobs", MainViewTab.AVAILABLE_JOBS);
        deeplinkMap.put("scheduled_jobs", MainViewTab.SCHEDULED_JOBS);

        deeplinkMap.put("payments", MainViewTab.PAYMENTS);

        deeplinkMap.put("ratings_and_feedback", MainViewTab.DASHBOARD);
        deeplinkMap.put("ratings_and_feedback/videos", MainViewTab.DASHBOARD_VIDEO_LIBRARY);
        deeplinkMap.put("ratings_and_feedback/feedback", MainViewTab.DASHBOARD_FEEDBACK);

        deeplinkMap.put("refer", MainViewTab.REFER_A_FRIEND);

        deeplinkMap.put("account_settings", MainViewTab.ACCOUNT_SETTINGS);
        deeplinkMap.put("account_settings/edit_profile", MainViewTab.PROFILE_UPDATE);
        deeplinkMap.put("account_settings/edit_payment_method", MainViewTab.SELECT_PAYMENT_METHOD);
        deeplinkMap.put("account_settings/request_supplies", MainViewTab.REQUEST_SUPPLIES);

        deeplinkMap.put("requested_jobs", MainViewTab.REQUESTED_JOBS); //TODO: verify the schema

        // TODO: Add more

        DEEPLINK_MAP = ImmutableMap.copyOf(deeplinkMap);
    }

    @Nullable
    public static MainViewTab getTabForDeeplink(@NonNull final String deeplink)
    {
        return DEEPLINK_MAP.get(deeplink);
    }
}
