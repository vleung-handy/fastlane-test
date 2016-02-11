package com.handy.portal.model.logs;

import com.google.gson.annotations.SerializedName;

public abstract class EventLog
{
    @SerializedName("timestamp")
    private long mTimestampSecs;
    @SerializedName("provider_id")
    private int mProviderId;
    @SerializedName("version_track")
    private String mVersionTrack;
    @SerializedName("event_type")
    private String mEventType;
    @SerializedName("event_context")
    private String mEventContext;

    public EventLog(String providerId, String versionTrack, String eventType, String eventContext)
    {
        mTimestampSecs = System.currentTimeMillis() / 1000;
        mProviderId = providerId != null && !providerId.isEmpty() ? Integer.parseInt(providerId) : 0;
        mVersionTrack = versionTrack;
        mEventType = eventType;
        mEventContext = eventContext;
    }

    public String getEventName()
    {
        return mEventContext + "_" + mEventType;
    }

}
