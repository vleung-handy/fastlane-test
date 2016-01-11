package com.handy.portal.ui.element.notifications;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.model.notifications.NotificationImage;
import com.handy.portal.model.notifications.NotificationMessage;
import com.handy.portal.ui.view.NotificationIconImageView;
import com.handy.portal.util.Utils;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;

public class NotificationsListEntryView extends LinearLayout
{
    @Bind(R.id.notification_icon)
    private NotificationIconImageView mNotificationIcon;

    @Bind(R.id.notification_title)
    private TextView mNotificationTitle;

    @Bind(R.id.notification_body)
    private TextView mNotificationBody;

    @Bind(R.id.notification_time)
    private TextView mNotificationTime;

    public NotificationsListEntryView(Context context)
    {
        super(context);
    }

    public NotificationsListEntryView(Context context, AttributeSet attrs)
    {
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
        setNotificationImage(notificationMessage);
    }

    private void setNotificationImage(NotificationMessage notificationMessage)
    {
        NotificationImage notificationImage = getNotificationImage(notificationMessage);

        if (notificationImage != null)
        {
            Picasso.with(getContext()).load(notificationImage.getUrl()).into(mNotificationIcon);
            mNotificationIcon.setBackground(ContextCompat.getDrawable(getContext(), notificationImage.getDrawableBackground()));
        }
    }

    private NotificationImage getNotificationImage(NotificationMessage notificationMessage)
    {
        if (notificationMessage.getImage() == null && !notificationMessage.hasNoImage())
        {
            float density = getResources().getDisplayMetrics().density;
            if (density <= Utils.MDPI)
            {
                notificationMessage.setImage(Utils.MDPI);
            }
            else if (density == Utils.HDPI)
            {
                notificationMessage.setImage(Utils.HDPI);
            }
            else if (density == Utils.XHDPI)
            {
                notificationMessage.setImage(Utils.XHDPI);
            }
            else
            {
                notificationMessage.setImage(Utils.XXHDPI);
            }
        }

        return notificationMessage.getImage();
    }
}
