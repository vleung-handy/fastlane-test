package com.handy.portal.model.notifications;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class NotificationAction implements Serializable
{
    @SerializedName("type")
    private String mType;

    @SerializedName("deeplink")
    private String mDeeplink;

    @SerializedName("text")
    private String mText;

    public String getType()
    {
        return mType;
    }

    public String getDeeplink()
    {
        return mDeeplink;
    }

    public String getText()
    {
        return mText;
    }
}
