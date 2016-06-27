package com.handy.portal.bookings.ui.fragment.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.handy.portal.R;
import com.handy.portal.bookings.model.Booking;
import com.handy.portal.bookings.ui.element.BookingCancellationPolicyListItemView;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.library.util.CurrencyUtils;
import com.handy.portal.payments.model.PaymentInfo;

import butterknife.Bind;
import butterknife.OnClick;

public abstract class ConfirmBookingCancellationPolicyDialogFragment
        extends ConfirmBookingActionDialogFragment
{
    @Bind(R.id.fragment_dialog_confirm_booking_cancellation_policy_content)
    LinearLayout mCancellationPolicyContent;
    @Bind(R.id.fragment_dialog_confirm_booking_show_cancellation_policy_button)
    TextView mShowCancellationPolicyButton;
    @Bind(R.id.confirm_booking_action_title)
    TextView mConfirmBookingActionTitle;
    @Bind(R.id.confirm_booking_action_subtitle)
    TextView mConfirmBookingActionSubtitle;

    protected Booking.Action mAction;

    @OnClick(R.id.fragment_dialog_confirm_booking_show_cancellation_policy_button)
    public void onShowCancellationPolicyButtonClicked()
    {
        mCancellationPolicyContent.setVisibility(View.VISIBLE);
        mShowCancellationPolicyButton.setVisibility(View.GONE);
        afterShowCancellationPolicyButtonClicked();
    }

    public abstract void afterShowCancellationPolicyButtonClicked();

    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mAction = (Booking.Action) getArguments().getSerializable(BundleKeys.BOOKING_ACTION);
    }

    @Override
    protected View inflateBookingActionContentView(final LayoutInflater inflater, final ViewGroup container)
    {
        return inflater.inflate(R.layout.layout_confirm_booking_cancellation_policy, container, false);
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        setTitleAndSubtitle();
        setBookingCancellationPolicyDisplay();
        afterViewCreated();
    }

    public abstract void afterViewCreated();

    private void setTitleAndSubtitle()
    {
        if (mAction == null
                || mAction.getExtras() == null
                || mAction.getExtras().getCancellationPolicy() == null)
        {
            Crashlytics.logException(new Exception("Booking action object is null or missing cancellation policy"));
            return;
        }

        Booking.Action.Extras.CancellationPolicy cancellationPolicy = mAction.getExtras().getCancellationPolicy();
        mConfirmBookingActionTitle.setText(cancellationPolicy.getHeaderText());
        mConfirmBookingActionSubtitle.setText(cancellationPolicy.getSubtitleText());
    }


    private void setBookingCancellationPolicyDisplay()
    {
        if (mAction == null
                || mAction.getExtras() == null
                || mAction.getExtras().getCancellationPolicy() == null)
        {
            Crashlytics.logException(new Exception("Booking action object is null or missing cancellation policy"));
            return;
        }
        Booking.Action.Extras.CancellationPolicy.CancellationPolicyItem cancellationPolicies[] =
                mAction.getExtras().getCancellationPolicy().getCancellationPolicyItems();

        mCancellationPolicyContent.removeAllViews();
        if (cancellationPolicies != null)
        {
            for (int i = 0; i < cancellationPolicies.length; i++)
            {
                Booking.Action.Extras.CancellationPolicy.CancellationPolicyItem cancellationPolicy = cancellationPolicies[i];
                PaymentInfo fee = cancellationPolicy.getPaymentInfo();
                PaymentInfo waivedFee = cancellationPolicy.getWaivedPaymentInfo();
                if (fee != null)
                {
                    final String feeAmountFormatted = CurrencyUtils.formatPriceWithoutCents(fee.getAmount(), fee.getCurrencySymbol());
                    String waivedFeeAmountFormatted = null;
                    if (waivedFee != null)
                    {
                        waivedFeeAmountFormatted = CurrencyUtils.formatPriceWithoutCents(waivedFee.getAmount(), waivedFee.getCurrencySymbol());
                    }

                    BookingCancellationPolicyListItemView policyListItemView =
                            new BookingCancellationPolicyListItemView(getContext())
                                    .setLeftText(cancellationPolicy.getDisplayText())
                                    .setWaivedFeeText(waivedFeeAmountFormatted)
                                    .setFeeText(feeAmountFormatted)
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
