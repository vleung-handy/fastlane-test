package com.handy.portal.ui.fragment.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.constant.RequestCode;
import com.handy.portal.model.Booking;
import com.handy.portal.model.PaymentInfo;
import com.handy.portal.util.DateTimeUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ConfirmBookingDialogFragment extends DialogFragment
{

    @Bind(R.id.booking_info_address)
    TextView mBookingAddressText;
    @Bind(R.id.booking_info_timer)
    TextView mBookingTimerText;
    @Bind(R.id.booking_info_time)
    TextView mBookingTimeText;
    @Bind(R.id.booking_info_claim_button)
    TextView mBookingClaimButton;

    private Booking mBooking;
    private CountDownTimer mCounter;

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

        setBookingInfoDisplay();
        return view;
    }

    @Override
    public void onStart()
    {
        super.onStart();
        Point size = new Point();
        getActivity().getWindowManager().getDefaultDisplay().getSize(size);
        Window window = getDialog().getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, size.y / 2);
    }

    @Override
    public void onStop()
    {
        mCounter.cancel();
        super.onStop();
    }

    private void setBookingInfoDisplay()
    {
        PaymentInfo paymentInfo = mBooking.getPaymentToProvider();

        mBookingAddressText.setText(mBooking.getAddress().getShortRegion());

        String startTime = DateTimeUtils.CLOCK_FORMATTER_12HR.format(mBooking.getStartDate());
        String endTime = DateTimeUtils.CLOCK_FORMATTER_12HR.format(mBooking.getEndDate());
        mBookingTimeText.setText(getString(R.string.time_interval_formatted, startTime, endTime));

        mBookingClaimButton.setText(getString(R.string.claim_n_dollar_job_formatted,
                paymentInfo.getCurrencySymbol() + paymentInfo.getAdjustedAmount()));
        setCountDownTimer(mBooking.getStartDate().getTime() - System.currentTimeMillis());
    }

    @OnClick(R.id.booking_info_claim_button)
    public void claimBooking()
    {
        Intent intent = new Intent();
        intent.putExtra(BundleKeys.BOOKING, mBooking);
        getTargetFragment().onActivityResult(RequestCode.CONFIRM_REQUEST, Activity.RESULT_OK, intent);
        dismiss();
    }


    @OnClick(R.id.booking_info_dismiss)
    public void closeDialog()
    {
        dismiss();
    }


    private void setCountDownTimer(long timeRemainMillis)
    {
        if (mCounter != null) { mCounter.cancel(); } // cancel the previous counter

        mCounter = DateTimeUtils.setCountDownTimer(mBookingTimerText, timeRemainMillis);
    }


}
