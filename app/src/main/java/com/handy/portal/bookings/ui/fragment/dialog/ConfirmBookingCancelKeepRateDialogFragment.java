package com.handy.portal.bookings.ui.fragment.dialog;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.bookings.model.Booking;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.library.util.CurrencyUtils;
import com.handy.portal.logger.handylogger.LogEvent;
import com.handy.portal.logger.handylogger.model.ScheduledJobsLog;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

import butterknife.BindView;

public class ConfirmBookingCancelKeepRateDialogFragment extends ConfirmBookingActionDialogFragment
{
    public static final String FRAGMENT_TAG = ConfirmBookingCancelKeepRateDialogFragment.class.getSimpleName();

    @BindView(R.id.keep_rate)
    View mKeepRateView;
    @BindView(R.id.old_keep_rate)
    TextView mOldKeepRateText;
    @BindView(R.id.new_keep_rate)
    TextView mNewKeepRateText;
    @BindView(R.id.no_keep_rate)
    View mNoKeepRateView;
    @BindView(R.id.no_fee_notice)
    View mNoFeeNoticeView;
    @BindView(R.id.withholding_fee_notice)
    TextView mWithholdingFeeNoticeText;

    @Inject
    EventBus mBus;

    public static ConfirmBookingCancelKeepRateDialogFragment newInstance(@NonNull final Booking booking)
    {
        ConfirmBookingCancelKeepRateDialogFragment fragment = new ConfirmBookingCancelKeepRateDialogFragment();
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
        if (removeAction != null)
        {
            final int withholdingAmountCents = removeAction.getFeeAmount();
            mBus.post(new LogEvent.AddLogEvent(new ScheduledJobsLog.RemoveJobConfirmationShown(
                    mBooking,
                    ScheduledJobsLog.RemoveJobLog.KEEP_RATE,
                    withholdingAmountCents,
                    removeAction.getWarningText()
            )));
        }
    }

    private void displayWithholdingNotice()
    {
        final Booking.Action removeAction = mBooking.getAction(Booking.Action.ACTION_REMOVE);
        if (removeAction != null)
        {
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
    }

    private void displayKeepRate()
    {
        final Booking.Action removeAction = mBooking.getAction(Booking.Action.ACTION_REMOVE);
        if (removeAction != null)
        {
            final Booking.Action.Extras.KeepRate keepRate = removeAction.getKeepRate();
            if (keepRate != null)
            {
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
        }
    }

    @Override
    protected View inflateBookingActionContentView(final LayoutInflater inflater, final ViewGroup container)
    {
        return inflater.inflate(R.layout.layout_confirm_booking_cancel, container, false);
    }

    @Override
    protected void onConfirmBookingActionButtonClicked()
    {

        final Booking.Action removeAction = mBooking.getAction(Booking.Action.ACTION_REMOVE);
        if (removeAction != null)
        {
            mBus.post(new LogEvent.AddLogEvent(new ScheduledJobsLog.RemoveJobSubmitted(
                    mBooking,
                    ScheduledJobsLog.RemoveJobLog.KEEP_RATE,
                    null, //don't have a remove reason
                    removeAction.getFeeAmount(),
                    removeAction.getWarningText()
            )));
        }
        if (getTargetFragment() != null)
        {
            getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, null);
        }
        dismiss();
    }

    @Override
    public void dismiss()
    {
        if (getTargetFragment() != null)
        {
            getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_CANCELED, null);
        }
        super.dismiss();
    }

    @Override
    protected int getConfirmButtonBackgroundResourceId()
    {
        return R.drawable.button_red_round;
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
                new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.plumber_red)),
                start,
                end,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );
    }
}
