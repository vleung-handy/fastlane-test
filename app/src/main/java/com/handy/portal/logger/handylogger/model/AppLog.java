package com.handy.portal.logger.handylogger.model;

import com.google.gson.annotations.SerializedName;

/*
    Basic app events such as app open and navigation change
 */
public class AppLog extends EventLog
{
    private static final String EVENT_CONTEXT = "app";

    protected AppLog(final String eventType)
    {
        super(eventType, EVENT_CONTEXT);
    }

    public static class AppOpenLog extends AppLog
    {
        private static final String EVENT_TYPE = "open";

        @SerializedName("first_launch")
        private boolean mFirstLaunch;

        public AppOpenLog(final boolean firstLaunch)
        {
            super(EVENT_TYPE);
            mFirstLaunch = firstLaunch;
        }

    }


    public static class Navigation extends AppLog
    {
        private static final String EVENT_TYPE = "navigation";

        @SerializedName("page_name")
        private String mPageName;

        public Navigation(final String pageName)
        {
            super(EVENT_TYPE);
            mPageName = pageName;
        }
    }

}
