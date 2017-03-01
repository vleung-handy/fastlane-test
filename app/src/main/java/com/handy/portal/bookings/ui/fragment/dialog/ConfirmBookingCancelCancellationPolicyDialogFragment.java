package com.handy.portal.bookings.ui.fragment.dialog;

import android.app.Activity;
import android.os.Bundle;

import com.handy.portal.R;
import com.handy.portal.bookings.model.Booking;
import com.handy.portal.core.constant.BundleKeys;
import com.handy.portal.logger.handylogger.LogEvent;
import com.handy.portal.logger.handylogger.model.ScheduledJobsLog;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

public class ConfirmBookingCancelCancellationPolicyDialogFragment
        extends ConfirmBookingCancellationPolicyDialogFragment {
    public static final String FRAGMENT_TAG =
            ConfirmBookingCancelCancellationPolicyDialogFragment.class.getSimpleName();

    @Inject
    EventBus mBus;

    public static ConfirmBookingCancelCancellationPolicyDialogFragment newInstance(
            final Booking booking
    ) {
        ConfirmBookingCancelCancellationPolicyDialogFragment fragment =
                new ConfirmBookingCancelCancellationPolicyDialogFragment();
        Bundle arguments = new Bundle();
        arguments.putSerializable(BundleKeys.BOOKING, booking);
        arguments.putSerializable(BundleKeys.BOOKING_ACTION,
                booking.getAction(Booking.Action.ACTION_REMOVE));
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void afterShowCancellationPolicyButtonClicked() {
        // do nothing
    }

    @Override
    public void afterViewCreated() {
        onShowCancellationPolicyButtonClicked();
    }

    @Override
    protected void onConfirmActionButtonClicked() {
        if (mAction != null) {
            mBus.post(new LogEvent.AddLogEvent(new ScheduledJobsLog.RemoveJobSubmitted(
                    mBooking,
                    ScheduledJobsLog.RemoveJobLog.CANCELLATION_POLICY,
                    null, //don't have a remove reason
                    mAction.getFeeAmount(),
                    mAction.getWaivedAmount(),
                    mAction.getWarningText()
            )));
        }
        if (getTargetFragment() != null) {
            getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, null);
        }
        dismiss();
    }

    @Override
    protected int getConfirmButtonBackgroundResourceId() {
        return R.drawable.button_red_round;
    }

    @Override
    protected String getConfirmButtonText() {
        return getString(R.string.cancel_job);
    }
}
