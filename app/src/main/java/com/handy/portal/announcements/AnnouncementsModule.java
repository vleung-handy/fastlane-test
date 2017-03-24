package com.handy.portal.announcements;

import android.content.Context;

import com.handy.portal.announcements.ui.AnnouncementCarouselDialogFragment;
import com.handy.portal.core.manager.ConfigManager;
import com.handy.portal.core.manager.PrefsManager;
import com.handy.portal.data.DataManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        library = true,
        complete = false,
        injects = {
                AnnouncementCarouselDialogFragment.class,
        })
public final class AnnouncementsModule {
    @Provides
    @Singleton
    final AnnouncementsManager provideAnnouncementsManager(
            final Context context,
            final DataManager dataManager,
            final PrefsManager prefsManager,
            final ConfigManager configManager) {
        return new AnnouncementsManager(context, dataManager, prefsManager, configManager);
    }
}
