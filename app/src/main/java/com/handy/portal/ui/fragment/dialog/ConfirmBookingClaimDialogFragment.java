package com.handy.portal.ui.fragment.dialog;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.handy.portal.R;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.logger.handylogger.LogEvent;
import com.handy.portal.logger.handylogger.model.AvailableJobsLog;
import com.handy.portal.model.Booking;
import com.handy.portal.model.PaymentInfo;
import com.handy.portal.ui.element.bookings.BookingCancellationPolicyListItemView;
import com.handy.portal.util.CurrencyUtils;
import com.squareup.otto.Bus;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.OnClick;

public class ConfirmBookingClaimDialogFragment extends ConfirmBookingActionDialogFragment
{
    @Bind(R.id.fragment_dialog_confirm_claim_cancellation_policy_content)
    LinearLayout mCancellationPolicyContent;
    @Bind(R.id.fragment_dialog_confirm_claim_show_cancellation_policy_button)
    TextView mShowCancellationPolicyButton;
    @Bind(R.id.confirm_booking_action_title)
    TextView mConfirmBookingActionTitle;
    @Bind(R.id.confirm_booking_action_subtitle)
    TextView mConfirmBookingActionSubtitle;

    @Inject
    Bus mBus;

    public static final String FRAGMENT_TAG = ConfirmBookingClaimDialogFragment.class.getName();

    public static ConfirmBookingClaimDialogFragment newInstance(@NonNull Booking booking)
    {
        ConfirmBookingClaimDialogFragment fragment = new ConfirmBookingClaimDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(BundleKeys.BOOKING, booking);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View getBookingActionContentView(LayoutInflater inflater, ViewGroup container)
    {
        return inflater.inflate(R.layout.layout_confirm_booking_claim, container, false);
    }

    @Override
    protected int getConfirmButtonBackgroundResourceId()
    {
        return R.drawable.button_green_round;
    }

    @Override
    protected String getConfirmButtonText()
    {
        return getString(R.string.claim_job);
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        setTitleAndSubtitle();
        setBookingCancellationPolicyDisplay();

        mBus.post(new LogEvent.AddLogEvent(new AvailableJobsLog.ConfirmClaimShown()));
    }

    private void setTitleAndSubtitle()
    {
        Booking.Action bookingClaimAction = mBooking.getAction(Booking.Action.ACTION_CLAIM);
        if(bookingClaimAction == null
                || bookingClaimAction.getExtras() == null
                || bookingClaimAction.getExtras().getCancellationPolicy() == null)
        {
            Crashlytics.logException(new Exception("Booking claim action object is null or missing cancellation policy"));
            return;
        }

        Booking.Action.Extras.CancellationPolicy cancellationPolicy = bookingClaimAction.getExtras().getCancellationPolicy();
        mConfirmBookingActionTitle.setText(cancellationPolicy.getHeaderText());
        mConfirmBookingActionSubtitle.setText(cancellationPolicy.getSubtitleText());
    }

    @OnClick(R.id.fragment_dialog_confirm_claim_show_cancellation_policy_button)
    public void onShowCancellationPolicyButtonClicked()
    {
        mCancellationPolicyContent.setVisibility(View.VISIBLE);
        mShowCancellationPolicyButton.setVisibility(View.GONE);
        mBus.post(new LogEvent.AddLogEvent(new AvailableJobsLog.ConfirmClaimDetailsShown()));
    }

    @Override
    protected void onConfirmBookingActionButtonClicked()
    {
        Intent intent = new Intent();
        intent.putExtra(BundleKeys.BOOKING, mBooking);
        if(getTargetFragment() != null)
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

    private void setBookingCancellationPolicyDisplay()
    {
        Booking.Action bookingClaimAction = mBooking.getAction(Booking.Action.ACTION_CLAIM);
        if(bookingClaimAction == null
                || bookingClaimAction.getExtras() == null
                || bookingClaimAction.getExtras().getCancellationPolicy() == null)
        {
            Crashlytics.logException(new Exception("Booking claim action object is null or missing cancellation policy"));
            return;
        }
        Booking.Action.Extras.CancellationPolicy.CancellationPolicyItem cancellationPolicies[] =
                bookingClaimAction.getExtras().getCancellationPolicy().getCancellationPolicyItems();

        mCancellationPolicyContent.removeAllViews();
        if(cancellationPolicies != null)
        {
            for(int i = 0; i< cancellationPolicies.length; i++)
            {
                Booking.Action.Extras.CancellationPolicy.CancellationPolicyItem cancellationPolicy = cancellationPolicies[i];
                PaymentInfo fee = cancellationPolicy.getPaymentInfo();
                if(fee != null)
                {
                    String feeAmountFormatted = CurrencyUtils.formatPriceWithoutCents(fee.getAmount(), fee.getCurrencySymbol());

                    BookingCancellationPolicyListItemView policyListItemView =
                            new BookingCancellationPolicyListItemView(getContext())
                                    .setLeftText(cancellationPolicy.getDisplayText())
                                    .setRightText(feeAmountFormatted)
                                    .setHighlighted(cancellationPolicy.isActive())
                                    .setDividerVisible(i != (cancellationPolicies.length - 1));
                    mCancellationPolicyContent.addView(policyListItemView);
                }
                else
                {
                    Crashlytics.logException(new Exception("Cancellation policy item payment info is null"));
                }
            }
        }
        else
        {
            Crashlytics.logException(new Exception("Cancellation policies array is null"));
        }
    }
}
