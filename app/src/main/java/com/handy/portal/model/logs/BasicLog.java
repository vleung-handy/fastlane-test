package com.handy.portal.model.logs;

import com.google.gson.annotations.SerializedName;

/*
    Basic app events shuch as app open and navigation change
 */
public class BasicLog extends EventLog
{
    private static final String EVENT_CONTEXT = "app";

    protected BasicLog(
            String providerId, String versionTrack, String eventType)
    {
        super(providerId, versionTrack, eventType, EVENT_CONTEXT);
    }

    public static class Open extends BasicLog
    {
        private static final String EVENT_TYPE = "open";

        public Open(String providerId, String versionTrack)
        {
            super(providerId, versionTrack, EVENT_TYPE);
        }
    }

    public static class Navigation extends BasicLog
    {
        private static final String EVENT_TYPE = "navigation";

        @SerializedName("tab_name")
        private String mTabName;

        public Navigation(String providerId, String versionTrack, String tabName)
        {
            super(providerId, versionTrack, EVENT_TYPE);
            mTabName = tabName;
        }
    }

}
