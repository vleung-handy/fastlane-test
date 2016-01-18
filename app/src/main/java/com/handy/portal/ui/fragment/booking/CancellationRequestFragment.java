package com.handy.portal.ui.fragment.booking;

import android.os.Bundle;
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
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.constant.MainViewTab;
import com.handy.portal.constant.TransitionStyle;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.event.LogEvent;
import com.handy.portal.model.Booking;
import com.handy.portal.ui.fragment.ActionBarFragment;
import com.handy.portal.util.CurrencyUtils;
import com.handy.portal.util.DateTimeUtils;
import com.handy.portal.util.UIUtils;
import com.squareup.otto.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CancellationRequestFragment extends ActionBarFragment
{
    @Bind(R.id.cancellation_address)
    TextView mAddressTextView;
    @Bind(R.id.cancellation_date)
    TextView mDateTextView;
    @Bind(R.id.cancellation_time)
    TextView mTimeTextView;
    @Bind(R.id.cancellation_withholding_amount)
    TextView mWithholdingAmountTextView;
    @Bind(R.id.cancellation_reasons)
    RadioGroup mReasonsRadioGroup;

    private Booking mBooking;
    private Booking.Action mAction;

    @Override
    protected MainViewTab getTab()
    {
        return MainViewTab.CANCELLATION_REQUEST;
    }

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
        return inflater.inflate(R.layout.fragment_cancellation_request, container, false);
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
        RadioButton reasonBtn = UIUtils.getCheckedRadioButton(mReasonsRadioGroup);
        if (reasonBtn == null)
        {
            showToast(R.string.select_a_reason);
        }
        else
        {
            bus.post(new HandyEvent.SetLoadingOverlayVisibility(true));
            bus.post(new HandyEvent.RequestRemoveJob(mBooking));

        }
    }

    @Subscribe
    public void onReceiveRemoveJobSuccess(final HandyEvent.ReceiveRemoveJobSuccess event)
    {
        Booking booking = event.booking;
        if ((!booking.isClaimedByMe() || booking.getProviderId().equals(Booking.NO_PROVIDER_ASSIGNED))
                && booking.getId().equals(mBooking.getId()))
        {
            bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
            TransitionStyle transitionStyle = TransitionStyle.JOB_REMOVE_SUCCESS;
            Bundle arguments = new Bundle();
            arguments.putLong(BundleKeys.DATE_EPOCH_TIME, event.booking.getStartDate().getTime());
            //Return to available jobs on that day
            bus.post(new HandyEvent.NavigateToTab(MainViewTab.SCHEDULED_JOBS, arguments, transitionStyle));
        }
    }

    @Subscribe
    public void onReceiveRemoveJobError(final HandyEvent.ReceiveRemoveJobError event)
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        bus.post(new LogEvent.AddLogEvent(mEventLogFactory.createRemoveJobErrorLog(mBooking)));
        showToast(R.string.job_remove_error);
    }

    private void init()
    {
        String address = mBooking.getAddress().getAddress1() + " " + mBooking.getAddress().getAddress2();
        mAddressTextView.setText(address);

        mDateTextView.setText(DateTimeUtils.formatDateDayOfWeekMonthDay(mBooking.getStartDate()));

        String startTime = DateTimeUtils.CLOCK_FORMATTER_12HR.format(mBooking.getStartDate());
        String endTime = DateTimeUtils.CLOCK_FORMATTER_12HR.format(mBooking.getEndDate());
        mTimeTextView.setText(getString(R.string.time_interval_formatted, startTime, endTime));

        mWithholdingAmountTextView.setText(getString(R.string.withholding_fee_formatted,
                CurrencyUtils.formatPriceWithCents(mAction.getWithholdingAmount(), "$")));

        for (String reason : mAction.getRemoveReasons())
        {
            RadioButton radioButton = (RadioButton) LayoutInflater.from(getContext())
                    .inflate(R.layout.radio_button_cancel_reason, mReasonsRadioGroup, false);
            radioButton.setText(reason);
            mReasonsRadioGroup.addView(radioButton);
        }
    }

}
