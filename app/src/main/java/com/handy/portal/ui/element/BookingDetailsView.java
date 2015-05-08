package com.handy.portal.ui.element;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.handy.portal.core.booking.Booking;

/**
 * Created by cdavis on 5/8/15.
 */
public abstract class BookingDetailsView
{
    protected ViewGroup parentViewGroup;

    protected abstract int getLayoutResourceId();
    public void init(Booking booking, ViewGroup parentViewGroup, Context context)
    {
        LayoutInflater.from(context).inflate(getLayoutResourceId(), parentViewGroup);
        this.parentViewGroup = parentViewGroup;
        initFromBooking(booking);
    }
    protected abstract void initFromBooking(Booking booking);
}
