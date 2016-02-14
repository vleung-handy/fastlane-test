package com.handy.portal.model.logs;

import com.google.gson.annotations.SerializedName;

public class Event extends EventSuperProperties
{
    @SerializedName("event_timestamp")
    private long mTimestampSecs;
    @SerializedName("event_type")
    private String mEventType;
    @SerializedName("event_context")
    private String mEventContext;
    @SerializedName("properties")
    private EventLog mEventLog;

    public Event(
            final int providerId,
            final EventLog eventLog
    )
    {
        super(providerId);
        mTimestampSecs = System.currentTimeMillis() / 1000;
        mEventType = eventLog.getEventType();
        mEventContext = eventLog.getEventContext();
        mEventLog = eventLog;
    }
}
