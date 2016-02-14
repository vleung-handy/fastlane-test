package com.handy.portal.model.logs;

import com.google.gson.annotations.SerializedName;
import com.handy.portal.core.BaseApplication;

import java.util.List;

public class EventLogBundle
{
    @SerializedName("event_bundle_id")
    private String mEventBundleId;
    @SerializedName("events")
    private List<Event> mEvents;
    @SerializedName("event_bundle_sent_timestamp")
    private long mSentTimestampSecs;
    @SerializedName("super_properties")
    private EventSuperProperties mEventSuperProperties;

    public EventLogBundle(final int providerId, final List<Event> events)
    {
        mEventBundleId = createBundleId();
        mEvents = events;
        mEventSuperProperties = new EventSuperProperties(providerId);
    }

    public void prepareForSending()
    {
        mSentTimestampSecs = System.currentTimeMillis() / 1000;
    }

    private String createBundleId()
    {
        return System.currentTimeMillis() + "+" + BaseApplication.getDeviceId();
    }
}
