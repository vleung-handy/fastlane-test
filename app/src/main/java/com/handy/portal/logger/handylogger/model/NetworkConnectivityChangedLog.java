package com.handy.portal.logger.handylogger.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public abstract class NetworkConnectivityChangedLog extends EventLog
{
    private static final String EVENT_CONTEXT = "network_connectivity_changed";

    @SerializedName("captured_at_date")
    private final Date mCapturedAtDate;

    public NetworkConnectivityChangedLog(final String eventType, Date capturedAtDate)
    {
        super(eventType, EVENT_CONTEXT);
        mCapturedAtDate = capturedAtDate;
    }

    public static class Reconnected extends NetworkConnectivityChangedLog
    {
        private static final String EVENT_TYPE = "reconnected";

        public Reconnected(Date capturedAtDate)
        {
            super(EVENT_TYPE, capturedAtDate);
        }
    }

    public static class Disconnected extends NetworkConnectivityChangedLog
    {
        private static final String EVENT_TYPE = "disconnected";

        public Disconnected(Date capturedAtDate)
        {
            super(EVENT_TYPE, capturedAtDate);
        }
    }
}
