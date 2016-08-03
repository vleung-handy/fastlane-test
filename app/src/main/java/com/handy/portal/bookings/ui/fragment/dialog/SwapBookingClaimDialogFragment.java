package com.handy.portal.bookings.ui.fragment.dialog;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handy.portal.R;
import com.handy.portal.bookings.model.Booking;
import com.handy.portal.bookings.ui.element.AvailableBookingElementView;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.logger.handylogger.LogEvent;
import com.handy.portal.logger.handylogger.model.AvailableJobsLog;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

import butterknife.BindView;

public class SwapBookingClaimDialogFragment extends ConfirmBookingActionDialogFragment
{
    @Inject
    EventBus mBus;
    @BindView(R.id.conflicting_jobs)
    ViewGroup mConflictingJobsContainer;

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
    public void onViewCreated(final View view, final Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        initSwappableJob();
        initClaimableJob();
    }

    private void initSwappableJob()
    {
        final AvailableBookingElementView bookingViewMediator = new AvailableBookingElementView();
        bookingViewMediator.initView(getActivity(), mBooking, null, mConflictingJobsContainer);
        final View bookingView = bookingViewMediator.getAssociatedView();
        restyleBookingView(bookingView);
        mConflictingJobsContainer.addView(bookingView);
    }

    private void initClaimableJob()
    {
        final AvailableBookingElementView bookingViewMediator = new AvailableBookingElementView();
        bookingViewMediator.initView(getActivity(), mBooking, null, mConflictingJobsContainer);
        final View bookingView = bookingViewMediator.getAssociatedView();
        restyleBookingView(bookingView);
        mConflictingJobsContainer.addView(bookingView);
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

    private void restyleBookingView(final View bookingView)
    {
        final View serviceText = bookingView.findViewById(R.id.booking_entry_service_text);
        if (serviceText != null)
        {
            serviceText.setVisibility(View.GONE);
        }

        final View leftStrip =
                bookingView.findViewById(R.id.booking_list_entry_left_strip_indicator);
        if (leftStrip != null)
        {
            leftStrip.setVisibility(View.GONE);
        }
    }
}
