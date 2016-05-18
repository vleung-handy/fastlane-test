package com.handy.portal.ui.fragment.dialog;


import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handy.portal.R;
import com.handy.portal.library.ui.fragment.dialog.SlideUpDialogFragment;

import butterknife.OnClick;

public class OnboardingJobClaimConfirmDialog extends SlideUpDialogFragment
{
    public static OnboardingJobClaimConfirmDialog newInstance()
    {
        return new OnboardingJobClaimConfirmDialog();
    }

    @OnClick(R.id.booking_info_claim_button)
    void onClaim()
    {
        if (getTargetFragment() != null)
        {
            getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, null);
        }
        dismiss();
    }

    @Override
    protected View inflateContentView(final LayoutInflater inflater, final ViewGroup container)
    {
        return inflater.inflate(R.layout.fragment_dialog_onboarding_confirm_claim,
                container, false);
    }
}
