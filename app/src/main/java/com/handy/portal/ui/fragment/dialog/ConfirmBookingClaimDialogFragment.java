package com.handy.portal.ui.fragment.dialog;

import android.animation.LayoutTransition;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.handy.portal.R;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.model.Booking;
import com.handy.portal.model.PaymentInfo;
import com.handy.portal.ui.element.bookings.BookingCancellationPolicyListItemView;
import com.handy.portal.util.CurrencyUtils;

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

    public static final String FRAGMENT_TAG = ConfirmBookingClaimDialogFragment.class.getName();

    /*
    TODO make this cleaner
     */
    public static ConfirmBookingClaimDialogFragment newInstance(Booking booking)
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
        return R.drawable.button_green;
    }

    @Override
    protected String getConfirmButtonText()
    {
        return "Confirm Claim"; //todo strings.xml
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        //tODO make this actually work
        LayoutTransition lt = new LayoutTransition();
        lt.enableTransitionType(LayoutTransition.DISAPPEARING);
        lt.enableTransitionType(LayoutTransition.CHANGE_APPEARING);
        lt.enableTransitionType(LayoutTransition.CHANGE_DISAPPEARING);
        lt.enableTransitionType(LayoutTransition.APPEARING);
        mCancellationPolicyContent.setLayoutTransition(lt);

        setTitleAndSubtitle();
        setBookingCancellationPolicyDisplay();
    }

    private void setTitleAndSubtitle()
    {
        Booking.Action bookingClaimAction = mBooking.getAction(Booking.Action.ACTION_CLAIM);
        if(bookingClaimAction == null || bookingClaimAction.getExtras() == null) return;
        mConfirmBookingActionTitle.setText(bookingClaimAction.getExtras().getHeaderText());
        mConfirmBookingActionSubtitle.setText(bookingClaimAction.getExtras().getSubText());
    }

    @OnClick(R.id.fragment_dialog_confirm_claim_show_cancellation_policy_button)
    public void onShowCancellationPolicyButtonClicked()
    {
        mCancellationPolicyContent.setVisibility(View.VISIBLE);
        mShowCancellationPolicyButton.setVisibility(View.GONE);
    }

    //todo consider another way of doing this
    @Override
    protected void onConfirmBookingActionButtonClicked()
    {
        Intent intent = new Intent();
        intent.putExtra(BundleKeys.BOOKING, mBooking);
        if(getTargetFragment() != null)
        {
            getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
        }
        dismiss();
    }

    private void setBookingCancellationPolicyDisplay()
    {
        Booking.Action bookingClaimAction = mBooking.getAction(Booking.Action.ACTION_CLAIM);
        if(bookingClaimAction == null)
        {
            Crashlytics.logException(new Exception("Booking claim action is null in confirm booking dialog fragment"));
        }
        else if(bookingClaimAction.getExtras() == null)
        {
            Crashlytics.logException(new Exception("Booking claim action extras is null in confirm booking dialog fragment"));
        }
        else if(bookingClaimAction.getExtras().getCancellationPolicy() != null)
        {
            Booking.Action.Extras.CancellationPolicy.CancellationPolicyItem cancellationPolicies[] =
                    bookingClaimAction.getExtras().getCancellationPolicy().getCancellationPolicyItems();

            mCancellationPolicyContent.removeAllViews();
            for(int i = 0; i< cancellationPolicies.length; i++)
            {
                Booking.Action.Extras.CancellationPolicy.CancellationPolicyItem cancellationPolicy = cancellationPolicies[i];
                PaymentInfo fee = cancellationPolicy.getPaymentInfo();
                String feeAmountFormatted = CurrencyUtils.formatPriceWithCents(fee.getAmount(), fee.getCurrencySymbol());

                BookingCancellationPolicyListItemView policyListItemView =
                        new BookingCancellationPolicyListItemView(getContext())
                                .setLeftText(cancellationPolicy.getDisplayText())
                                .setRightText(feeAmountFormatted)
                                .setHighlighted(cancellationPolicy.isActive());
                mCancellationPolicyContent.addView(policyListItemView);
            }
        }
    }
}
