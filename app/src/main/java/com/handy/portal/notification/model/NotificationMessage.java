package com.handy.portal.notification.model;

import com.google.gson.annotations.SerializedName;
import com.handy.portal.library.util.DateTimeUtils;

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
    private Date mCreatedAt;

    @SerializedName("expires_at")
    private Date mExpiresAt;

    @SerializedName("available")
    private boolean mAvailable;

    @SerializedName("read_status")
    private boolean mReadStatus;

    @SerializedName("images")
    private List<NotificationImage> mImages;

    @SerializedName("actions")
    private List<NotificationAction> mActions;

    private NotificationImage mImage;
    private boolean mHasNoImage = false;

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

    public Date getCreatedAt()
    {
        return mCreatedAt;
    }

    public Date getExpiresAt()
    {
        return mExpiresAt;
    }

    public boolean isAvailable()
    {
        return mAvailable;
    }

    public boolean isRead()
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
        return DateTimeUtils.formatDateTo12HourClock(getCreatedAt());
    }

    public NotificationImage getImage()
    {
        return mImage;
    }

    public void setImage(float scale)
    {
        for (NotificationImage image : getImages()) {
            if (scale == image.getScale())
            {
                mImage = image;
            }
        }

        if (mImage == null)
        {
            mHasNoImage = true;
        }
    }

    public boolean hasNoImage()
    {
        return mHasNoImage;
    }

    public void markAsRead()
    {
        mReadStatus = true;
    }
}
