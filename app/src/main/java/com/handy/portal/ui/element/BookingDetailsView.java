package com.handy.portal.ui.element;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.handy.portal.core.booking.Booking;

import butterknife.ButterKnife;

/**
 * Created by cdavis on 5/8/15.
 */
public abstract class BookingDetailsView
{
    protected ViewGroup parentViewGroup;
    protected Context applicationContext;
    protected Activity activity;

    protected abstract int getLayoutResourceId();

    public void init(Booking booking, Bundle arguments, ViewGroup parentViewGroup, Activity activity)
    {
        //System.out.println("Init booking details view with resource id " + Integer.toString(getLayoutResourceId()));
        //System.out.println("to parent " + parentViewGroup.toString());

        this.parentViewGroup = parentViewGroup;
        this.activity = activity;
        this.applicationContext = activity.getApplicationContext();

        LayoutInflater.from(applicationContext).inflate(getLayoutResourceId(), parentViewGroup);

        ButterKnife.inject(this, parentViewGroup);

        initFromBooking(booking, arguments);
    }

    protected abstract void initFromBooking(Booking booking, Bundle arguments);
}
