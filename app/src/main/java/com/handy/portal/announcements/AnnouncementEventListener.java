package com.handy.portal.announcements;

import android.support.annotation.NonNull;

import org.greenrobot.eventbus.Subscribe;

/**
 * currently used by {@link com.handy.portal.core.ui.activity.BaseActivity} as a bus event listener
 * because it is abstract and cannot be registered to the bus
 */
public class AnnouncementEventListener {
    private final AnnouncementsLauncher mAnnouncementsLauncher;

    public AnnouncementEventListener(@NonNull AnnouncementsLauncher announcementsLauncher) {
        mAnnouncementsLauncher = announcementsLauncher;
    }

    @Subscribe
    public void onReceiveShowAnnouncementForTrigger(@NonNull AnnouncementEvent.ShowAnnouncementForTrigger event) {
        mAnnouncementsLauncher.launchAnnouncementForTrigger(event.getTriggerContext());
    }
}
