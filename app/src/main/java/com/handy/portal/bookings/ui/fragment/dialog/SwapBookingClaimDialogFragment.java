package com.handy.portal.bookings.ui.fragment.dialog;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handy.portal.R;
import com.handy.portal.bookings.model.Booking;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.logger.handylogger.LogEvent;
import com.handy.portal.logger.handylogger.model.AvailableJobsLog;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

public class SwapBookingClaimDialogFragment extends ConfirmBookingActionDialogFragment
{
    @Inject
    EventBus mBus;

    public static final String FRAGMENT_TAG = SwapBookingClaimDialogFragment.class.getName();

    public static SwapBookingClaimDialogFragment newInstance(final Booking booking)
    {
        final SwapBookingClaimDialogFragment dialogFragment = new SwapBookingClaimDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(BundleKeys.BOOKING, booking);
        dialogFragment.setArguments(bundle);
        return dialogFragment;
    }

    @Override
    protected View inflateBookingActionContentView(final LayoutInflater inflater,
                                                   final ViewGroup container)
    {
        return inflater.inflate(R.layout.layout_confirm_booking_swap, container, false);
    }

    @Override
    protected void onConfirmBookingActionButtonClicked()
    {

        final Intent intent = new Intent();
        intent.putExtra(BundleKeys.BOOKING, mBooking);
        if (getTargetFragment() != null)
        {
            getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK,
                    intent);
        }
        mBus.post(new LogEvent.AddLogEvent(new AvailableJobsLog.ConfirmSwitchSubmitted()));
        dismiss();
    }

    @Override
    protected int getConfirmButtonBackgroundResourceId()
    {
        return R.drawable.button_green_round;
    }

    @Override
    protected String getConfirmButtonText()
    {
        return getString(R.string.confirm_switch);
    }
}
