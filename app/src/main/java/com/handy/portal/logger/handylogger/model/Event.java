package com.handy.portal.logger.handylogger.model;

import com.google.gson.annotations.SerializedName;

public class Event
{
    @SerializedName("event_timestamp")
    private long mTimestampSecs;
    @SerializedName("event_type")
    private String mEventType;
    @SerializedName("event_context")
    private String mEventContext;
    @SerializedName("properties")
    private EventLog mEventLog;

    public Event(final EventLog eventLog)
    {
        mTimestampSecs = System.currentTimeMillis() / 1000;
        mEventType = eventLog.getEventType();
        mEventContext = eventLog.getEventContext();
        mEventLog = eventLog;
    }

    public String getEventType()
    {
        return mEventType;
    }

    public String getEventContext()
    {
        return mEventContext;
    }
}
