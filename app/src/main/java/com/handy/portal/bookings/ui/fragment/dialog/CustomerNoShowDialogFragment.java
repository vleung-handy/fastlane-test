package com.handy.portal.bookings.ui.fragment.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.handy.portal.R;
import com.handy.portal.bookings.model.Booking;
import com.handy.portal.core.constant.BundleKeys;
import com.handy.portal.core.constant.MainViewPage;
import com.handy.portal.core.constant.TransitionStyle;
import com.handy.portal.core.manager.PageNavigationManager;
import com.handy.portal.helpcenter.constants.HelpCenterConstants;
import com.handy.portal.library.ui.fragment.dialog.InjectedDialogFragment;
import com.handy.portal.library.ui.widget.BulletListItem;
import com.handy.portal.logger.handylogger.LogEvent;
import com.handy.portal.logger.handylogger.model.ScheduledJobsLog;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CustomerNoShowDialogFragment extends InjectedDialogFragment {
    @BindView(R.id.fragment_dialog_customer_no_show_instructions_list)
    LinearLayout mInstructionsList;
    @BindView(R.id.fragment_dialog_customer_no_show_payment_info_text)
    TextView mPaymentInfoText;

    @Inject
    EventBus mBus;
    @Inject
    PageNavigationManager mNavigationManager;

    private Booking mBooking;

    public static final String FRAGMENT_TAG = CustomerNoShowDialogFragment.class.getName();

    private static final int[] INSTRUCTION_LIST_STRING_RESOURCE_IDS = new int[]{
            R.string.customer_no_show_instructions_list_item_check_in,
            R.string.customer_no_show_instructions_list_item_contact_customer,
            R.string.customer_no_show_instructions_list_item_wait_for_customer
    };

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme);
        mBooking = (Booking) getArguments().getSerializable(BundleKeys.BOOKING); //should not be null
        if (mBooking == null) {
            Crashlytics.logException(new Exception("Booking is null in customer no show dialog fragment"));
            Toast.makeText(getContext(), R.string.error_fetching_booking_details, Toast.LENGTH_LONG).show();
            dismiss();
            return;
        }
        mBus.post(new LogEvent.AddLogEvent(new ScheduledJobsLog.CustomerNoShowModalShown(mBooking.getId())));
    }

    public static CustomerNoShowDialogFragment newInstance(@NonNull Booking booking) {
        CustomerNoShowDialogFragment fragment = new CustomerNoShowDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(BundleKeys.BOOKING, booking);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setWindowAnimations(R.style.dialog_animation_slide_up_down_from_bottom);
        }
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dialog_customer_no_show, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (mBooking == null) { return; }
        updateHeaderWithBookingPaymentInfo();
        populateInstructionsList(INSTRUCTION_LIST_STRING_RESOURCE_IDS);
    }

    private void updateHeaderWithBookingPaymentInfo() {
        mPaymentInfoText.setText(R.string.customer_no_show_payment_info);
    }

    private void populateInstructionsList(@NonNull final int[] instructionListStringResourceIds) {
        mInstructionsList.removeAllViews();
        for (int instructionListStringResourceId : instructionListStringResourceIds) {
            BulletListItem instructionListItem = new BulletListItem(getContext())
                    .setBulletDrawable(R.drawable.circle_grey)
                    .setBulletColorTint(R.color.white_pressed)
                    .setText(instructionListStringResourceId);

            mInstructionsList.addView(instructionListItem);
        }
    }

    @OnClick(R.id.fragment_dialog_customer_no_show_dismiss_button)
    public void onDismissButtonClicked() {
        dismiss();
    }

    @OnClick(R.id.fragment_dialog_customer_no_show_view_policy_button)
    public void onViewPolicyClicked() {
        showCustomerNoShowPolicyWebView();
    }

    private void showCustomerNoShowPolicyWebView() {
        final Bundle arguments = new Bundle();
        arguments.putString(BundleKeys.HELP_REDIRECT_PATH, HelpCenterConstants.CUSTOMER_NO_SHOW_POLICY_PATH);
        mNavigationManager.navigateToPage(getFragmentManager(), MainViewPage.HELP_WEBVIEW,
                arguments, TransitionStyle.NATIVE_TO_NATIVE, true);
    }

    @OnClick(R.id.fragment_dialog_customer_no_show_complete_report_button)
    public void onCompleteReportButtonClicked() {
        if (isRemoving() || isDetached()) { return; }
        OnReportCustomerNoShowButtonClickedListener onReportCustomerNoShowButtonClickedListener;
        try {
            /*
            NOTE: Google recommends the callback implementor to be an activity
            but not doing that here because our app currently isn't modularized by activities enough
            so don't want to put all callbacks in MainActivity

            also not using getTargetFragment() because of this:
            https://code.google.com/p/android/issues/detail?id=54520
             */
            onReportCustomerNoShowButtonClickedListener
                    = (OnReportCustomerNoShowButtonClickedListener) getParentFragment();

        }
        catch (ClassCastException e) //should never happen
        {
            throw new ClassCastException(getParentFragment().toString() +
                    " must implement " + OnReportCustomerNoShowButtonClickedListener.class.getName());
        }

        /*
            note that DialogFragments do not trigger the parent fragment
            to call onPause() which means bus is still registered at this point
             */
        //app fatality on null listener is intentional
        onReportCustomerNoShowButtonClickedListener.onReportCustomerNoShowButtonClicked();
        dismiss();
    }

    public interface OnReportCustomerNoShowButtonClickedListener {
        void onReportCustomerNoShowButtonClicked();
    }
}
