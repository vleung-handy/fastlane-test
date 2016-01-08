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

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

public class NotificationsListAdapter extends ArrayAdapter<NotificationMessage> implements StickyListHeadersAdapter
{
    private boolean mShouldRequestMoreNotifications = true;

    public NotificationsListAdapter(Context context)
    {
        super(context, R.layout.element_notification_list_entry, 0);
    }

    public void appendData(final NotificationMessage[] notificationMessages)
    {
        addAll(notificationMessages);
        if (notificationMessages.length == 0 || notificationMessages.length < 20)
        {
            mShouldRequestMoreNotifications = false;
        }
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View v = convertView;
        NotificationMessage notificationMessage = getItem(position);
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (v == null)
        {
            v = inflater.inflate(R.layout.element_notification_list_entry, null);
        }

        ((NotificationsListEntryView) v).updateDisplay(notificationMessage);

        return v;
    }

    @Override
    public View getHeaderView(final int position, final View convertView, final ViewGroup parent)
    {
        View v = convertView;
        NotificationMessage notificationMessage = getItem(position);
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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

    public boolean shouldRequestMoreNotifications()
    {
        return mShouldRequestMoreNotifications;
    }
}
