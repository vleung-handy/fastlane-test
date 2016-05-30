package com.handy.portal.bookings.ui.element;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.handy.portal.bookings.model.Booking;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public abstract class BookingElementView
{
    protected static final DateFormat TIME_OF_DAY_FORMAT =
            new SimpleDateFormat("h:mm a", Locale.getDefault());

    protected View mAssociatedView;

    public abstract View initView(Context parentContext, Booking booking, View convertView, ViewGroup parent);

    public View getAssociatedView()
    {
        return mAssociatedView;
    }
}
