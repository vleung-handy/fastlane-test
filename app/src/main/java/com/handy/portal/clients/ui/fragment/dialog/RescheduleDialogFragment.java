package com.handy.portal.clients.ui.fragment.dialog;

import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.handy.portal.R;
import com.handy.portal.availability.manager.AvailabilityManager;
import com.handy.portal.bookings.model.Booking;
import com.handy.portal.bookings.ui.fragment.dialog.ConfirmBookingActionDialogFragment;
import com.handy.portal.core.constant.BundleKeys;
import com.handy.portal.core.constant.MainViewPage;
import com.handy.portal.core.event.HandyEvent;
import com.handy.portal.core.event.NavigationEvent;
import com.handy.portal.core.manager.ConfigManager;
import com.handy.portal.core.manager.ProviderManager;
import com.handy.portal.data.DataManager;
import com.handy.portal.data.callback.FragmentSafeCallback;
import com.handy.portal.helpcenter.constants.HelpCenterConstants;
import com.handy.portal.library.util.DateTimeUtils;
import com.handy.portal.library.util.TextUtils;
import com.handy.portal.logger.handylogger.model.EventContext;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

import butterknife.BindView;

public class RescheduleDialogFragment extends ConfirmBookingActionDialogFragment {
    @Inject
    AvailabilityManager mAvailabilityManager;
    @Inject
    DataManager mDataManager;
    @Inject
    ConfigManager mConfigManager;
    @Inject
    ProviderManager mProviderManager;
    @Inject
    EventBus mBus;

    @BindView(R.id.reschedule_dialog_title)
    TextView mTitle;
    @BindView(R.id.reschedule_dialog_subtitle)
    TextView mSubtitle;
    @BindView(R.id.reschedule_dialog_input)
    EditText mInput;

    private boolean mHasAvailableHours;

    public static RescheduleDialogFragment newInstance(final Booking booking) {
        final RescheduleDialogFragment dialogFragment = new RescheduleDialogFragment();
        final Bundle arguments = new Bundle();
        arguments.putSerializable(BundleKeys.BOOKING, booking);
        dialogFragment.setArguments(arguments);
        return dialogFragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHasAvailableHours = mAvailabilityManager.isReady()
                && mAvailabilityManager.hasAvailableHours();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        final Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        return dialog;
    }

    @Override
    protected View inflateConfirmActionContentView(
            final LayoutInflater inflater,
            final ViewGroup container
    ) {
        return inflater.inflate(R.layout.fragment_dialog_reschedule, container, false);
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        hideDismissButton();
        final String customerName = mBooking.getRequestAttributes().getCustomerFirstName();
        if (mHasAvailableHours) {
            mTitle.setText(getString(R.string.reschedule_with_formatted, customerName));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                mSubtitle.setText(Html.fromHtml(
                        getString(R.string.reschedule_prompt), Html.FROM_HTML_MODE_LEGACY)
                );
            }
            else {
                mSubtitle.setText(Html.fromHtml(getString(R.string.reschedule_prompt)));
            }
            TextUtils.stripUnderlines(mSubtitle);
            mSubtitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    navigateToEditWeeklyAvailability();
                }
            });
            mInput.setVisibility(View.VISIBLE);
            mInput.append(getString(
                    R.string.reschedule_message_formatted,
                    customerName,
                    DateTimeUtils.formatDetailedDate(mBooking.getStartDate())
            ));
        }
        else {
            mTitle.setText(R.string.reschedule_no_availability);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                mSubtitle.setText(Html.fromHtml(getString(
                        R.string.reschedule_set_availability_prompt_formatted, customerName
                ), Html.FROM_HTML_MODE_LEGACY));
            }
            else {
                mSubtitle.setText(Html.fromHtml(getString(
                        R.string.reschedule_set_availability_prompt_formatted, customerName
                )));
            }
            TextUtils.stripUnderlines(mSubtitle);
            mSubtitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    navigateToHelp();
                }
            });
            mInput.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onConfirmActionButtonClicked() {
        if (mHasAvailableHours) {
            final Booking.Action action = mBooking.getAction(Booking.Action.ACTION_SEND_TIMES);
            mDataManager.sendAvailability(
                    mProviderManager.getLastProviderId(),
                    action.getProviderRequest().getId(),
                    mInput.getText().toString(),
                    new FragmentSafeCallback<Void>(this) {
                        @Override
                        public void onCallbackSuccess(final Void response) {
                            mBus.post(new HandyEvent.AvailableHoursSent(mBooking));
                            Toast.makeText(
                                    getActivity(),
                                    getString(
                                            R.string.reschedule_message_sent_formatted,
                                            mBooking.getRequestAttributes().getCustomerFirstName()
                                    ),
                                    Toast.LENGTH_LONG
                            ).show();
                            dismiss();
                        }

                        @Override
                        public void onCallbackError(final DataManager.DataManagerError error) {
                            String errorMessage = error.getMessage();
                            if (TextUtils.isNullOrEmpty(errorMessage)) {
                                errorMessage = getString(R.string.reschedule_message_error);
                            }
                            Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_LONG).show();
                        }
                    }
            );
        }
        else {
            navigateToEditWeeklyAvailability();
        }
    }

    @Override
    protected int getConfirmButtonBackgroundResourceId() {
        return mHasAvailableHours ? R.color.tertiary_gray : R.color.error_red;
    }

    @Override
    protected String getConfirmButtonText() {
        return getString(mHasAvailableHours ? R.string.send_message : R.string.set_my_availability);
    }

    private void navigateToEditWeeklyAvailability() {
        final Bundle arguments = new Bundle();
        arguments.putString(BundleKeys.FLOW_CONTEXT, EventContext.AVAILABILITY);
        mBus.post(new NavigationEvent.NavigateToPage(
                mConfigManager.getConfigurationResponse().isTemplateAvailabilityEnabled()
                        ? MainViewPage.EDIT_WEEKLY_TEMPLATE_AVAILABLE_HOURS
                        : MainViewPage.EDIT_WEEKLY_ADHOC_AVAILABLE_HOURS,
                arguments,
                true
        ));
        dismiss();
    }

    private void navigateToHelp() {
        final Bundle arguments = new Bundle();
        arguments.putString(
                BundleKeys.TARGET_URL,
                mDataManager.getBaseUrl() + HelpCenterConstants.SETTING_HOURS_INFO_PATH
        );
        mBus.post(new NavigationEvent.NavigateToPage(MainViewPage.WEB_PAGE, arguments, true));
        dismiss();
    }
}
