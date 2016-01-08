package com.handy.portal.model.notifications;

import com.google.gson.annotations.SerializedName;
import com.handy.portal.R;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NotificationImage implements Serializable
{
    public static final String NOTIFICATION_ICON_PATTERN_MATCHER = "ic_notification_[^@.]+";

    public static final String NOTIFICATION_GENERIC_MATCHER = "ic_notification_generic";
    public static final String NOTIFICATION_CANCEL_MATCHER = "ic_notification_cancel";

    public static final int NOTIFICATION_GENERIC_DRAWABLE = R.drawable.notification_generic;
    public static final int NOTIFICATION_CANCEL_DRAWABLE = R.drawable.notification_cancel;

    @SerializedName("scale")
    private float mScale;

    @SerializedName("url")
    private String mUrl;

    private Integer mDrawableBackground;

    public float getScale()
    {
        return mScale;
    }

    public String getUrl()
    {
        return mUrl;
    }

    public Integer getDrawableBackground()
    {

        if (mDrawableBackground == null)
        {

            Pattern pattern = Pattern.compile(NOTIFICATION_ICON_PATTERN_MATCHER);
            Matcher matcher = pattern.matcher(getUrl());
            matcher.find();

            switch (matcher.group(0))
            {
                case NOTIFICATION_CANCEL_MATCHER:
                {
                    mDrawableBackground = NOTIFICATION_CANCEL_DRAWABLE;
                }
                break;
                default:
                {
                    mDrawableBackground = NOTIFICATION_GENERIC_DRAWABLE;
                }
                break;
            }
        }

        return mDrawableBackground;
    }
}
