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
import com.handy.portal.bookings.manager.BookingManager;
import com.handy.portal.bookings.model.Booking;
import com.handy.portal.core.constant.BundleKeys;
import com.handy.portal.core.constant.MainViewPage;
import com.handy.portal.core.constant.PrefsKey;
import com.handy.portal.core.constant.TransitionStyle;
import com.handy.portal.core.event.HandyEvent;
import com.handy.portal.core.manager.PageNavigationManager;
import com.handy.portal.core.manager.PrefsManager;
import com.handy.portal.core.ui.fragment.ActionBarFragment;
import com.handy.portal.library.util.CurrencyUtils;
import com.handy.portal.library.util.DateTimeUtils;
import com.handy.portal.library.util.UIUtils;
import com.handy.portal.logger.handylogger.model.ScheduledJobsLog;

import org.greenrobot.eventbus.Subscribe;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CancellationRequestFragment extends ActionBarFragment {
    @Inject
    PrefsManager mPrefsManager;
    @Inject
    BookingManager mBookingManager;
    @Inject
    PageNavigationManager mNavigationManager;

    @BindView(R.id.cancellation_address)
    TextView mAddressTextView;
    @BindView(R.id.cancellation_date)
    TextView mDateTextView;
    @BindView(R.id.cancellation_time)
    TextView mTimeTextView;
    @BindView(R.id.cancellation_fee_amount)
    TextView mFeeAmountTextView;
    @BindView(R.id.cancellation_reasons)
    RadioGroup mReasonsRadioGroup;

    private Booking mBooking;
    private Booking.Action mAction;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setOptionsMenuEnabled(true);
        setActionBar(R.string.cancellation_request, false);
        mBooking = (Booking) getArguments().getSerializable(BundleKeys.BOOKING);
        mAction = (Booking.Action) getArguments().getSerializable(BundleKeys.BOOKING_ACTION);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_cancellation_request, container, false);
        bus.post(new ScheduledJobsLog.RemoveJobConfirmationShown(
                mBooking,
                ScheduledJobsLog.RemoveJobLog.REASON_FLOW,
                mAction.getFeeAmount(),
                mAction.getWaivedAmount(),
                mAction.getWarningText()
        ));
        return view;
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        init();
    }

    @Override
    public void onResume() {
        super.onResume();
        bus.register(this);
    }

    @Override
    public void onPause() {
        bus.unregister(this);
        super.onPause();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_x_back, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_exit:
                onBackButtonPressed();
                return true;
            default:
                return false;
        }
    }

    @OnClick(R.id.cancellation_confirm_button)
    public void cancelBooking() {
        final String selectedReason = getSelectedReason();
        if (selectedReason == null) {
            showToast(R.string.select_a_reason);
        }
        else {
            bus.post(new ScheduledJobsLog.RemoveJobSubmitted(
                    mBooking,
                    ScheduledJobsLog.RemoveJobLog.REASON_FLOW,
                    selectedReason,
                    mAction.getFeeAmount(),
                    mAction.getWaivedAmount(),
                    mAction.getWarningText()
            ));
            bus.post(new HandyEvent.SetLoadingOverlayVisibility(true));
            mBookingManager.requestRemoveJob(mBooking);
        }
    }

    @Subscribe
    public void onReceiveRemoveJobSuccess(final HandyEvent.ReceiveRemoveJobSuccess event) {
        if (event.booking.getId().equals(mBooking.getId())) {
            bus.post(new ScheduledJobsLog.RemoveJobSuccess(
                    mBooking,
                    ScheduledJobsLog.RemoveJobLog.REASON_FLOW,
                    getSelectedReason(),
                    mAction.getFeeAmount(),
                    mAction.getWaivedAmount(),
                    mAction.getWarningText()
            ));
            bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
            TransitionStyle transitionStyle = TransitionStyle.JOB_REMOVE_SUCCESS;
            Bundle arguments = new Bundle();
            arguments.putLong(BundleKeys.DATE_EPOCH_TIME, event.booking.getStartDate().getTime());
            //Return to available jobs on that day
            mNavigationManager.navigateToPage(getActivity().getSupportFragmentManager(),
                    MainViewPage.SCHEDULED_JOBS, arguments, transitionStyle, false);
        }
    }

    @Subscribe
    public void onReceiveRemoveJobError(final HandyEvent.ReceiveRemoveJobError event) {
        String errorMessage = event.error.getMessage();
        if (errorMessage == null) {
            errorMessage = getString(R.string.job_remove_error_generic);
        }
        bus.post(new ScheduledJobsLog.RemoveJobError(
                mBooking,
                ScheduledJobsLog.RemoveJobLog.REASON_FLOW,
                getSelectedReason(),
                mAction.getFeeAmount(),
                mAction.getWaivedAmount(),
                mAction.getWarningText(),
                errorMessage
        ));
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        showToast(errorMessage);
    }

    private void init() {
        String providerId = mPrefsManager.getSecureString(PrefsKey.LAST_PROVIDER_ID);
        Booking.BookingStatus bookingStatus = mBooking.inferBookingStatus(providerId);
        String address = mBooking.getFormattedLocation(bookingStatus);
        mAddressTextView.setText(address);

        mDateTextView.setText(DateTimeUtils.formatDateDayOfWeekMonthDay(mBooking.getStartDate()));

        String startTime = DateTimeUtils.CLOCK_FORMATTER_12HR.format(mBooking.getStartDate());
        String endTime = DateTimeUtils.CLOCK_FORMATTER_12HR.format(mBooking.getEndDate());
        mTimeTextView.setText(getString(R.string.time_interval_formatted, startTime, endTime));

        mFeeAmountTextView.setText(getString(R.string.fee_formatted,
                CurrencyUtils.formatPriceWithCents(mAction.getFeeAmount(),
                        mBooking.getCurrencySymbol())));

        for (String reason : mAction.getRemoveReasons()) {
            RadioButton radioButton = (RadioButton) LayoutInflater.from(getContext())
                    .inflate(R.layout.radio_button_cancel_reason, mReasonsRadioGroup, false);
            radioButton.setText(reason);
            mReasonsRadioGroup.addView(radioButton);
        }
    }

    @Nullable
    public String getSelectedReason() {
        final RadioButton reasonButton = UIUtils.getCheckedRadioButton(mReasonsRadioGroup);
        if (reasonButton != null) {
            return reasonButton.getText().toString();
        }
        return null;
    }
}
