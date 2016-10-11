package com.handy.portal.logger.handylogger.model;

import com.google.gson.annotations.SerializedName;

public abstract class EventLog
{
    //These are handled in the Event object
    private transient String mEventType;
    private transient String mEventContext;

    @SerializedName("session_event_count")
    private int mSessionEventCount;
    @SerializedName("session_id")
    private long mSessionId;

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

    public int getSessionEventCount()
    {
        return mSessionEventCount;
    }

    public void setSessionEventCount(final int sessionEventCount)
    {
        mSessionEventCount = sessionEventCount;
    }

    public long getSessionId()
    {
        return mSessionId;
    }

    public void setSessionId(final long sessionId)
    {
        mSessionId = sessionId;
    }
}
