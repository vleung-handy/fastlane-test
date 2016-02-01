package com.handy.portal.constant;

import android.support.annotation.StringDef;

// TODO: this should be refactored into static final Strings instead of enum
public interface PrefsKey
{
    String LAST_PROVIDER_ID = "user_credentials_id";
    String AUTH_TOKEN = "user_credentials";
    String ENVIRONMENT_PREFIX = "environment_prefix";
    String EVENT_LOG_BUNDLES = "event_log_bundles";
    String SAME_DAY_LATE_DISPATCH_AVAILABLE_JOB_NOTIFICATION_EXPLAINED = "same_day_late_dispatch_available_job_notification_explained";


    @StringDef({
            LAST_PROVIDER_ID,
            AUTH_TOKEN,
            ENVIRONMENT_PREFIX,
            EVENT_LOG_BUNDLES,
            SAME_DAY_LATE_DISPATCH_AVAILABLE_JOB_NOTIFICATION_EXPLAINED,
    })
    @interface Key {}
}
