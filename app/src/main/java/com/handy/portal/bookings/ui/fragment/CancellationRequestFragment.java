package com.handy.portal.bookings.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.bookings.model.Booking;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.constant.MainViewPage;
import com.handy.portal.constant.PrefsKey;
import com.handy.portal.constant.TransitionStyle;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.event.NavigationEvent;
import com.handy.portal.logger.handylogger.LogEvent;
import com.handy.portal.logger.handylogger.model.ScheduledJobsLog;
import com.handy.portal.manager.PrefsManager;
import com.handy.portal.ui.fragment.ActionBarFragment;
import com.handy.portal.library.util.CurrencyUtils;
import com.handy.portal.library.util.DateTimeUtils;
import com.handy.portal.library.util.UIUtils;
import org.greenrobot.eventbus.Subscribe;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CancellationRequestFragment extends ActionBarFragment
{
    @Inject
    PrefsManager mPrefsManager;

    @Bind(R.id.cancellation_address)
    TextView mAddressTextView;
    @Bind(R.id.cancellation_date)
    TextView mDateTextView;
    @Bind(R.id.cancellation_time)
    TextView mTimeTextView;
    @Bind(R.id.cancellation_fee_amount)
    TextView mFeeAmountTextView;
    @Bind(R.id.cancellation_reasons)
    RadioGroup mReasonsRadioGroup;

    private Booking mBooking;
    private Booking.Action mAction;

    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setOptionsMenuEnabled(true);
        setActionBar(R.string.cancellation_request, false);
        mBooking = (Booking) getArguments().getSerializable(BundleKeys.BOOKING);
        mAction = (Booking.Action) getArguments().getSerializable(BundleKeys.BOOKING_ACTION);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        final View view = inflater.inflate(R.layout.fragment_cancellation_request, container, false);
        bus.post(new LogEvent.AddLogEvent(new ScheduledJobsLog.RemoveJobConfirmationShown(
                mBooking,
                ScheduledJobsLog.RemoveJobLog.REASON_FLOW,
                mAction.getFeeAmount(),
                mAction.getWarningText()
        )));
        return view;
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState)
    {
        ButterKnife.bind(this, view);
        init();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.menu_x_back, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_exit:
                onBackButtonPressed();
                return true;
            default:
                return false;
        }
    }

    @OnClick(R.id.cancellation_confirm_button)
    public void cancelBooking()
    {
        final String selectedReason = getSelectedReason();
        if (selectedReason == null)
        {
            showToast(R.string.select_a_reason);
        }
        else
        {
            bus.post(new LogEvent.AddLogEvent(new ScheduledJobsLog.RemoveJobSubmitted(
                    mBooking,
                    ScheduledJobsLog.RemoveJobLog.REASON_FLOW,
                    selectedReason,
                    mAction.getFeeAmount(),
                    mAction.getWarningText()
            )));
            bus.post(new HandyEvent.SetLoadingOverlayVisibility(true));
            bus.post(new HandyEvent.RequestRemoveJob(mBooking));
        }
    }

    @Subscribe
    public void onReceiveRemoveJobSuccess(final HandyEvent.ReceiveRemoveJobSuccess event)
    {
        if (event.booking.getId().equals(mBooking.getId()))
        {
            bus.post(new LogEvent.AddLogEvent(new ScheduledJobsLog.RemoveJobSuccess(
                    mBooking,
                    ScheduledJobsLog.RemoveJobLog.REASON_FLOW,
                    getSelectedReason(),
                    mAction.getFeeAmount(),
                    mAction.getWarningText()
            )));
            bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
            TransitionStyle transitionStyle = TransitionStyle.JOB_REMOVE_SUCCESS;
            Bundle arguments = new Bundle();
            arguments.putLong(BundleKeys.DATE_EPOCH_TIME, event.booking.getStartDate().getTime());
            //Return to available jobs on that day
            bus.post(new NavigationEvent.NavigateToPage(MainViewPage.SCHEDULED_JOBS, arguments, transitionStyle));
        }
    }

    @Subscribe
    public void onReceiveRemoveJobError(final HandyEvent.ReceiveRemoveJobError event)
    {
        String errorMessage = event.error.getMessage();
        if (errorMessage == null)
        {
            errorMessage = getString(R.string.job_remove_error_generic);
        }
        bus.post(new LogEvent.AddLogEvent(new ScheduledJobsLog.RemoveJobError(
                mBooking,
                ScheduledJobsLog.RemoveJobLog.REASON_FLOW,
                getSelectedReason(),
                mAction.getFeeAmount(),
                mAction.getWarningText(),
                errorMessage
        )));
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        showToast(errorMessage);
    }

    private void init()
    {
        String providerId = mPrefsManager.getString(PrefsKey.LAST_PROVIDER_ID);
        Booking.BookingStatus bookingStatus = mBooking.inferBookingStatus(providerId);
        String address = mBooking.getFormattedLocation(bookingStatus);
        mAddressTextView.setText(address);

        mDateTextView.setText(DateTimeUtils.formatDateDayOfWeekMonthDay(mBooking.getStartDate()));

        String startTime = DateTimeUtils.CLOCK_FORMATTER_12HR.format(mBooking.getStartDate());
        String endTime = DateTimeUtils.CLOCK_FORMATTER_12HR.format(mBooking.getEndDate());
        mTimeTextView.setText(getString(R.string.time_interval_formatted, startTime, endTime));

        mFeeAmountTextView.setText(getString(R.string.fee_formatted,
                CurrencyUtils.formatPriceWithCents(mAction.getFeeAmount(),
                        mBooking.getPaymentToProvider().getCurrencySymbol())));

        for (String reason : mAction.getRemoveReasons())
        {
            RadioButton radioButton = (RadioButton) LayoutInflater.from(getContext())
                    .inflate(R.layout.radio_button_cancel_reason, mReasonsRadioGroup, false);
            radioButton.setText(reason);
            mReasonsRadioGroup.addView(radioButton);
        }
    }

    @Nullable
    public String getSelectedReason()
    {
        final RadioButton reasonButton = UIUtils.getCheckedRadioButton(mReasonsRadioGroup);
        if (reasonButton != null)
        {
            return reasonButton.getText().toString();
        }
        return null;
    }
}
