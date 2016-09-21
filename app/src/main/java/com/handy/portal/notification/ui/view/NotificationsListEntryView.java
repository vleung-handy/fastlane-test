package com.handy.portal.notification.ui.view;

import android.content.Context;
import android.text.Html;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.notification.model.NotificationMessage;
import com.handy.portal.notification.model.NotificationType;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NotificationsListEntryView extends FrameLayout
{
    @BindView(R.id.notification_container)
    ViewGroup mNotificationContainer;

    @BindView(R.id.notification_icon)
    ImageView mNotificationIcon;

    @BindView(R.id.notification_body)
    TextView mNotificationBody;

    @BindView(R.id.notification_time)
    TextView mNotificationTime;

    public NotificationsListEntryView(Context context)
    {
        super(context);
        inflate(getContext(), R.layout.element_notification_list_entry, this);
        ButterKnife.bind(this);
    }

    public void updateDisplay(NotificationMessage notificationMessage)
    {
        setNotificationBackground(notificationMessage);
        setNotificationText(notificationMessage);
        setNotificationImage(notificationMessage);
    }

    public void setNotificationBackground(final NotificationMessage notificationMessage)
    {
        if (notificationMessage.isInteracted())
        {
            mNotificationContainer.setBackgroundResource(R.color.handy_bg);
        }
        else
        {
            mNotificationContainer.setBackgroundResource(R.drawable.button_white);
        }
    }

    private void setNotificationText(final NotificationMessage notificationMessage)
    {
        if (notificationMessage.isInteracted())
        {
            mNotificationBody.setText(Html.fromHtml(notificationMessage.getBody()));
        }
        else
        {
            mNotificationBody.setText(Html.fromHtml(notificationMessage.getHtmlBody()));
        }
        mNotificationTime.setText(notificationMessage.getFormattedTime());
    }

    private void setNotificationImage(final NotificationMessage notificationMessage)
    {
        final NotificationType notificationType = notificationMessage.getType();
        if (notificationMessage.isInteracted())
        {
            mNotificationIcon.setImageResource(notificationType.getInactiveIconResId());
        }
        else
        {
            mNotificationIcon.setImageResource(notificationType.getActiveIconResId());
        }
    }
}
