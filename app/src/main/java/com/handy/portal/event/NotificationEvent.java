package com.handy.portal.event;

import com.handy.portal.data.DataManager;
import com.handy.portal.model.notifications.NotificationMessage;

import java.util.ArrayList;

public abstract class NotificationEvent extends HandyEvent
{
    public static class RequestNotificationMessages extends RequestEvent
    {
        private final Integer mSinceId;
        private final Integer mUntilId;
        private final Integer mCount;

        public RequestNotificationMessages(Integer sinceId, Integer untilId, Integer count)
        {
            mSinceId = sinceId;
            mUntilId = untilId;
            mCount = count;
        }

        public Integer getSinceId()
        {
            return mSinceId;
        }

        public Integer getUntilId()
        {
            return mUntilId;
        }

        public Integer getCount()
        {
            return mCount;
        }
    }

    public static class ReceiveNotificationMessagesSuccess extends ReceiveSuccessEvent
    {
        private final NotificationMessage[] mNotificationMessages;

        public NotificationMessage[] getNotificationMessages()
        {
            return mNotificationMessages;
        }


        public ReceiveNotificationMessagesSuccess(NotificationMessage[] notificationMessages)
        {
            mNotificationMessages = notificationMessages;
        }
    }

    public static class ReceiveNotificationMessagesError extends ReceiveErrorEvent
    {
        public ReceiveNotificationMessagesError(DataManager.DataManagerError error)
        {
            this.error = error;
        }
    }

    public static class RequestMarkNotificationsAsRead extends RequestEvent
    {
        private final ArrayList<Integer> mNotificationIds;

        public RequestMarkNotificationsAsRead(ArrayList<Integer> notificationIds)
        {
            mNotificationIds = notificationIds;
        }

        public ArrayList<Integer> getNotificationIds()
        {
            return mNotificationIds;
        }
    }

    public static class ReceiveMarkNotificationsAsReadSuccess extends ReceiveSuccessEvent
    {
        private final NotificationMessage[] mNotificationMessages;

        public ReceiveMarkNotificationsAsReadSuccess(NotificationMessage[] notificationMessages)
        {
            mNotificationMessages = notificationMessages;
        }

        public NotificationMessage[] getNotificationMessages()
        {
            return mNotificationMessages;
        }
    }

    public static class ReceiveMarkNotificationsAsReadError extends ReceiveErrorEvent
    {
        public ReceiveMarkNotificationsAsReadError(DataManager.DataManagerError error)
        {
            this.error = error;
        }
    }
}
