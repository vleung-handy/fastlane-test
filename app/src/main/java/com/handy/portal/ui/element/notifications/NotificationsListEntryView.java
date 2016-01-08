package com.handy.portal.ui.element.notifications;

import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.model.notifications.NotificationMessage;

import butterknife.Bind;
import butterknife.ButterKnife;

public class NotificationsListEntryView extends LinearLayout
{
    @Bind(R.id.notification_title)
    protected TextView mNotificationTitle;

    @Bind(R.id.notification_body)
    protected TextView mNotificationBody;

    @Bind(R.id.notification_time)
    protected TextView mNotificationTime;

    public NotificationsListEntryView(Context context) {
        super(context);

    }

    public NotificationsListEntryView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate()
    {
        super.onFinishInflate();
        ButterKnife.bind(this);
    }

    public void updateDisplay(NotificationMessage notificationMessage)
    {
        mNotificationTitle.setText(notificationMessage.getTitle());
        mNotificationBody.setText(Html.fromHtml(notificationMessage.getHtmlBody()));
        mNotificationTime.setText(notificationMessage.getFormattedTime());
    }
}
