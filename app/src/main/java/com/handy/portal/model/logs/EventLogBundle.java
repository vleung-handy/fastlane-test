package com.handy.portal.model.logs;

import com.google.gson.annotations.SerializedName;
import com.handy.portal.core.BaseApplication;

import java.util.List;

public class EventLogBundle
{
    @SerializedName("event_bundle_id")
    private String mEventBundleId;
    @SerializedName("events")
    private List<EventLog> mEventLogs;
    @SerializedName("event_bundle_sent_timestamp")
    private long mSentTimestampSecs;
    @SerializedName("super_properties")
    private EventSuperProperties mEventSuperProperties;

    public EventLogBundle(final int providerId, final List<EventLog> eventLogs)
    {
        mEventBundleId = createBundleId();
        mEventLogs = eventLogs;
        mEventSuperProperties = new EventSuperProperties(providerId);
    }

    public void add(EventLog eventLog)
    {
        mEventLogs.add(eventLog);
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
