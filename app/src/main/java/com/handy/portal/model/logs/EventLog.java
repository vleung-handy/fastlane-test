package com.handy.portal.model.logs;

import com.google.gson.annotations.SerializedName;

public abstract class EventLog
{
    @SerializedName("event_type")
    private String mEventType;
    @SerializedName("event_context")
    private String mEventContext;

    public EventLog(String providerId, String versionTrack, String eventType, String eventContext)
    {
        mEventType = eventType;
        mEventContext = eventContext;
    }

    public String getEventName()
    {
        return mEventContext + "_" + mEventType;
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
