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
    private EventSuperPropertiesBase mEventSuperProperties;

    /**
     * If providerId greater then 0, then providerId will be part of super properties
     *
     * @param providerId
     * @param events
     */
    public EventLogBundle(final int providerId, final List<Event> events)
    {
        mEventBundleId = createBundleId();
        mEvents = events;

        if (providerId > 0)
        {
            mEventSuperProperties = new EventSuperProperties(providerId);
        }
        else
        {
            mEventSuperProperties = new EventSuperPropertiesBase();
        }
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
