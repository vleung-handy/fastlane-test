package com.handy.portal.deeplink;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;

import com.google.common.collect.ImmutableMap;
import com.handy.portal.core.constant.MainViewPage;

import java.util.HashMap;
import java.util.Map;

public class DeeplinkMapper {
    private static final ImmutableMap<String, MainViewPage> DEEPLINK_MAP;
    public static final String SCHEDULE_AVAILABILITY = "scheduled_jobs/availability";
    public static final String AVAILABLE_JOBS = "available_jobs";

    static {
        final Map<String, MainViewPage> deeplinkMap = new HashMap<>();

        deeplinkMap.put("booking_details", MainViewPage.JOB_DETAILS);
        deeplinkMap.put("more", MainViewPage.AVAILABLE_JOBS);
        deeplinkMap.put(AVAILABLE_JOBS, MainViewPage.AVAILABLE_JOBS);
        deeplinkMap.put(SCHEDULE_AVAILABILITY, MainViewPage.SCHEDULED_JOBS);
        deeplinkMap.put("scheduled_jobs", MainViewPage.SCHEDULED_JOBS);
        deeplinkMap.put("requested_jobs", MainViewPage.CLIENTS);
        deeplinkMap.put("clients", MainViewPage.CLIENTS);
        deeplinkMap.put("conversations", MainViewPage.CLIENTS);

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

    @VisibleForTesting
    public static ImmutableMap<String, MainViewPage> getDeeplinkMap() {
        return DEEPLINK_MAP;
    }

    @Nullable
    public static MainViewPage getPageForDeeplink(@NonNull final String deeplink) {
        return DEEPLINK_MAP.get(deeplink);
    }
}
