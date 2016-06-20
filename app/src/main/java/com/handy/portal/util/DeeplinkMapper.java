package com.handy.portal.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.common.collect.ImmutableMap;
import com.handy.portal.constant.AppPage;

import java.util.HashMap;
import java.util.Map;

public class DeeplinkMapper
{
    private static final ImmutableMap<String, AppPage> DEEPLINK_MAP;

    static
    {
        final Map<String, AppPage> deeplinkMap = new HashMap<>();

        deeplinkMap.put("booking_details", AppPage.JOB_DETAILS);
        deeplinkMap.put("available_jobs", AppPage.AVAILABLE_JOBS);
        deeplinkMap.put("scheduled_jobs", AppPage.SCHEDULED_JOBS);
        deeplinkMap.put("requested_jobs", AppPage.REQUESTED_JOBS);

        deeplinkMap.put("payments", AppPage.PAYMENTS);

        deeplinkMap.put("ratings_and_feedback", AppPage.DASHBOARD);
        deeplinkMap.put("ratings_and_feedback/videos", AppPage.DASHBOARD_VIDEO_LIBRARY);
        deeplinkMap.put("ratings_and_feedback/feedback", AppPage.DASHBOARD_FEEDBACK);

        deeplinkMap.put("refer", AppPage.REFER_A_FRIEND);

        deeplinkMap.put("account_settings", AppPage.ACCOUNT_SETTINGS);
        deeplinkMap.put("account_settings/edit_profile", AppPage.PROFILE_UPDATE);
        deeplinkMap.put("account_settings/edit_payment_method", AppPage.SELECT_PAYMENT_METHOD);
        deeplinkMap.put("account_settings/request_supplies", AppPage.REQUEST_SUPPLIES);

        // TODO: Add more

        DEEPLINK_MAP = ImmutableMap.copyOf(deeplinkMap);
    }

    @Nullable
    public static AppPage getTabForDeeplink(@NonNull final String deeplink)
    {
        return DEEPLINK_MAP.get(deeplink);
    }
}
