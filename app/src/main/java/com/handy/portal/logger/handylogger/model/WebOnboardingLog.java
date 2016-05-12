package com.handy.portal.logger.handylogger.model;

import com.google.gson.annotations.SerializedName;
import com.handy.portal.model.onboarding.OnboardingParams;

public abstract class WebOnboardingLog extends EventLog
{
    @SerializedName("blocking")
    private boolean mBlocking;
    @SerializedName("url")
    private String mUrl;

    private static final String EVENT_CONTEXT = "web_onboarding";

    public WebOnboardingLog(final String eventType, final OnboardingParams onboardingParams)
    {
        super(eventType, EVENT_CONTEXT);
        if (onboardingParams != null)
        {
            mBlocking = onboardingParams.isOnboardingBlocking();
            mUrl = onboardingParams.getOnboardingCompleteWebUrl();
        }
        else
        {
            mBlocking = false;
            mUrl = "";
        }
    }

    public static class Shown extends WebOnboardingLog
    {
        public static final String EVENT_TYPE = "shown";

        public Shown(final OnboardingParams onboardingParams)
        {
            super(EVENT_TYPE, onboardingParams);
        }
    }


    //I the user navigated away from it
    public static class Dismissed extends WebOnboardingLog
    {
        public static final String EVENT_TYPE = "dismissed";

        public Dismissed()
        {
            super(EVENT_TYPE, null);
        }
    }


    //The system closed it because it thought it should
    public static class Closed extends WebOnboardingLog
    {
        public static final String EVENT_TYPE = "closed";

        public Closed()
        {
            super(EVENT_TYPE, null);
        }
    }
}
