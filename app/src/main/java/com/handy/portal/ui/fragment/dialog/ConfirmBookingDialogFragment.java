package com.handy.portal.ui.fragment.dialog;

import android.animation.LayoutTransition;
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
import com.handy.portal.ui.element.bookings.BookingCancellationPolicyListItemView;

import butterknife.Bind;
import butterknife.OnClick;

public class ConfirmBookingDialogFragment extends ConfirmBookingActionDialogFragment
{
    @Bind(R.id.fragment_dialog_confirm_claim_cancellation_policy_content)
    LinearLayout mCancellationPolicyContent;
    @Bind(R.id.fragment_dialog_confirm_claim_show_cancellation_policy_button)
    TextView mShowCancellationPolicyButton;

    public static final String FRAGMENT_TAG = ConfirmBookingDialogFragment.class.getName();

    /*
    TODO make this cleaner
     */
    public static ConfirmBookingDialogFragment newInstance(Booking booking)
    {
        ConfirmBookingDialogFragment fragment = new ConfirmBookingDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(BundleKeys.BOOKING, booking);
        fragment.setArguments(args);
        return fragment;
    }

    /*
    TODO make this cleaner
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        View confirmBookingActionContentView = inflater.inflate(R.layout.fragment_dialog_confirm_claim, container, false);
        LinearLayout confirmBookingActionContentLayout = (LinearLayout) view.findViewById(R.id.confirm_booking_action_content);
        confirmBookingActionContentLayout.addView(confirmBookingActionContentView);
        return view;
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
