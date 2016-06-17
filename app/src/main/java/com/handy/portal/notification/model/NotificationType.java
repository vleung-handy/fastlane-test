package com.handy.portal.notification.model;

import android.support.annotation.DrawableRes;

import com.google.gson.annotations.SerializedName;
import com.handy.portal.R;

public enum NotificationType
{
    @SerializedName("notification")
    ALERT(R.drawable.ic_notification_alert, R.drawable.ic_notification_alert_gray),
    @SerializedName("payment")
    PAYMENT(R.drawable.ic_notification_payment, R.drawable.ic_notification_payment_gray),
    @SerializedName("cancellation")
    CANCELLATION(R.drawable.ic_notification_cancel, R.drawable.ic_notification_cancel_gray),
    @SerializedName("pro_team")
    PRO_TEAM(R.drawable.ic_notification_pro_team, R.drawable.ic_notification_pro_team_gray),
    @SerializedName("performance")
    PERFORMANCE(R.drawable.ic_notification_performance, R.drawable.ic_notification_performance_gray),
    @SerializedName("job")
    JOB(R.drawable.ic_notification_job, R.drawable.ic_notification_job_gray),;

    private int mInactiveIconResId;
    private int mActiveIconResId;

    NotificationType(@DrawableRes final int activeIconResId,
                     @DrawableRes final int inactiveIconResId)
    {
        mActiveIconResId = activeIconResId;
        mInactiveIconResId = inactiveIconResId;
    }

    public int getInactiveIconResId()
    {
        return mInactiveIconResId;
    }

    public int getActiveIconResId()
    {
        return mActiveIconResId;
    }
}
