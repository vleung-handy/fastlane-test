package com.handy.portal.ui.fragment.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import com.handy.portal.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class PaymentBillBlockerDialogFragment extends DialogFragment //TODO: consolidate some of this logic with other dialog fragments
{

    @InjectView(R.id.payments_bill_blocker_update_now_button)
    protected Button updateNowButton;

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
        View view = inflater.inflate(R.layout.fragment_dialog_payment_bill_blocker, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        updateNowButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                PaymentBillBlockerDialogFragment.this.dismiss();
            }
        });
    }
}
