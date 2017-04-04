package com.handy.portal.logger.handylogger.model;

import com.google.gson.annotations.SerializedName;

public abstract class AnnouncementsLog extends EventLog {
    private static final String EVENT_CONTEXT = "pro_announcement";

    @SerializedName("message_id")
    private String mAnnouncementId;

    public AnnouncementsLog(String eventType, String announcementId) {
        super(eventType, EVENT_CONTEXT);
        mAnnouncementId = announcementId;
    }

    public static class Shown extends AnnouncementsLog {
        private static final String EVENT_TYPE = "shown";

        public Shown(final String announcementId) {
            super(EVENT_TYPE, announcementId);
        }
    }

    public static class Dismissed extends AnnouncementsLog {
        private static final String EVENT_TYPE = "dismissed";

        @SerializedName("time_seen_milliseconds")
        private final long mDurationAnnouncementShownMs;

        public Dismissed(final String announcementId, final long durationAnnouncementShownMs) {
            super(EVENT_TYPE, announcementId);
            mDurationAnnouncementShownMs = durationAnnouncementShownMs;
        }
    }

}
