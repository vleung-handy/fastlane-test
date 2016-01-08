package com.handy.portal.model.notifications;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class NotificationImage implements Serializable
{
    @SerializedName("scale")
    private float mScale;

    @SerializedName("url")
    private String mUrl;

    public float getScale()
    {
        return mScale;
    }

    public String getUrl()
    {
        return mUrl;
    }
}
