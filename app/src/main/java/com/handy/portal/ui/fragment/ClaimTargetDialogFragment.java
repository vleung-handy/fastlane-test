package com.handy.portal.ui.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.model.BookingClaimDetails;
import com.handy.portal.model.PaymentInfo;
import com.handy.portal.util.TextUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ClaimTargetDialogFragment extends DialogFragment
{

    @InjectView(R.id.claim_target_frame_layout)
    protected FrameLayout frameLayout;

    @InjectView(R.id.claim_target_window_layout)
    protected LinearLayout windowLayout;

    @InjectView(R.id.claim_target_title)
    protected TextView claimTargetTitle;

    @InjectView(R.id.claim_target_progress_icons_container)
    protected LinearLayout progressIconsContainer;

    @InjectView(R.id.claim_target_info_text)
    protected TextView textClaimTarget;

    @InjectView(R.id.expected_pay_label)
    protected TextView textExpectedPayLabel;

    @InjectView(R.id.claim_target_expected_pay_dollars_text)
    protected TextView textExpectedPayDollars;

    final Handler handler = new Handler();
    private BookingClaimDetails.ClaimTargetInfo claimTargetInfo;
    private final static long fadeInAnimationDuration = 300; //TODO: make this not hard-coded. currently have no way of accessing dialog's animation listeners, and cannot access style resource file attributes easily
    private final static long showDurationMs = 2500;

    public ClaimTargetDialogFragment()
    {

    }

    public void setDisplayData(BookingClaimDetails.ClaimTargetInfo claimTargetInfo)
    {
        this.claimTargetInfo = claimTargetInfo;
    }

    public void updateDisplay() //with claimTargetInfo
    {
        inflateProgressIcons();

        int numClaims = claimTargetInfo.getNumJobsClaimed();
        int bookingsThreshold = claimTargetInfo.getNumBookingsThreshold();

        if (numClaims >= bookingsThreshold)
        {
            claimTargetTitle.setText(getResources().getString(R.string.booking_details_claim_target_reached_title));
            textClaimTarget.setText(getResources().getString(R.string.booking_details_claim_target_reached_msg));
        }
        else
        {
            claimTargetTitle.setText(getResources().getString(R.string.booking_details_claim_target_title));
            textClaimTarget.setText(getResources().getString(R.string.booking_details_claim_target_msg, bookingsThreshold - numClaims));
        }
        textExpectedPayLabel.setText(getResources().getString(R.string.booking_details_claim_target_expected_pay_label, claimTargetInfo.getNumDaysExpectedPayment()));

        PaymentInfo paymentInfo = claimTargetInfo.getPaymentInfo();
        textExpectedPayDollars.setText(TextUtils.formatPrice(paymentInfo.getAmount(), paymentInfo.getCurrencySymbol()));

    }

    public void inflateProgressIcons()
    {
        int numClaims = claimTargetInfo.getNumJobsClaimed();
        int totalNumIcons = claimTargetInfo.getNumBookingsThreshold();
        if (progressIconsContainer == null) return;//should not happen. test only
        progressIconsContainer.removeAllViews();
        for (int i = 0; i < totalNumIcons; i++)
        {
            ImageView view = (ImageView) LayoutInflater.from(this.getActivity()).inflate(R.layout.element_claim_target_progress_icon, null);
            view.setImageResource(i < numClaims ? R.drawable.icon_check_small : R.drawable.icon_empty_small);
            view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)); //the identical params in resource xml don't work
            progressIconsContainer.addView(view);
        }
        progressIconsContainer.setWeightSum(totalNumIcons);
        progressIconsContainer.invalidate();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation_slide_down_up_from_top;
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_claim_target, container, false);
        ButterKnife.inject(this, view);
        updateDisplay();
        return view;
    }

    public void setDelayedDismiss()
    {
        final Runnable runnable = new Runnable()
        {
            @Override
            public void run()
            {
                Dialog dialog = getDialog();
                if (dialog != null && dialog.isShowing())
                {
                    ClaimTargetDialogFragment.this.dismiss();
                }
            }
        };
        handler.postDelayed(runnable, showDurationMs + fadeInAnimationDuration);
    }

    public void onStart()//dialog becomes visible
    {
        super.onStart();
        setDelayedDismiss();

    }

}
