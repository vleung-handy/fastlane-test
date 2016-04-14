package com.handy.portal.ui.fragment.bookings;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.crashlytics.android.Crashlytics;
import com.handy.portal.R;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.constant.MainViewTab;
import com.handy.portal.model.Booking;
import com.handy.portal.ui.element.bookings.BookingView;
import com.handy.portal.ui.fragment.ActionBarFragment;

import butterknife.Bind;
import butterknife.ButterKnife;


public class CheckoutJobDetailsFragment extends ActionBarFragment
{
    @Bind(R.id.booking_details_view)
    BookingView mBookingDetailsView;

    private Booking mBooking;

    @Override
    protected MainViewTab getTab()
    {
        return MainViewTab.CHECKOUT_JOB_DETAILS;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null)
        {
            mBooking = (Booking) bundle.getSerializable(BundleKeys.BOOKING);
        }
        else
        {
            Crashlytics.log("Booking missing for Checkout Job Details");
        }
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_checkout_job_details, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        mBookingDetailsView.setDisplay(mBooking, null, null, null, false, false);
        mBookingDetailsView.hideButtons();

        setOptionsMenuEnabled(true);
        setBackButtonEnabled(true);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        mBookingDetailsView.registerBus();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        mBookingDetailsView.unregisterBus();
    }
}
