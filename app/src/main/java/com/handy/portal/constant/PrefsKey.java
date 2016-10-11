package com.handy.portal.constant;

import android.support.annotation.StringDef;

public interface PrefsKey
{
    String LAST_PROVIDER_ID = "user_credentials_id";
    String AUTH_TOKEN = "user_credentials";
    String ENVIRONMENT = "environment";
    String ENVIRONMENT_PREFIX = "environment_prefix";
    String EVENT_LOG_BUNDLES = "event_log_bundles";
    String LOG_SESSION = "log_session";
    String SAME_DAY_LATE_DISPATCH_AVAILABLE_JOB_NOTIFICATION_EXPLAINED = "same_day_late_dispatch_available_job_notification_explained";
    String APP_FIRST_LAUNCH = "app_first_launch";
    String PROFILE_PHOTO_URL = "profile_photo_url";


    @StringDef({
            LAST_PROVIDER_ID,
            AUTH_TOKEN,
            ENVIRONMENT,
            ENVIRONMENT_PREFIX,
            EVENT_LOG_BUNDLES,
            SAME_DAY_LATE_DISPATCH_AVAILABLE_JOB_NOTIFICATION_EXPLAINED,
            LOG_SESSION,
    })
    @interface Key {}
}
