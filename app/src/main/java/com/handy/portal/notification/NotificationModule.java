package com.handy.portal.notification;

import com.handy.portal.data.DataManager;
import com.handy.portal.manager.PrefsManager;
import com.handy.portal.notification.ui.fragment.NotificationBlockerDialogFragment;
import com.handy.portal.notification.ui.fragment.NotificationsFragment;
import com.handy.portal.notification.ui.view.NotificationsListEntryView;
import com.handy.portal.notification.ui.view.NotificationsListView;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        library = true,
        complete = false,
        injects = {
                NotificationsFragment.class,
                NotificationsListView.class,
                NotificationsListEntryView.class,
                NotificationBlockerDialogFragment.class,

        })
public final class NotificationModule
{
    @Provides
    @Singleton
    final NotificationMessageManager provideNotificationMessageManager(final EventBus bus, final DataManager dataManager, final PrefsManager prefsManager)
    {
        return new NotificationMessageManager(bus, dataManager, prefsManager);
    }
}
