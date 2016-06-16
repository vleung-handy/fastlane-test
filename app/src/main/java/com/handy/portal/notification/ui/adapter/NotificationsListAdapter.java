package com.handy.portal.notification.ui.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.handy.portal.R;
import com.handy.portal.notification.model.NotificationMessage;
import com.handy.portal.notification.ui.view.NotificationsListEntryView;

import java.util.HashSet;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

public class NotificationsListAdapter extends ArrayAdapter<NotificationMessage> implements StickyListHeadersAdapter
{
    private boolean mShouldRequestMoreNotifications = true;
    // Unique store of the notification ids to avoid duplicates
    private HashSet<Integer> mNotificationIds = new HashSet<>();

    public NotificationsListAdapter(Context context)
    {
        super(context, R.layout.element_notification_list_entry, 0);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View v = convertView;
        NotificationMessage notificationMessage = getItem(position);

        if (v == null)
        {
            v = new NotificationsListEntryView(getContext());
        }

        ((NotificationsListEntryView) v).updateDisplay(notificationMessage);

        return v;
    }

    @Override
    public View getHeaderView(final int position, final View convertView, final ViewGroup parent)
    {
        final View view = convertView != null ? convertView : new View(getContext());
        view.setVisibility(View.GONE);
        return view;
    }

    @Override
    public long getHeaderId(final int position)
    {
        return 0;
    }

    public void appendData(final NotificationMessage[] notificationMessages)
    {
        if (notificationMessages.length == 0 || notificationMessages.length < 20)
        {
            mShouldRequestMoreNotifications = false;
        }
        addNotifications(notificationMessages);

        notifyDataSetChanged();
    }

    private void addNotifications(NotificationMessage[] notificationMessages)
    {
        for (NotificationMessage notificationMessage : notificationMessages)
        {
            // Ensure uniqueness of notification feed messages
            if (!mNotificationIds.contains(notificationMessage.getId()))
            {
                mNotificationIds.add(notificationMessage.getId());
                add(notificationMessage);
            }
        }
    }

    public boolean shouldRequestMoreNotifications()
    {
        return mShouldRequestMoreNotifications;
    }

    @Nullable
    public Integer getLastNotificationId()
    {
        return isEmpty() ? null : getItem(getCount() - 1).getId();
    }

    @Nullable
    public Integer getFirstNotificationId()
    {
        return isEmpty() ? null : getItem(0).getId();
    }

    public void reset()
    {
        clear();
        mNotificationIds.clear();
        mShouldRequestMoreNotifications = true;
    }

    public void stopRequestingNotifications()
    {
        mShouldRequestMoreNotifications = false;
    }
}
