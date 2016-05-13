package com.handy.portal.ui.fragment.dialog;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handy.portal.R;
import com.handy.portal.library.ui.fragment.dialog.BottomUpDialogFragment;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class OnboardJobClaimConfirmDialog extends BottomUpDialogFragment
{

    private ConfirmationDialogListener mDialogListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_dialog_onboarding_confirm_claim, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        //makes it so that the only way to dismiss this is to explicitly click the buttons on the dialog
        dialog.setCancelable(false);
        return dialog;
    }

    @Override
    public void onAttach(final Context context)
    {
        super.onAttach(context);
        if (context instanceof ConfirmationDialogListener)
        {
            mDialogListener = (ConfirmationDialogListener) context;
        }
    }

    @OnClick(R.id.booking_info_claim_button)
    public void confirmed()
    {
        if (mDialogListener != null)
        {
            mDialogListener.confirmJobClaims();
        }
    }

    public interface ConfirmationDialogListener
    {
        void confirmJobClaims();
    }
}
