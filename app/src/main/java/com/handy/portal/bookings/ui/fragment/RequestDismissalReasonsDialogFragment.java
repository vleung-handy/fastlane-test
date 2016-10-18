package com.handy.portal.bookings.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.handy.portal.R;
import com.handy.portal.bookings.model.Booking;
import com.handy.portal.bookings.ui.fragment.dialog.ConfirmBookingActionDialogFragment;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.library.util.UIUtils;
import com.handy.portal.logger.handylogger.LogEvent;
import com.handy.portal.logger.handylogger.model.RequestedJobsLog;
import com.handy.portal.model.ConfigurationResponse.RequestDismissal;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;

public class RequestDismissalReasonsDialogFragment extends ConfirmBookingActionDialogFragment
{
    @Inject
    EventBus mBus;

    private static final String KEY_REASONS = "reasons";

    @BindView(R.id.request_dismissal_reasons_radio_group)
    RadioGroup mReasonsRadioGroup;
    @BindView(R.id.request_dismissal_other_edit_text)
    EditText mOtherEditText;

    private ArrayList<RequestDismissal.Reason> mReasons;
    private String mSelectedReasonMachineName;

    public static RequestDismissalReasonsDialogFragment newInstance(
            final Booking booking,
            final ArrayList<RequestDismissal.Reason> reasons)
    {
        final RequestDismissalReasonsDialogFragment dialogFragment =
                new RequestDismissalReasonsDialogFragment();
        final Bundle arguments = new Bundle();
        arguments.putSerializable(BundleKeys.BOOKING, booking);
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
                        final String reasonMachineName = reason.getMachineName();
                        mSelectedReasonMachineName = reasonMachineName;
                        // Show keyboard if "other" option is selected, otherwise dismiss it
                        if (RequestDismissal.Reason.MACHINE_NAME_OTHER.equals(reasonMachineName))
                        {
                            mOtherEditText.setVisibility(View.VISIBLE);
                            mOtherEditText.requestFocus();
                            UIUtils.showKeyboard(mOtherEditText);
                        }
                        else
                        {
                            mOtherEditText.setVisibility(View.GONE);
                            UIUtils.dismissKeyboard(mOtherEditText);
                        }
                    }
                }
            });
            mReasonsRadioGroup.addView(radioButton);
        }
        mBus.post(new LogEvent.AddLogEvent(new RequestedJobsLog.DismissJobShown(mBooking)));
    }

    @Override
    protected void onConfirmBookingActionButtonClicked()
    {
        if (mSelectedReasonMachineName == null)
        {
            UIUtils.showToast(getActivity(),
                    getString(R.string.request_dismissal_reason_not_selected), Toast.LENGTH_SHORT);
            return;
        }
        String reasonDescription = null;
        if (RequestDismissal.Reason.MACHINE_NAME_OTHER.equals(mSelectedReasonMachineName))
        {
            reasonDescription = mOtherEditText.getText().toString();
            if (TextUtils.isEmpty(reasonDescription))
            {
                UIUtils.showToast(getActivity(),
                        getString(R.string.request_dismissal_reason_description_missing),
                        Toast.LENGTH_SHORT);
                return;
            }
        }
        final Intent intent = new Intent();
        intent.putExtra(BundleKeys.BOOKING, mBooking);
        intent.putExtra(BundleKeys.REASON_MACHINE_NAME, mSelectedReasonMachineName);
        intent.putExtra(BundleKeys.REASON_DESCRIPTION, reasonDescription);
        if (getTargetFragment() != null)
        {
            getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
        }
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
        return getString(R.string.dismiss_request);
    }
}
