package com.handy.portal.ui.element;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.handy.portal.core.booking.Booking;

public abstract class BookingElementView
{
    protected static final String DATE_FORMAT = "h:mm a";

    public View associatedView;

    public abstract View initView(Context parentContext, Booking booking, View convertView, ViewGroup parent);
}