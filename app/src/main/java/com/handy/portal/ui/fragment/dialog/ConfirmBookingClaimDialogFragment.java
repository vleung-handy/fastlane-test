package com.handy.portal.ui.fragment.dialog;

import android.animation.LayoutTransition;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.handy.portal.R;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.model.Booking;
import com.handy.portal.ui.element.bookings.BookingCancellationPolicyListItemView;

import butterknife.Bind;
import butterknife.OnClick;

public class ConfirmBookingClaimDialogFragment extends ConfirmBookingActionDialogFragment
{
    @Bind(R.id.fragment_dialog_confirm_claim_cancellation_policy_content)
    LinearLayout mCancellationPolicyContent;
    @Bind(R.id.fragment_dialog_confirm_claim_show_cancellation_policy_button)
    TextView mShowCancellationPolicyButton;
    @Bind(R.id.confirm_booking_action_button)
    Button mConfirmBookingActionButton;

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
    public void onViewCreated(final View view, final Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        //tODO make this actually work
        LayoutTransition lt = new LayoutTransition();
        lt.enableTransitionType(LayoutTransition.DISAPPEARING);
        lt.enableTransitionType(LayoutTransition.CHANGE_APPEARING);
        mCancellationPolicyContent.setLayoutTransition(lt);

        setBookingCancellationPolicyDisplay();
    }

    @OnClick(R.id.fragment_dialog_confirm_claim_show_cancellation_policy_button)
    public void onShowCancellationPolicyButtonClicked()
    {
        if(mCancellationPolicyContent.getVisibility() == View.VISIBLE)
        {
            mCancellationPolicyContent.setVisibility(View.GONE);
            mShowCancellationPolicyButton.setText("Show Cancellation Policy"); //TODO strings.xml
        }
        else
        {
            mCancellationPolicyContent.setVisibility(View.VISIBLE);
            mShowCancellationPolicyButton.setText("Hide Cancellation Policy");
        }

    }

    @OnClick(R.id.confirm_booking_action_button)
    public void onConfirmBookingActionButtonClicked()
    {
        super.confirmBookingActionButtonClicked();
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
        else
        {
            Booking.Action.Extras.CancellationPolicyItem cancellationPolicyItems[] =
                    bookingClaimAction.getExtras().getCancellationPolicyArray();

            mCancellationPolicyContent.removeAllViews();
            for(int i = 0; i<cancellationPolicyItems.length; i++)
            {
                Booking.Action.Extras.CancellationPolicyItem cancellationPolicyItem = cancellationPolicyItems[i];
                BookingCancellationPolicyListItemView policyListItemView =
                        new BookingCancellationPolicyListItemView(getContext())
                                .setLeftText(cancellationPolicyItem.getDisplayText())
                                .setRightText(cancellationPolicyItem.getAmountFormatted())
                                .setHighlighted(cancellationPolicyItem.isActive());
                mCancellationPolicyContent.addView(policyListItemView);
            }
        }
    }
}
