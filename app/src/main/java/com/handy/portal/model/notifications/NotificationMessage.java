package com.handy.portal.model.notifications;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class NotificationMessage implements Serializable
{
    @SerializedName("id")
    private int mId;

    @SerializedName("title")
    private String mTitle;

    @SerializedName("body")
    private String mBody;

    @SerializedName("html_body")
    private String mHtmlBody;

    @SerializedName("type")
    private String mType;

    @SerializedName("created_at")
    private String mCreatedAt;

    @SerializedName("expires_at")
    private String mExpiresAt;

    @SerializedName("available")
    private boolean mAvailable;

    @SerializedName("read_status")
    private boolean mReadStatus;

    @SerializedName("images")
    private List<NotificationImage> mImages;

    @SerializedName("actions")
    private List<NotificationAction> mActions;

    public int getId()
    {
        return mId;
    }

    public String getTitle()
    {
        return mTitle;
    }

    public String getBody()
    {
        return mBody;
    }

    public String getHtmlBody()
    {
        return mHtmlBody;
    }

    public String getType()
    {
        return mType;
    }

    public String getCreatedAt()
    {
        return mCreatedAt;
    }

    public String getExpiresAt()
    {
        return mExpiresAt;
    }

    public boolean isAvailable()
    {
        return mAvailable;
    }

    public boolean isReadStatus()
    {
        return mReadStatus;
    }

    public List<NotificationImage> getImages()
    {
        return mImages;
    }

    public List<NotificationAction> getActions()
    {
        return mActions;
    }

    public String getFormattedTime()
    {
        return "alskdf";
    }
}
