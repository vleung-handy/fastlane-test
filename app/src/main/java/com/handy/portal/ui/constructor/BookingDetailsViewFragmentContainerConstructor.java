package com.handy.portal.ui.constructor;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.ViewGroup;

import com.handy.portal.model.Booking;
import com.handy.portal.ui.activity.BaseActivity;

public abstract class BookingDetailsViewFragmentContainerConstructor extends BookingDetailsViewConstructor
{
    public BookingDetailsViewFragmentContainerConstructor(@NonNull Context context, Bundle arguments)
    {
        super(context, arguments);
    }

    protected int getLayoutResourceId()
    {
        return 0;
    }

    protected abstract Class getFragmentClass();

    @Override
    public void create(ViewGroup container, Booking booking)
    {
        constructView(container, booking);
        addFragment(container);
    }

    private void addFragment(ViewGroup container)
    {
        FragmentManager fragmentManager = ((BaseActivity) getContext()).getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        Fragment fragment = null;
        try
        {
            fragment = (Fragment) getFragmentClass().newInstance();
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        if (fragment != null)
        {
            fragmentTransaction.add(container.getId(), fragment);
            fragmentTransaction.commit();
            fragmentManager.executePendingTransactions();
        }

        onFragmentCreated(fragment);
    }

    protected abstract void onFragmentCreated(Fragment fragment);
}
