package com.handy.portal.constant;

import android.support.annotation.StringDef;

// TODO: this should be refactored into static final Strings instead of enum
public interface PrefsKey
{
    String LAST_PROVIDER_ID = "user_credentials_id";
    String AUTH_TOKEN = "user_credentials";
    String ONBOARDING_COMPLETED = "onboarding_completed";
    String ONBOARDING_NEEDED = "onboarding_needed";
    String ENVIRONMENT_PREFIX = "environment_prefix";
    String EVENT_LOG_BUNDLES = "event_log_bundles";
    String APP_FIRST_OPEN = "app_first_open";


    @StringDef({
            LAST_PROVIDER_ID,
            AUTH_TOKEN,
            ONBOARDING_COMPLETED,
            ONBOARDING_NEEDED,
            ENVIRONMENT_PREFIX,
            EVENT_LOG_BUNDLES,
            APP_FIRST_OPEN,
    })
    @interface Key {}
}
