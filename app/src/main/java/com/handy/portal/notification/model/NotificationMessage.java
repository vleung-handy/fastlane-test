package com.handy.portal.notification.model;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;
import com.handy.portal.library.util.DateTimeUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class NotificationMessage implements Serializable {
    @SerializedName("id")
    private int mId;

    @SerializedName("body")
    private String mBody;

    @SerializedName("html_body")
    private String mHtmlBody;

    @SerializedName("type")
    private NotificationType mType;

    @SerializedName("created_at")
    private Date mCreatedAt;

    @SerializedName("expires_at")
    private Date mExpiresAt;

    @SerializedName("available")
    private boolean mAvailable;

    @SerializedName("read_status")
    private boolean mIsRead;

    @SerializedName("interacted_status")
    private boolean mIsInteracted;

    @SerializedName("actions")
    private List<NotificationAction> mActions;

    public int getId() {
        return mId;
    }

    public String getBody() {
        return mBody;
    }

    public String getHtmlBody() {
        return mHtmlBody;
    }

    @NonNull
    public NotificationType getType() {
        return mType != null ? mType : NotificationType.ALERT;
    }

    public Date getCreatedAt() {
        return mCreatedAt;
    }

    public Date getExpiresAt() {
        return mExpiresAt;
    }

    public boolean isAvailable() {
        return mAvailable;
    }

    public boolean isRead() {
        return mIsRead;
    }

    public boolean isInteracted() {
        return mIsInteracted;
    }

    public List<NotificationAction> getActions() {
        return mActions;
    }

    public String getFormattedTime() {
        return DateTimeUtils.formatDateToNumberTimeUnit(getCreatedAt());
    }
}
