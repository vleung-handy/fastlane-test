package com.handy.portal.clients.ui.fragment.dialog;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.handy.portal.R;
import com.handy.portal.bookings.manager.BookingManager;
import com.handy.portal.bookings.model.Booking;
import com.handy.portal.bookings.ui.fragment.dialog.ConfirmBookingActionDialogFragment;
import com.handy.portal.core.constant.BundleKeys;
import com.handy.portal.library.util.UIUtils;
import com.handy.portal.logger.handylogger.LogEvent;
import com.handy.portal.logger.handylogger.model.RequestedJobsLog;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

import butterknife.BindView;

public class RequestDismissalReasonsDialogFragment extends ConfirmBookingActionDialogFragment {
    @Inject
    EventBus mBus;

    @BindView(R.id.request_dismissal_reasons_radio_group)
    RadioGroup mReasonsRadioGroup;
    @BindView(R.id.request_dismissal_dialog_title)
    TextView mTitle;

    private String mSelectedReasonMachineName;

    private static final String[] REASON_MACHINE_NAMES = {
            BookingManager.DISMISSAL_REASON_UNSPECIFIED,
            BookingManager.DISMISSAL_REASON_BLOCK_CUSTOMER
    };
    private static final int[] REASON_DISPLAY_NAMES = {
            R.string.yes,
            R.string.request_dismissal_block_client
    };

    public static RequestDismissalReasonsDialogFragment newInstance(final Booking booking) {
        final RequestDismissalReasonsDialogFragment dialogFragment =
                new RequestDismissalReasonsDialogFragment();
        final Bundle arguments = new Bundle();
        arguments.putSerializable(BundleKeys.BOOKING, booking);
        dialogFragment.setArguments(arguments);
        return dialogFragment;
    }

    @Override
    protected View inflateConfirmActionContentView(final LayoutInflater inflater, final ViewGroup container) {
        return inflater.inflate(R.layout.fragment_dialog_request_dismissal_reasons, container, false);
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        hideDismissButton();
        mTitle.setText(getString(R.string.request_dismissal_dialog_title_formatted,
                mBooking.getRequestAttributes().getCustomerName()));
        for (int i = 0; i < REASON_MACHINE_NAMES.length; i++) {
            final String reasonMachineName = REASON_MACHINE_NAMES[i];
            final RadioButton radioButton = (RadioButton) LayoutInflater.from(getContext())
                    .inflate(R.layout.radio_button_dismissal_reason, mReasonsRadioGroup, false);
            radioButton.setText(REASON_DISPLAY_NAMES[i]);
            radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(final CompoundButton compoundButton,
                                             final boolean isChecked) {
                    if (isChecked) {
                        mSelectedReasonMachineName = reasonMachineName;
                    }
                }
            });
            mReasonsRadioGroup.addView(radioButton);
        }
        mBus.post(new LogEvent.AddLogEvent(new RequestedJobsLog.DismissJobShown(mBooking)));
    }

    @Override
    protected void onConfirmActionButtonClicked() {
        if (mSelectedReasonMachineName == null) {
            UIUtils.showToast(getActivity(),
                    getString(R.string.request_dismissal_reason_not_selected), Toast.LENGTH_SHORT);
            return;
        }
        final Intent intent = new Intent();
        intent.putExtra(BundleKeys.BOOKING, mBooking);
        intent.putExtra(BundleKeys.DISMISSAL_REASON, mSelectedReasonMachineName);
        if (getTargetFragment() != null) {
            getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
        }
        dismiss();
    }

    @Override
    protected int getConfirmButtonBackgroundResourceId() {
        return R.drawable.button_grey_round;
    }

    @Override
    protected String getConfirmButtonText() {
        return getString(R.string.dismiss_request);
    }
}
