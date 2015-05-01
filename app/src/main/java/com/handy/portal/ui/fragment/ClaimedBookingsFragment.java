package com.handy.portal.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handy.portal.R;

import butterknife.ButterKnife;


/**
 * A placeholder fragment containing a simple view.
 */
public class ClaimedBookingsFragment extends InjectedFragment {

    public ClaimedBookingsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_claimed_bookings, null);
        ButterKnife.inject(this, view);
        return view;
    }
}
