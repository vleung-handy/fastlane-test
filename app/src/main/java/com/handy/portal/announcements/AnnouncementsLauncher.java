package com.handy.portal.announcements;

import android.support.annotation.Nullable;

import com.handy.portal.announcements.model.Announcement;

/**
 * currently implemented by BaseActivity
 * and invoked by {@link AnnouncementEventListener}
 * <p/>
 * launches the UI stuff related to announcements
 */
public interface AnnouncementsLauncher {
    void launchAnnouncementForTrigger(@Nullable Announcement.TriggerContext triggerContext);
}
