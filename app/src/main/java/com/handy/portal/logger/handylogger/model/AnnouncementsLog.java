package com.handy.portal.logger.handylogger.model;

import com.google.gson.annotations.SerializedName;

public abstract class AnnouncementsLog extends EventLog {
    private static final String EVENT_CONTEXT = "pro_announcement_screen";

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

    public static class SkipTapped extends AnnouncementsLog {
        private static final String EVENT_TYPE = "skip_tapped";

        public SkipTapped(final String announcementId) {
            super(EVENT_TYPE, announcementId);
        }
    }

}
