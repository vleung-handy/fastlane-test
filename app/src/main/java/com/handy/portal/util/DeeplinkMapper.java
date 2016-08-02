package com.handy.portal.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.common.collect.ImmutableMap;
import com.handy.portal.constant.MainViewPage;

import java.util.HashMap;
import java.util.Map;

public class DeeplinkMapper
{
    private static final ImmutableMap<String, MainViewPage> DEEPLINK_MAP;

    static
    {
        final Map<String, MainViewPage> deeplinkMap = new HashMap<>();

        deeplinkMap.put("booking_details", MainViewPage.JOB_DETAILS);
        deeplinkMap.put("available_jobs", MainViewPage.AVAILABLE_JOBS);
        deeplinkMap.put("scheduled_jobs", MainViewPage.SCHEDULED_JOBS);
        deeplinkMap.put("requested_jobs", MainViewPage.REQUESTED_JOBS);

        deeplinkMap.put("payments", MainViewPage.PAYMENTS);

        deeplinkMap.put("ratings_and_feedback", MainViewPage.DASHBOARD);
        deeplinkMap.put("ratings_and_feedback/videos", MainViewPage.DASHBOARD_VIDEO_LIBRARY);
        deeplinkMap.put("ratings_and_feedback/feedback", MainViewPage.DASHBOARD_FEEDBACK);
        deeplinkMap.put("ratings_and_feedback/tiers", MainViewPage.DASHBOARD_TIERS);

        deeplinkMap.put("refer", MainViewPage.REFER_A_FRIEND);

        deeplinkMap.put("account_settings", MainViewPage.ACCOUNT_SETTINGS);
        deeplinkMap.put("account_settings/edit_profile", MainViewPage.PROFILE_UPDATE);
        deeplinkMap.put("account_settings/edit_payment_method", MainViewPage.SELECT_PAYMENT_METHOD);
        deeplinkMap.put("account_settings/request_supplies", MainViewPage.REQUEST_SUPPLIES);

        // TODO: Add more

        DEEPLINK_MAP = ImmutableMap.copyOf(deeplinkMap);
    }

    @Nullable
    public static MainViewPage getPageForDeeplink(@NonNull final String deeplink)
    {
        return DEEPLINK_MAP.get(deeplink);
    }
}
