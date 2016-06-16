package com.handy.portal.notification.ui.view;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.library.util.Utils;
import com.handy.portal.notification.model.NotificationImage;
import com.handy.portal.notification.model.NotificationMessage;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;

public class NotificationsListEntryView extends FrameLayout
{
    private static final int[] STATE_INTERACTED = {R.attr.state_interacted};

    @Bind(R.id.notification_container)
    ViewGroup mNotificationContainer;

    @Bind(R.id.notification_icon)
    ImageView mNotificationIcon;

    @Bind(R.id.notification_body)
    TextView mNotificationBody;

    @Bind(R.id.notification_time)
    TextView mNotificationTime;

    private boolean mIsInteracted = false;
    private NotificationMessage mNotificationBackground;

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
        if (mIsInteracted)
        {
            mergeDrawableStates(drawableState, STATE_INTERACTED);
        }

        return drawableState;
    }

    public void updateDisplay(NotificationMessage notificationMessage)
    {
        setNotificationBackground(notificationMessage);
        setNotificationText(notificationMessage);
        setNotificationImage(notificationMessage);
    }

    public void setInteracted(boolean isInteracted)
    {
        mIsInteracted = isInteracted;
    }

    public void setNotificationBackground(final NotificationMessage notificationMessage)
    {
        if (notificationMessage.isInteracted())
        {
            mNotificationContainer.setBackgroundResource(R.color.handy_bg);
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
