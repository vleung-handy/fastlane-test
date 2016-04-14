package com.handy.portal.notification.model;

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
