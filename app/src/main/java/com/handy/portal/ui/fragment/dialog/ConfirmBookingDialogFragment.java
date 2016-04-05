package com.handy.portal.ui.fragment.dialog;

import android.animation.LayoutTransition;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.handy.portal.R;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.model.Booking;
import com.handy.portal.ui.element.bookings.BookingCancellationPolicyListItemView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ConfirmBookingDialogFragment extends DialogFragment
{
    @Bind(R.id.fragment_dialog_confirm_claim_cancellation_policy_content)
    LinearLayout mCancellationPolicyContent;
    @Bind(R.id.booking_info_claim_button)
    TextView mBookingClaimButton;
    @Bind(R.id.fragment_dialog_confirm_claim_show_cancellation_policy_button)
    TextView mShowCancellationPolicyButton;

    private Booking mBooking;
//    private CountDownTimer mCounter;

    public static final String FRAGMENT_TAG = "fragment_dialog_confirm_claim";

    public static ConfirmBookingDialogFragment newInstance(Booking booking)
    {
        ConfirmBookingDialogFragment fragment = new ConfirmBookingDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(BundleKeys.BOOKING, booking);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mBooking = (Booking) getArguments().getSerializable(BundleKeys.BOOKING);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        Window window = dialog.getWindow();
        window.requestFeature(Window.FEATURE_NO_TITLE);
        window.getAttributes().windowAnimations = R.style.dialog_animation_slide_up_down_from_bottom;
        Drawable background = new ColorDrawable(Color.BLACK);
        background.setAlpha(130);
        window.setBackgroundDrawable(background);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_dialog_confirm_claim, container, false);
        ButterKnife.bind(this, view);


        LayoutTransition lt = new LayoutTransition();
        lt.enableTransitionType(LayoutTransition.DISAPPEARING);
        lt.enableTransitionType(LayoutTransition.CHANGE_APPEARING);
        mCancellationPolicyContent.setLayoutTransition(lt);

        setBookingCancellationPolicyDisplay();
        return view;
    }

    @Override
    public void onStart()
    {
        super.onStart();
        Window window = getDialog().getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        wlp.gravity = Gravity.BOTTOM;
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//        Point size = new Point();
//        getActivity().getWindowManager().getDefaultDisplay().getSize(size);
//        Window window = getDialog().getWindow();
//        window.setGravity(Gravity.BOTTOM);
//        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, size.y / 2);
    }

    @Override
    public void onStop()
    {
//        mCounter.cancel();
        super.onStop();
    }

    @OnClick(R.id.fragment_dialog_confirm_claim_show_cancellation_policy_button)
    public void onShowCancellationPolicyButtonClicked()
    {
        if(mCancellationPolicyContent.getVisibility() == View.VISIBLE)
        {
            mCancellationPolicyContent.setVisibility(View.GONE);
            mShowCancellationPolicyButton.setText("Show Cancellation Policy");
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

    @OnClick(R.id.booking_info_claim_button)
    public void claimBooking()
    {
        Intent intent = new Intent();
        intent.putExtra(BundleKeys.BOOKING, mBooking);
        if(getTargetFragment() != null)
        {
            getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
        }
        dismiss();
    }

    @OnClick(R.id.booking_info_dismiss)
    public void closeDialog()
    {
        dismiss();
    }
}
