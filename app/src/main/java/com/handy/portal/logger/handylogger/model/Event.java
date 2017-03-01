package com.handy.portal.logger.handylogger.model;

import com.google.gson.annotations.SerializedName;

import java.util.UUID;

public class Event {
    @SerializedName("event_timestamp_ms")
    private long mTimestampMillis;
    @SerializedName("event_timestamp")
    private long mTimestampSecs;
    @SerializedName("event_id")
    private String mId;
    @SerializedName("event_type")
    private String mEventType;
    @SerializedName("event_context")
    private String mEventContext;
    @SerializedName("properties")
    private EventLog mEventLog;

    public Event(final EventLog eventLog, long sessionId, int sessionEventCount) {
        mTimestampMillis = System.currentTimeMillis();
        mTimestampSecs = mTimestampMillis / 1000;
        mId = UUID.randomUUID().toString();
        mEventType = eventLog.getEventType();
        mEventContext = eventLog.getEventContext();
        mEventLog = eventLog;
        mEventLog.setSessionId(sessionId);
        mEventLog.setSessionEventCount(sessionEventCount);
    }

    public String getEventType() {
        return mEventType;
    }

    public String getEventContext() {
        return mEventContext;
    }

    public int getSessionEventCount() {
        return mEventLog.getSessionEventCount();
    }

    public long getSessionId() {
        return mEventLog.getSessionId();
    }

    public void setEventType(String eventType) {
        mEventType = eventType;
    }

    public void setSessionEventCount(final int sessionEventCount) {
        mEventLog.setSessionEventCount(sessionEventCount);
    }

    public void setSessionId(final long sessionId) {
        mEventLog.setSessionId(sessionId);
    }

    public long getTimestampMillis() {
        return mTimestampMillis;
    }

    public long getTimestampSecs() {
        return mTimestampSecs;
    }

    public String getId() {
        return mId;
    }
}
