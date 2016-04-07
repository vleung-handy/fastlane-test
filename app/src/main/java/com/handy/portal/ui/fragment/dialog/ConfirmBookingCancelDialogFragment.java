package com.handy.portal.ui.fragment.dialog;

import android.app.Activity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.logger.handylogger.LogEvent;
import com.handy.portal.logger.handylogger.model.ScheduledJobsLog;
import com.handy.portal.model.Booking;
import com.handy.portal.util.CurrencyUtils;
import com.squareup.otto.Bus;

import javax.inject.Inject;

import butterknife.Bind;

public class ConfirmBookingCancelDialogFragment extends ConfirmBookingActionDialogFragment
{
    public static final String FRAGMENT_TAG = ConfirmBookingCancelDialogFragment.class.getSimpleName();

    @Bind(R.id.keep_rate)
    View mKeepRate;
    @Bind(R.id.old_keep_rate)
    TextView mOldKeepRate;
    @Bind(R.id.new_keep_rate)
    TextView mNewKeepRate;
    @Bind(R.id.no_keep_rate)
    View mNoKeepRate;
    @Bind(R.id.no_fee_notice)
    View mNoFeeNotice;
    @Bind(R.id.withholding_fee_notice)
    TextView mWithholdingFeeNotice;

    @Inject
    Bus mBus;

    public static ConfirmBookingCancelDialogFragment newInstance(final Booking booking)
    {
        ConfirmBookingCancelDialogFragment fragment = new ConfirmBookingCancelDialogFragment();
        Bundle arguments = new Bundle();
        arguments.putSerializable(BundleKeys.BOOKING, booking);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        displayWithholdingNotice();
        displayKeepRate();

        mBus.post(new LogEvent.AddLogEvent(new ScheduledJobsLog.RemoveJobConfirmationShown(
                mBooking,
                ScheduledJobsLog.RemoveJobLog.KEEP_RATE
        )));
    }

    private void displayWithholdingNotice()
    {
        final Booking.Action removeAction = mBooking.getAction(Booking.Action.ACTION_REMOVE);
        final int withholdingAmount = removeAction.getWithholdingAmount();
        if (withholdingAmount > 0)
        {
            final String currencySymbol = mBooking.getPaymentToProvider().getCurrencySymbol();
            final String fee = CurrencyUtils.formatPriceWithCents(withholdingAmount, currencySymbol);
            setWithholdingFee(fee);
            mNoFeeNotice.setVisibility(View.GONE);
            mWithholdingFeeNotice.setVisibility(View.VISIBLE);
        }
        else
        {
            mWithholdingFeeNotice.setVisibility(View.GONE);
            mNoFeeNotice.setVisibility(View.VISIBLE);
        }
    }

    private void displayKeepRate()
    {
        final Booking.Action removeAction = mBooking.getAction(Booking.Action.ACTION_REMOVE);
        final Booking.Action.Extras.KeepRate keepRate = removeAction.getKeepRate();
        final Float oldKeepRate = keepRate.getActual();
        final Float newKeepRate = keepRate.getOnNextUnassign();
        if (oldKeepRate != null && newKeepRate != null)
        {
            final String oldKeepRateFormatted =
                    getString(R.string.keep_rate_percent_formatted, Math.round(oldKeepRate * 100));
            final String newKeepRateFormatted =
                    getString(R.string.keep_rate_percent_formatted, Math.round(newKeepRate * 100));
            mOldKeepRate.setText(oldKeepRateFormatted);
            mNewKeepRate.setText(newKeepRateFormatted);
            mKeepRate.setVisibility(View.VISIBLE);
            mNoKeepRate.setVisibility(View.GONE);
        }
        else
        {
            mKeepRate.setVisibility(View.GONE);
            mNoKeepRate.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected View getBookingActionContentView(final LayoutInflater inflater, final ViewGroup container)
    {
        return inflater.inflate(R.layout.layout_confirm_booking_cancel, container, false);
    }

    @Override
    protected void onConfirmBookingActionButtonClicked()
    {
        mBus.post(new LogEvent.AddLogEvent(new ScheduledJobsLog.RemoveJobSubmitted(
                mBooking,
                ScheduledJobsLog.RemoveJobLog.KEEP_RATE
        )));
        if (getTargetFragment() != null)
        {
            getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, null);
        }
        dismiss();
    }

    @Override
    protected int getConfirmButtonBackgroundResourceId()
    {
        return R.color.error_red;
    }

    @Override
    protected String getConfirmButtonText()
    {
        return getString(R.string.cancel_job);
    }

    private void setWithholdingFee(final String fee)
    {
        final String feeFormatted = getString(R.string.withholding_fee_simple_formatted, fee);
        mWithholdingFeeNotice.setText(feeFormatted, TextView.BufferType.SPANNABLE);
        final Spannable spannable = (Spannable) mWithholdingFeeNotice.getText();
        final int start = feeFormatted.indexOf(fee);
        final int end = start + fee.length();
        spannable.setSpan(
                new ForegroundColorSpan(getResources().getColor(R.color.error_red)),
                start,
                end,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );
    }
}
