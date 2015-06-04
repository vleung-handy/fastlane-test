package com.handy.portal.ui.element;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.view.ViewGroup;

import com.handy.portal.core.booking.Booking;

/**
 * Created by cdavis on 5/8/15.
 */
public abstract class BookingDetailsViewFragmentContainer extends BookingDetailsView
{
    protected ViewGroup parentViewGroup;
    protected Context applicationContext;
    protected Activity activity;

    protected int getLayoutResourceId() { return 0;}
    protected abstract Class getFragmentClass();

    @Override
    public void init(Booking booking, Bundle arguments, ViewGroup parentViewGroup, Activity activity)
    {
        this.parentViewGroup = parentViewGroup;
        this.activity = activity;
        this.applicationContext = activity.getApplicationContext();

        initFromBooking(booking, arguments);

        //don't inflate anything, will be adding a fragment to the container with fragment manager
        addFragment();
    }

    private void addFragment()
    {
        FragmentManager fragmentManager = activity.getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        Fragment fragment = null;
        try
        {
            fragment = (Fragment) getFragmentClass().newInstance();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        if(fragment != null)
        {
            fragmentTransaction.add(parentViewGroup.getId(), fragment);
            fragmentTransaction.commit();
            fragmentManager.executePendingTransactions(); //otherwise transaction will not resolve immediately
        }

        onFragmentCreated(fragment);
    }

    protected abstract void onFragmentCreated(Fragment fragment);
}
