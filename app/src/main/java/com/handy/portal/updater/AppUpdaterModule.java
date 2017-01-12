package com.handy.portal.updater;

import android.content.Context;

import com.handy.portal.core.BuildConfigWrapper;
import com.handy.portal.core.manager.PrefsManager;
import com.handy.portal.data.DataManager;
import com.handy.portal.updater.ui.PleaseUpdateActivity;
import com.handy.portal.updater.ui.PleaseUpdateFragment;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        library = true,
        complete = false,
        injects = {
                PleaseUpdateActivity.class,
                PleaseUpdateFragment.class,
        })
public final class AppUpdaterModule
{
    @Provides
    @Singleton
    final VersionManager provideVersionManager(final Context context,
                                               final EventBus bus,
                                               final DataManager dataManager,
                                               final PrefsManager prefsManager,
                                               final BuildConfigWrapper buildConfigWrapper)
    {
        return new VersionManager(context, bus, dataManager, prefsManager, buildConfigWrapper);
    }
}
