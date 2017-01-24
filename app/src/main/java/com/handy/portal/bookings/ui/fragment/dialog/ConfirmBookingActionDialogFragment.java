package com.handy.portal.bookings.ui.fragment.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handy.portal.bookings.model.Booking;
import com.handy.portal.core.constant.BundleKeys;
import com.handy.portal.library.ui.fragment.dialog.ConfirmActionSlideUpDialogFragment;
import com.handy.portal.library.util.Utils;

/**
 * dialog fragment that slides up from the bottom.
 * has a dismiss button and a confirm button
 *
 * shown when we want to confirm a booking action
 */
public abstract class ConfirmBookingActionDialogFragment extends ConfirmActionSlideUpDialogFragment
{
    protected Booking mBooking;

    /**
     *
     * @param inflater
     * @param container
     * @return the view that will be stuffed inside confirm_booking_action_content of this fragment's view
     */
    protected abstract View inflateConfirmActionContentView(LayoutInflater inflater, ViewGroup container);

    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Utils.inject(getActivity(), this);
        mBooking = (Booking) getArguments().getSerializable(BundleKeys.BOOKING); //should not be null
    }

    public boolean cancelDialogOnTouchOutside()
    {
        return true;
    }
}
