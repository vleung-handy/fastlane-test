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
    @SerializedName("super_properties")
    private EventSuperProperties mEventSuperProperties;

    public EventLogBundle(final int providerId, final List<Event> events)
    {
        mEventBundleId = createBundleId();
        mEvents = events;
        mEventSuperProperties = new EventSuperProperties(providerId);
    }

    private String createBundleId()
    {
        return System.currentTimeMillis() + "+" + BaseApplication.getDeviceId();
    }
}
