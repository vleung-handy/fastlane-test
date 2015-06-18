package com.handy.portal.ui.element;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handy.portal.core.booking.Booking;

import butterknife.ButterKnife;

/**
 * Created by cdavis on 5/8/15.
 */
public abstract class BookingDetailsView
{
    protected ViewGroup parentViewGroup;
    protected Activity activity;

    protected abstract int getLayoutResourceId();

    public void init(Booking booking, Bundle arguments, ViewGroup parentViewGroup, Activity activity)
    {
        this.parentViewGroup = parentViewGroup;
        this.activity = activity;

        LayoutInflater.from(activity).inflate(getLayoutResourceId(), parentViewGroup);

        ButterKnife.inject(this, parentViewGroup);

        initFromBooking(booking, arguments);
    }

    protected abstract void initFromBooking(Booking booking, Bundle arguments);

    protected void removeView()
    {
        parentViewGroup.removeAllViews();
        parentViewGroup.setVisibility(View.GONE);
    }
}
