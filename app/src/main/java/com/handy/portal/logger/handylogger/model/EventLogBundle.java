package com.handy.portal.logger.handylogger.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class EventLogBundle {
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
    public EventLogBundle(int providerId, List<Event> events, String osVersion, String appVersion,
                          String deviceId, String deviceModel, String installationId) {
        mEventBundleId = System.currentTimeMillis() + "+" + deviceId;
        mEvents = events;

        if (providerId > 0) {
            mEventSuperProperties = new EventSuperProperties(providerId, osVersion, appVersion,
                    deviceId, deviceModel, installationId);
        }
        else {
            mEventSuperProperties = new EventSuperPropertiesBase(osVersion, appVersion, deviceId,
                    deviceModel, installationId);
        }
    }

    public String getEventBundleId() {
        return mEventBundleId;
    }

    public void addEvent(Event event) {
        mEvents.add(event);
    }

    public int size() {
        return mEvents.size();
    }
}
