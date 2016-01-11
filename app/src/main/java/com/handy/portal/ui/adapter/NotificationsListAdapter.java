package com.handy.portal.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.model.notifications.NotificationMessage;
import com.handy.portal.ui.element.notifications.NotificationsListEntryView;
import com.handy.portal.util.DateTimeUtils;

import java.util.HashMap;
import java.util.HashSet;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

public class NotificationsListAdapter extends ArrayAdapter<NotificationMessage> implements StickyListHeadersAdapter
{
    private boolean mShouldRequestMoreNotifications = true;
    // Key value store of id to position in ListView
    private HashMap<Integer, Integer> mUnreadNotifications = new HashMap<>();
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

        ((NotificationsListEntryView) v).setRead(notificationMessage.isRead());
        v.refreshDrawableState();

        return v;
    }

    @Override
    public View getHeaderView(final int position, final View convertView, final ViewGroup parent)
    {
        View v = convertView;
        NotificationMessage notificationMessage = getItem(position);
        LayoutInflater inflater = LayoutInflater.from(getContext());
        if (v == null)
        {
            v = inflater.inflate(R.layout.element_notification_list_section_header, null);
        }

        String headerText = DateTimeUtils.dayDifferenceInWords(notificationMessage.getCreatedAt());
        headerText += " · ";
        headerText += DateTimeUtils.formatDayOfWeekMonthDateYear(notificationMessage.getCreatedAt());

        ((TextView) v.findViewById(R.id.notification_list_section_header_text)).setText(headerText);
        return v;
    }

    @Override
    public long getHeaderId(final int position)
    {
        NotificationMessage notificationMessage = getItem(position);
        return DateTimeUtils.getBeginningOfDay(notificationMessage.getCreatedAt()).getTime();
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
                if (!notificationMessage.isRead())
                {
                    mUnreadNotifications.put(notificationMessage.getId(), getCount());
                }
                mNotificationIds.add(notificationMessage.getId());
                add(notificationMessage);
            }
        }
    }

    public boolean shouldRequestMoreNotifications()
    {
        return mShouldRequestMoreNotifications;
    }

    public void markNotificationAsRead(NotificationMessage notificationMessage)
    {
        try
        {
            Integer notificationId = notificationMessage.getId();
            notificationMessage = getItem(mUnreadNotifications.get(notificationId));
            notificationMessage.markAsRead();
            mUnreadNotifications.remove(notificationId);
        }
        catch (IndexOutOfBoundsException e) {}
    }

    public Integer getLastNotificationId()
    {
        return isEmpty() ? null : getItem(getCount() - 1).getId();
    }
}
