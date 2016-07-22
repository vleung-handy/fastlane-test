package com.handy.portal.notification;

import com.handy.portal.constant.PrefsKey;
import com.handy.portal.data.DataManager;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.event.NotificationEvent;
import com.handy.portal.manager.HandyConnectivityManager;
import com.handy.portal.manager.PrefsManager;
import com.handy.portal.notification.model.NotificationMessage;
import com.handy.portal.notification.model.NotificationMessages;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import javax.inject.Inject;

public class NotificationMessageManager
{
    private static final String UNREAD_COUNT_KEY = "unread_count";

    private final EventBus mBus;
    private final DataManager mDataManager;
    private final PrefsManager mPrefsManager;
    private final HandyConnectivityManager mConnectivityManager;

    //LinkedHashSet maintains ordering
    //notificationmessages establish equality off of id
    //this acts as a cache without an automatic expiry
    //private LinkedHashSet<NotificationMessage> mAllCachedNotificationMessages;

    //going to be manually updating dupes with new data so don't need set to dedupe things
    private LinkedHashMap<Integer, NotificationMessage> mAllCachedNotificationMessages;

    @Inject
    public NotificationMessageManager(final EventBus bus, final DataManager dataManager, final PrefsManager prefsManager, final HandyConnectivityManager connectivityManager)
    {
        mBus = bus;
        mPrefsManager = prefsManager;
        mBus.register(this);
        mDataManager = dataManager;
        mAllCachedNotificationMessages = new LinkedHashMap<>();
        mConnectivityManager = connectivityManager;
    }

    @Subscribe
    public void onRequestNotificationMessages(final NotificationEvent.RequestNotificationMessages event)
    {
        if (mConnectivityManager.hasConnectivity())
        {
            String providerId = mPrefsManager.getString(PrefsKey.LAST_PROVIDER_ID);
            mDataManager.getNotifications(providerId, event.getSinceId(), event.getUntilId(), event.getCount(), new DataManager.Callback<NotificationMessages>()
            {
                @Override
                public void onSuccess(final NotificationMessages notificationMessages)
                {
                    mBus.post(new NotificationEvent.ReceiveNotificationMessagesSuccess(notificationMessages.getList()));
                    updateMessagesCache(notificationMessages);
                }

                @Override
                public void onError(DataManager.DataManagerError error)
                {
                    mBus.post(new NotificationEvent.ReceiveNotificationMessagesError(error));
                }
            });
        }
        else
        {
            //Offline mode, use whatever we have cached
            pruneCacheForExpiredEntries();
            NotificationMessage[] cachedMessages = mAllCachedNotificationMessages.values().toArray(new NotificationMessage[mAllCachedNotificationMessages.size()]);
            mBus.post(new NotificationEvent.ReceiveNotificationMessagesCacheSuccess(cachedMessages));
        }
    }

    @Subscribe
    public void onUserLoggedOut(final HandyEvent.UserLoggedOut event)
    {
        invalidateCaches();
    }

    //Don't display messages that are expired, the offline cache should not show more information than a live connection would
    private void pruneCacheForExpiredEntries()
    {
        Date currentDate = new Date();
        //respect message expiry even for offline caches
        List<NotificationMessage> messagesToRemove = new ArrayList<>();
        for (NotificationMessage message : mAllCachedNotificationMessages.values())
        {
            //a message has expired if its expiry is in the past
            if (message.getExpiresAt().compareTo(currentDate) < 0)
            {
                messagesToRemove.add(message);
            }
        }

        for (NotificationMessage message : messagesToRemove)
        {
            mAllCachedNotificationMessages.remove(message.getId());
        }
    }

    private void invalidateCaches()
    {
        mAllCachedNotificationMessages.clear();
    }

    private void updateMessagesCache(final NotificationMessages notificationMessages)
    {
        for (NotificationMessage message : notificationMessages.getList())
        {
            mAllCachedNotificationMessages.put(message.getId(), message);
        }
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

    @Subscribe
    public void onRequestMarkNotificationsAsInteracted(
            final NotificationEvent.RequestMarkNotificationsAsInteracted event)
    {
        String providerId = mPrefsManager.getString(PrefsKey.LAST_PROVIDER_ID);
        mDataManager.postMarkNotificationsAsInteracted(providerId, event.getNotificationIds(), new DataManager.Callback<NotificationMessages>()
        {
            @Override
            public void onSuccess(final NotificationMessages notificationMessages)
            {
                mBus.post(new NotificationEvent.ReceiveMarkNotificationsAsInteractedSuccess(notificationMessages.getList()));
            }

            @Override
            public void onError(final DataManager.DataManagerError error)
            {
                mBus.post(new NotificationEvent.ReceiveMarkNotificationsAsInteractedError(error));
            }
        });
    }

    @Subscribe
    public void onRequestUnreadCount(final NotificationEvent.RequestUnreadCount event)
    {
        String providerId = mPrefsManager.getString(PrefsKey.LAST_PROVIDER_ID);
        if (providerId != null)
        {
            mDataManager.getNotificationsUnreadCount(providerId, new DataManager.Callback<HashMap<String, Object>>()
            {
                @Override
                public void onSuccess(final HashMap<String, Object> response)
                {
                    int unreadCount = (int) ((double) response.get(UNREAD_COUNT_KEY));
                    mBus.post(new NotificationEvent.ReceiveUnreadCountSuccess(unreadCount));
                }

                @Override
                public void onError(final DataManager.DataManagerError error) {}
            });
        }
    }
}
