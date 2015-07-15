package com.handy.portal.ui.element;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handy.portal.model.Booking;

import java.util.List;

import butterknife.ButterKnife;

public abstract class BookingDetailsViewConstructor
{
    protected ViewGroup parentViewGroup;
    protected Activity activity;

    protected abstract int getLayoutResourceId();

    public void constructView(Booking booking, List<Booking.Action> allowedActions, Bundle arguments, ViewGroup parentViewGroup, Activity activity)
    {
        this.parentViewGroup = parentViewGroup;
        this.activity = activity;

        LayoutInflater.from(activity).inflate(getLayoutResourceId(), parentViewGroup);

        ButterKnife.inject(this, parentViewGroup);

        constructViewFromBooking(booking, allowedActions, arguments);
    }

    protected abstract void constructViewFromBooking(Booking booking, List<Booking.Action> allowedActions, Bundle arguments);

    protected void removeView()
    {
        parentViewGroup.removeAllViews();
        parentViewGroup.setVisibility(View.GONE);
    }
}
