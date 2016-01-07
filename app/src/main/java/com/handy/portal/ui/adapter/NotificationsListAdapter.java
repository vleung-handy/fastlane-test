package com.handy.portal.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.model.notifications.NotificationMessage;

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
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
        {
            convertView = inflater.inflate(R.layout.element_notification_list_entry, null);
        }

        return convertView;
    }

    @Override
    public View getHeaderView(final int position, final View convertView, final ViewGroup parent)
    {
        View v = convertView;
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (v == null)
        {
            v = inflater.inflate(R.layout.element_payment_list_year_section_header, null);
        }

        ((TextView) v.findViewById(R.id.payment_year)).setText("2015");
        return v;
    }

    @Override
    public long getHeaderId(final int position)
    {
        return 0;
    }

    public boolean shouldRequestMoreNotifications()
    {
        return mShouldRequestMoreNotifications;
    }
}
