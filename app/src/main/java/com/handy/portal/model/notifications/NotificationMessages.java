package com.handy.portal.model.notifications;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class NotificationMessages implements Serializable
{
    @SerializedName("notifications")
    private NotificationMessage[] mNotificationMessages;

    public NotificationMessage[] getList()
    {
        return mNotificationMessages;
    }
}
