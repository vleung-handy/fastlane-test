package com.handy.portal.announcements;

import com.handy.portal.announcements.model.Announcement;
import com.handy.portal.core.event.HandyEvent;

public abstract class AnnouncementEvent {
    public static class ShowAnnouncementForTrigger extends HandyEvent.RequestEvent {
        private final Announcement.TriggerContext mTriggerContext;

        public ShowAnnouncementForTrigger(Announcement.TriggerContext triggerContext) {
            mTriggerContext = triggerContext;
        }

        Announcement.TriggerContext getTriggerContext() {
            return mTriggerContext;
        }
    }
}
