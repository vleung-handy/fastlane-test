package com.handy.portal.bookings.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.handy.portal.R;
import com.handy.portal.bookings.ui.fragment.dialog.ConfirmBookingActionDialogFragment;
import com.handy.portal.model.ConfigurationResponse.RequestDismissal;

import java.util.ArrayList;

import butterknife.BindView;

public class RequestDismissalReasonsDialogFragment extends ConfirmBookingActionDialogFragment
{
    private static final String KEY_REASONS = "reasons";

    @BindView(R.id.request_dismissal_reasons_radio_group)
    RadioGroup mReasonsRadioGroup;

    private ArrayList<RequestDismissal.Reason> mReasons;
    private String mSelectedReasonMachineName;

    public static RequestDismissalReasonsDialogFragment newInstance(
            final ArrayList<RequestDismissal.Reason> reasons)
    {
        final RequestDismissalReasonsDialogFragment dialogFragment =
                new RequestDismissalReasonsDialogFragment();
        final Bundle arguments = new Bundle();
        arguments.putSerializable(KEY_REASONS, reasons);
        dialogFragment.setArguments(arguments);
        return dialogFragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mReasons = (ArrayList<RequestDismissal.Reason>) getArguments().getSerializable(KEY_REASONS);
    }

    @Override
    protected View inflateBookingActionContentView(final LayoutInflater inflater, final ViewGroup container)
    {
        return inflater.inflate(R.layout.fragment_dialog_request_dismissal_reasons, container, false);
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        hideDismissButton();
        for (final RequestDismissal.Reason reason : mReasons)
        {
            final RadioButton radioButton = (RadioButton) LayoutInflater.from(getContext())
                    .inflate(R.layout.radio_button_dismissal_reason, mReasonsRadioGroup, false);
            radioButton.setText(reason.getDisplayName());
            radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
            {
                @Override
                public void onCheckedChanged(final CompoundButton compoundButton,
                                             final boolean isChecked)
                {
                    if (isChecked)
                    {
                        mSelectedReasonMachineName = reason.getMachineName();
                    }
                }
            });
            mReasonsRadioGroup.addView(radioButton);
        }
    }

    @Override
    protected void onConfirmBookingActionButtonClicked()
    {
        // FIXME: Set result here
        dismiss();
    }

    @Override
    protected int getConfirmButtonBackgroundResourceId()
    {
        return R.drawable.button_grey_round;
    }

    @Override
    protected String getConfirmButtonText()
    {
        return getString(R.string.dismiss_job);
    }
}
