package com.handy.portal.logger.handylogger.model;

import com.google.gson.annotations.SerializedName;

public abstract class GoogleApiLog extends EventLog
{
    private static final String EVENT_CONTEXT = "google_api";

    public GoogleApiLog(String eventType)
    {
        super(eventType, EVENT_CONTEXT);
    }

    public static class GoogleApiAvailability extends GoogleApiLog
    {
        private static final String EVENT_TYPE = "availability";

        @SerializedName("available")
        private boolean mAvailable;

        public GoogleApiAvailability(boolean available)
        {
            super(EVENT_TYPE);
            mAvailable = available;
        }
    }
}
