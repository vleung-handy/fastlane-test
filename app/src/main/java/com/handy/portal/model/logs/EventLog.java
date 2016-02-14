package com.handy.portal.model.logs;

public abstract class EventLog
{
    private transient String mEventType;
    private transient String mEventContext;

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
