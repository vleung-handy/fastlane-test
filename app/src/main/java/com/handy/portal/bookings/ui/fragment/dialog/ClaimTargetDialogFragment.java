package com.handy.portal.bookings.ui.fragment.dialog;

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
import com.handy.portal.bookings.model.BookingClaimDetails;
import com.handy.portal.library.util.CurrencyUtils;
import com.handy.portal.payments.model.PaymentInfo;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ClaimTargetDialogFragment extends DialogFragment //TODO: consolidate some of this logic with other dialog fragments
{
    @Bind(R.id.claim_target_frame_layout)
    protected FrameLayout frameLayout;

    @Bind(R.id.claim_target_window_layout)
    protected LinearLayout windowLayout;

    @Bind(R.id.claim_target_title)
    protected TextView claimTargetTitle;

    @Bind(R.id.claim_target_progress_icons_container)
    protected LinearLayout progressIconsContainer;

    @Bind(R.id.claim_target_info_text)
    protected TextView textClaimTarget;

    @Bind(R.id.expected_pay_label)
    protected TextView textExpectedPayLabel;

    @Bind(R.id.claim_target_expected_pay_dollars_text)
    protected TextView textExpectedPayDollars;

    final Handler handler = new Handler();
    private BookingClaimDetails.ClaimTargetInfo claimTargetInfo;
    private final static long FADE_IN_ANIMATION_DURATION = 300; //TODO: make this not hard-coded. currently have no way of accessing dialog's animation listeners, and cannot access style resource file attributes easily
    private final static long SHOW_DURATION_MS = 3500;

    public static final String FRAGMENT_TAG = "fragment_dialog_claim_target";

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
            textClaimTarget.setText(getResources().getString(R.string.booking_details_claim_target_msg, bookingsThreshold));
        }
        textExpectedPayLabel.setText(getResources().getString(R.string.booking_details_claim_target_expected_pay_label, claimTargetInfo.getNumDaysExpectedPayment()));

        PaymentInfo paymentInfo = claimTargetInfo.getPaymentInfo();
        textExpectedPayDollars.setText(CurrencyUtils.formatPrice(paymentInfo.getAmount(), paymentInfo.getCurrencySymbol()));

    }

    public void inflateProgressIcons()
    {
        int numClaims = claimTargetInfo.getNumJobsClaimed();
        int totalNumIcons = claimTargetInfo.getNumBookingsThreshold();
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
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation_slide_down_up_from_top; //TODO: see if we can use an Animation instead so we can listen for when it ends
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_dialog_claim_target, container, false);
        ButterKnife.bind(this, view);
        updateDisplay();
        return view;
    }

    public void setDelayedDismiss() //TODO: see if we can use an Animation instead of setting a window animation so we can listen for when the animation ends, instead of using this gross logic
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
        handler.postDelayed(runnable, SHOW_DURATION_MS + FADE_IN_ANIMATION_DURATION);
    }

    public void onStart()//dialog becomes visible
    {
        super.onStart();
        setDelayedDismiss();

    }

}
