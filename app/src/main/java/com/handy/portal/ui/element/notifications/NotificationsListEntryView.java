package com.handy.portal.ui.element.notifications;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.model.notifications.NotificationImage;
import com.handy.portal.model.notifications.NotificationMessage;
import com.handy.portal.util.Utils;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;

public class NotificationsListEntryView extends FrameLayout
{
    private static final int[] STATE_READ = {R.attr.state_read};

    @Bind(R.id.notification_icon)
    ImageView mNotificationIcon;

    @Bind(R.id.notification_title)
    TextView mNotificationTitle;

    @Bind(R.id.notification_body)
    TextView mNotificationBody;

    @Bind(R.id.notification_time)
    TextView mNotificationTime;

    private boolean mIsRead = false;

    public NotificationsListEntryView(Context context)
    {
        super(context);
        inflate(getContext(), R.layout.element_notification_list_entry, this);
        ButterKnife.bind(this);
    }

    @Override
    public int[] onCreateDrawableState(int extraSpace)
    {
        final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if (mIsRead)
        {
            mergeDrawableStates(drawableState, STATE_READ);
        }

        return drawableState;
    }

    public void updateDisplay(NotificationMessage notificationMessage)
    {
        mNotificationTitle.setText(notificationMessage.getTitle());
        mNotificationBody.setText(Html.fromHtml(notificationMessage.getHtmlBody()));
        mNotificationTime.setText(notificationMessage.getFormattedTime());
        setNotificationImage(notificationMessage);
    }

    public void setRead(boolean isRead)
    {
        mIsRead = isRead;
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
