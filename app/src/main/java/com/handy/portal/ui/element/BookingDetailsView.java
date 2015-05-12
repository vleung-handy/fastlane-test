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
    protected Context context;

    protected abstract int getLayoutResourceId();
    public void init(Booking booking, ViewGroup parentViewGroup, Context context)
    {
        //System.out.println("Init booking details view with resource id " + Integer.toString(getLayoutResourceId()));
        //System.out.println("to parent " + parentViewGroup.toString());

        this.parentViewGroup = parentViewGroup;
        this.context = context;

        LayoutInflater.from(context).inflate(getLayoutResourceId(), parentViewGroup);

        initFromBooking(booking);
    }

    protected abstract void initFromBooking(Booking booking);
}
