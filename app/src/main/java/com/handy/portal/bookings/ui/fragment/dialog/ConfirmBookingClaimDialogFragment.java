package com.handy.portal.bookings.ui.fragment.dialog;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.crashlytics.android.Crashlytics;
import com.handy.portal.R;
import com.handy.portal.bookings.model.Booking;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.logger.handylogger.LogEvent;
import com.handy.portal.logger.handylogger.model.AvailableJobsLog;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

public class ConfirmBookingClaimDialogFragment
        extends ConfirmBookingCancellationPolicyDialogFragment
{
    @Inject
    EventBus mBus;

    public static final String FRAGMENT_TAG = ConfirmBookingClaimDialogFragment.class.getName();

    public static ConfirmBookingClaimDialogFragment newInstance(@NonNull Booking booking)
    {
        ConfirmBookingClaimDialogFragment fragment = new ConfirmBookingClaimDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(BundleKeys.BOOKING, booking);
        args.putSerializable(BundleKeys.BOOKING_ACTION, booking.getAction(Booking.Action.ACTION_CLAIM));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getConfirmButtonBackgroundResourceId()
    {
        return R.drawable.button_green_round;
    }

    @Override
    protected String getConfirmButtonText()
    {
        return getString(R.string.confirm_claim);
    }

    @Override
    protected void onConfirmBookingActionButtonClicked()
    {
        Intent intent = new Intent();
        intent.putExtra(BundleKeys.BOOKING, mBooking);
        if (getTargetFragment() != null)
        {
            getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
        }
        else
        {
            Crashlytics.logException(new Exception("getTargetFragment() is null for confirm booking claim dialog fragment"));
        }
        mBus.post(new LogEvent.AddLogEvent(new AvailableJobsLog.ConfirmClaimConfirmed()));
        dismiss();
    }

    @Override
    public void afterShowCancellationPolicyButtonClicked()
    {
        mBus.post(new LogEvent.AddLogEvent(new AvailableJobsLog.ConfirmClaimDetailsShown()));
    }

    @Override
    public void afterViewCreated()
    {
        mBus.post(new LogEvent.AddLogEvent(new AvailableJobsLog.ConfirmClaimShown()));
    }
}
