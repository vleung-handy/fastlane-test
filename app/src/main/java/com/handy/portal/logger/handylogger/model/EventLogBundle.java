package com.handy.portal.logger.handylogger.model;

import com.google.gson.annotations.SerializedName;
import com.handy.portal.core.BaseApplication;

import java.util.List;

public class EventLogBundle
{
    public static final String KEY_EVENT_BUNDLE_ID = "event_bundle_id";

    @SerializedName(KEY_EVENT_BUNDLE_ID)
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

    public String getEventBundleId()
    {
        return mEventBundleId;
    }

    public void addEvent(Event event)
    {
        mEvents.add(event);
    }

    public int size()
    {
        return mEvents.size();
    }

    private String createBundleId()
    {
        return System.currentTimeMillis() + "+" + BaseApplication.getDeviceId();
    }
}
