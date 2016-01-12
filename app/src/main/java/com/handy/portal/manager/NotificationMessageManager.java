package com.handy.portal.manager;

import com.handy.portal.constant.PrefsKey;
import com.handy.portal.data.DataManager;
import com.handy.portal.event.NotificationEvent;
import com.handy.portal.model.notifications.NotificationMessages;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

public class NotificationMessageManager
{
    private final Bus mBus;
    private final DataManager mDataManager;
    private final PrefsManager mPrefsManager;

    @Inject
    public NotificationMessageManager(final Bus bus, final DataManager dataManager, final PrefsManager prefsManager)
    {
        mBus = bus;
        mPrefsManager = prefsManager;
        mBus.register(this);
        mDataManager = dataManager;
    }

    @Subscribe
    public void onRequestNotificationMessages(final NotificationEvent.RequestNotificationMessages event)
    {
        String providerId = mPrefsManager.getString(PrefsKey.LAST_PROVIDER_ID);
        mDataManager.getNotifications(providerId, event.getSinceId(), event.getUntilId(), event.getCount(), new DataManager.Callback<NotificationMessages>()
        {
            @Override
            public void onSuccess(final NotificationMessages notificationMessages)
            {
                mBus.post(new NotificationEvent.ReceiveNotificationMessagesSuccess(notificationMessages.getList()));
            }

            @Override
            public void onError(DataManager.DataManagerError error)
            {
                mBus.post(new NotificationEvent.ReceiveNotificationMessagesError(error));
            }
        });
    }

    @Subscribe
    public void onRequestMarkNotificationsAsRead(final NotificationEvent.RequestMarkNotificationsAsRead event)
    {
        String providerId = mPrefsManager.getString(PrefsKey.LAST_PROVIDER_ID);
        mDataManager.postMarkNotificationsAsRead(providerId, event.getNotificationIds(), new DataManager.Callback<NotificationMessages>()
        {
            @Override
            public void onSuccess(final NotificationMessages notificationMessages)
            {
                mBus.post(new NotificationEvent.ReceiveMarkNotificationsAsReadSuccess(notificationMessages.getList()));
            }

            @Override
            public void onError(final DataManager.DataManagerError error)
            {
                mBus.post(new NotificationEvent.ReceiveMarkNotificationsAsReadError(error));
            }
        });
    }
}