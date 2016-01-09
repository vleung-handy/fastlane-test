package com.handy.portal.event;

import com.handy.portal.data.DataManager;
import com.handy.portal.model.notifications.NotificationMessage;

import java.util.ArrayList;

public class NotificationEvent extends HandyEvent
{
    public static class RequestNotificationMessages extends RequestEvent
    {
        public final Integer sinceId;
        public final Integer untilId;
        public final Integer count;

        public RequestNotificationMessages(Integer sinceId, Integer untilId, Integer count)
        {
            this.sinceId = sinceId;
            this.untilId = untilId;
            this.count = count;
        }
    }

    public static class ReceiveNotificationMessagesSuccess extends ReceiveSuccessEvent
    {
        private final NotificationMessage[] notificationMessages;

        public NotificationMessage[] getNotificationMessages()
        {
            return notificationMessages;
        }


        public ReceiveNotificationMessagesSuccess(NotificationMessage[] notificationMessages)
        {
            this.notificationMessages = notificationMessages;
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
        public final ArrayList<Integer> notificationIds;

        public RequestMarkNotificationsAsRead(ArrayList<Integer> notificationIds)
        {
            this.notificationIds = notificationIds;
        }
    }

    public static class ReceiveMarkNotificationsAsReadSuccess extends ReceiveSuccessEvent
    {
        private final NotificationMessage[] notificationMessages;

        public NotificationMessage[] getNotificationMessages()
        {
            return notificationMessages;
        }


        public ReceiveMarkNotificationsAsReadSuccess(NotificationMessage[] notificationMessages)
        {
            this.notificationMessages = notificationMessages;
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
