package com.handy.portal.logger.handylogger.model;

public abstract class EventLog
{
    private transient String mEventType;
    private transient String mEventContext;

    public EventLog(final String eventType, final String eventContext)
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
