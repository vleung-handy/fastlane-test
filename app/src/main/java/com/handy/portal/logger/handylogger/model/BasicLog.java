package com.handy.portal.logger.handylogger.model;

import com.google.gson.annotations.SerializedName;

/*
    Basic app events shuch as app open and navigation change
 */
public class BasicLog extends EventLog
{
    private static final String EVENT_CONTEXT = "app";

    protected BasicLog(final String eventType)
    {
        super(eventType, EVENT_CONTEXT);
    }

    public static class Open extends BasicLog
    {
        private static final String EVENT_TYPE = "open";

        public Open()
        {
            super(EVENT_TYPE);
        }
    }


    public static class Navigation extends BasicLog
    {
        private static final String EVENT_TYPE = "navigation";

        @SerializedName("tab_name")
        private String mTabName;

        public Navigation(final String tabName)
        {
            super(EVENT_TYPE);
            mTabName = tabName;
        }
    }

}
