package com.handy.portal.ui.fragment.dialog;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
    View mKeepRateView;
    @Bind(R.id.old_keep_rate)
    TextView mOldKeepRateText;
    @Bind(R.id.new_keep_rate)
    TextView mNewKeepRateText;
    @Bind(R.id.no_keep_rate)
    View mNoKeepRateView;
    @Bind(R.id.no_fee_notice)
    View mNoFeeNoticeView;
    @Bind(R.id.withholding_fee_notice)
    TextView mWithholdingFeeNoticeText;

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


        final Booking.Action removeAction = mBooking.getAction(Booking.Action.ACTION_REMOVE);
        final int withholdingAmountCents = removeAction.getFeeAmount();

        mBus.post(new LogEvent.AddLogEvent(new ScheduledJobsLog.RemoveJobConfirmationShown(
                mBooking,
                ScheduledJobsLog.RemoveJobLog.KEEP_RATE,
                withholdingAmountCents,
                removeAction.getWarningText()
        )));
    }

    private void displayWithholdingNotice()
    {
        final Booking.Action removeAction = mBooking.getAction(Booking.Action.ACTION_REMOVE);
        final int withholdingAmountCents = removeAction.getFeeAmount();
        if (withholdingAmountCents > 0)
        {
            final String currencySymbol = mBooking.getPaymentToProvider().getCurrencySymbol();
            final String feeFormatted = CurrencyUtils.formatPriceWithCents(withholdingAmountCents, currencySymbol);
            setWithholdingFee(feeFormatted);
            mNoFeeNoticeView.setVisibility(View.GONE);
            mWithholdingFeeNoticeText.setVisibility(View.VISIBLE);
        }
        else
        {
            mWithholdingFeeNoticeText.setVisibility(View.GONE);
            mNoFeeNoticeView.setVisibility(View.VISIBLE);
        }
    }

    private void displayKeepRate()
    {
        final Booking.Action removeAction = mBooking.getAction(Booking.Action.ACTION_REMOVE);
        final Booking.Action.Extras.KeepRate keepRate = removeAction.getKeepRate();
        final Float oldKeepRate = keepRate.getCurrent();
        final Float newKeepRate = keepRate.getOnNextUnassign();
        if (oldKeepRate != null && newKeepRate != null)
        {
            final String oldKeepRateFormatted =
                    getString(R.string.keep_rate_percent_formatted, Math.round(oldKeepRate * 100));
            final String newKeepRateFormatted =
                    getString(R.string.keep_rate_percent_formatted, Math.round(newKeepRate * 100));
            mOldKeepRateText.setText(oldKeepRateFormatted);
            mNewKeepRateText.setText(newKeepRateFormatted);
            mKeepRateView.setVisibility(View.VISIBLE);
            mNoKeepRateView.setVisibility(View.GONE);
        }
        else
        {
            mKeepRateView.setVisibility(View.GONE);
            mNoKeepRateView.setVisibility(View.VISIBLE);
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

        final Booking.Action removeAction = mBooking.getAction(Booking.Action.ACTION_REMOVE);
        mBus.post(new LogEvent.AddLogEvent(new ScheduledJobsLog.RemoveJobSubmitted(
                mBooking,
                ScheduledJobsLog.RemoveJobLog.KEEP_RATE,
                null,
                removeAction.getFeeAmount(),
                removeAction.getWarningText()
        ))); //TODO fix this
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

    private void setWithholdingFee(@NonNull final String feeFormatted)
    {
        final String withholdingFeeMessageFormatted = getString(R.string.withholding_fee_simple_formatted, feeFormatted);
        mWithholdingFeeNoticeText.setText(withholdingFeeMessageFormatted, TextView.BufferType.SPANNABLE);
        final Spannable spannable = (Spannable) mWithholdingFeeNoticeText.getText();
        final int start = withholdingFeeMessageFormatted.indexOf(feeFormatted);
        final int end = start + feeFormatted.length();
        spannable.setSpan(
                new ForegroundColorSpan(getResources().getColor(R.color.error_red)),
                start,
                end,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );
    }
}
