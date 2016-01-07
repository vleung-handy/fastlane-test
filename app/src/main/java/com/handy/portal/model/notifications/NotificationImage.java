package com.handy.portal.model.notifications;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class NotificationImage implements Serializable
{
    @SerializedName("scale")
    private Float mScale;

    @SerializedName("url")
    private String mUrl;

    public Float getScale()
    {
        return mScale;
    }

    public String getUrl()
    {
        return mUrl;
    }
}
