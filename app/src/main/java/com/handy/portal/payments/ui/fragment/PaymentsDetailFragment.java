package com.handy.portal.payments.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.handy.portal.R;
import com.handy.portal.core.constant.BundleKeys;
import com.handy.portal.core.constant.MainViewPage;
import com.handy.portal.core.event.HandyEvent;
import com.handy.portal.core.manager.ConfigManager;
import com.handy.portal.core.manager.PageNavigationManager;
import com.handy.portal.core.manager.ProviderManager;
import com.handy.portal.core.model.ProviderProfile;
import com.handy.portal.core.ui.fragment.ActionBarFragment;
import com.handy.portal.data.DataManager;
import com.handy.portal.data.callback.FragmentSafeCallback;
import com.handy.portal.library.ui.fragment.dialog.InfoDialogFragment;
import com.handy.portal.library.util.DateTimeUtils;
import com.handy.portal.library.util.FragmentUtils;
import com.handy.portal.library.util.TextUtils;
import com.handy.portal.library.util.UIUtils;
import com.handy.portal.logger.handylogger.LogEvent;
import com.handy.portal.logger.handylogger.model.PaymentsLog;
import com.handy.portal.payments.PaymentsManager;
import com.handy.portal.payments.PaymentsUtil;
import com.handy.portal.payments.model.BatchPaymentReviewRequest;
import com.handy.portal.payments.model.NeoPaymentBatch;
import com.handy.portal.payments.model.Payment;
import com.handy.portal.payments.model.PaymentBatches;
import com.handy.portal.payments.model.PaymentGroup;
import com.handy.portal.payments.model.PaymentReviewResponse;
import com.handy.portal.payments.model.PaymentSupportItem;
import com.handy.portal.payments.ui.element.PaymentDetailExpandableListView;
import com.handy.portal.payments.ui.element.PaymentsDetailListHeaderView;
import com.handy.portal.payments.ui.fragment.dialog.PaymentCashOutDialogFragment;
import com.handy.portal.payments.ui.fragment.dialog.PaymentFailedDialogFragment;
import com.handy.portal.payments.ui.fragment.dialog.PaymentSupportReasonsDialogFragment;
import com.handy.portal.payments.ui.fragment.dialog.PaymentSupportRequestReviewDialogFragment;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public final class PaymentsDetailFragment extends ActionBarFragment
        implements ExpandableListView.OnChildClickListener,
        PaymentSupportReasonsDialogFragment.Callback,
        PaymentSupportRequestReviewDialogFragment.Callback,
        PaymentCashOutDialogFragment.OnCashOutSuccessListener {
    @BindView(R.id.payments_detail_list_view)
    PaymentDetailExpandableListView paymentDetailExpandableListView; //using ExpandableListView because it is the only ListView that offers group view support
    @BindView(R.id.fragment_payments_detail_content)
    CoordinatorLayout mMainContentLayout;

    private NeoPaymentBatch mNeoPaymentBatch;
    private boolean mIsCurrentWeekPaymentBatch;
    /**
     * used to determine what happens when the cash out button is clicked
     */
    private PaymentBatches.OneTimeCashOutInfo mOneTimeCashOutInfo;
    private View mFragmentView;

    @Inject
    ProviderManager mProviderManager;
    @Inject
    PaymentsManager mPaymentsManager;
    @Inject
    ConfigManager mConfigManager;
    @Inject
    EventBus mBus;
    @Inject
    PageNavigationManager mNavigationManager;

    @Override
    protected MainViewPage getAppPage() {
        return MainViewPage.PAYMENTS;
    }

    public static Bundle createBundle(@NonNull NeoPaymentBatch neoPaymentBatch,
                                      boolean isCurrentWeekPaymentBatch,
                                      @Nullable PaymentBatches.OneTimeCashOutInfo oneTimeCashOutInfo) {
        Bundle arguments = new Bundle();
        arguments.putSerializable(BundleKeys.PAYMENT_BATCH, neoPaymentBatch);
        arguments.putBoolean(BundleKeys.IS_CURRENT_WEEK_PAYMENT_BATCH, isCurrentWeekPaymentBatch);
        arguments.putSerializable(BundleKeys.PAYMENT_CASH_OUT_INFO, oneTimeCashOutInfo);
        return arguments;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mNeoPaymentBatch = (NeoPaymentBatch) getArguments().getSerializable(BundleKeys.PAYMENT_BATCH);
            mIsCurrentWeekPaymentBatch = getArguments().getBoolean(BundleKeys.IS_CURRENT_WEEK_PAYMENT_BATCH, false);
            mOneTimeCashOutInfo = (PaymentBatches.OneTimeCashOutInfo) getArguments().getSerializable(BundleKeys.PAYMENT_CASH_OUT_INFO);
        }
        else {
            Crashlytics.logException(new Exception("Null arguments for class " + this.getClass().getName()));
        }
    }

    @Override
    public final View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                                   final Bundle savedInstanceState) {
        if (mFragmentView == null) {
            mFragmentView = inflater
                    .inflate(R.layout.fragment_payments_detail, container, false);
        }
        ButterKnife.bind(this, mFragmentView);
        return mFragmentView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        paymentDetailExpandableListView.setOnChildClickListener(this);

        paymentDetailExpandableListView.updateData(
                mNeoPaymentBatch,
                mConfigManager.getConfigurationResponse().isDailyProPaymentsEnabled()
                        && mIsCurrentWeekPaymentBatch);
        paymentDetailExpandableListView.getPaymentSupportButton().setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        PaymentsUtil.showPaymentSupportReasonsDialog(PaymentsDetailFragment.this, mNeoPaymentBatch.getPaymentSupportItems());
                    }
                }
        );
        for (int i = 0; i < mNeoPaymentBatch.getPaymentGroups().length; i++) {
            paymentDetailExpandableListView.expandGroup(i);
        }

        //adding below in case this gets transformed into an updateDisplay(NeoPaymentBatch) method
        //not ideal but may be refactored later
        paymentDetailExpandableListView.getHeaderView().setPaymentStatusHelpButtonVisible(true);
        if (NeoPaymentBatch.Status.FAILED.equalsIgnoreCase(mNeoPaymentBatch.getStatus())) {
            //don't show the bottom payment support button if failed
            paymentDetailExpandableListView.getPaymentSupportButton().setVisibility(View.GONE);
            //show dialog on ? button click that suggests why payment failed and directs user to update payment method
            paymentDetailExpandableListView.getHeaderView().setCallbackListener(new PaymentsDetailListHeaderView.Callback() {
                @Override
                public void onRequestStatusSupportButtonClicked() {
                    if (PaymentsDetailFragment.this.getChildFragmentManager()
                            .findFragmentByTag(PaymentFailedDialogFragment.FRAGMENT_TAG) == null) {
                        PaymentFailedDialogFragment paymentBillBlockerDialogFragment =
                                PaymentFailedDialogFragment.newInstance();
                        FragmentUtils.safeLaunchDialogFragment(paymentBillBlockerDialogFragment,
                                PaymentsDetailFragment.this, PaymentFailedDialogFragment.FRAGMENT_TAG);

                    }
                }
            });
        }
        else //if not failed
        {
            View.OnClickListener onCashOutButtonClickedListener =
                    PaymentsUtil.CashOut.createCashOutButtonClickListener(
                            this,
                            mNeoPaymentBatch.isCashOutEnabled(),
                            mOneTimeCashOutInfo,
                            bus
                    );
            paymentDetailExpandableListView.getHeaderView()
                    .setOnCashOutButtonClickListener(onCashOutButtonClickedListener);
            if (mNeoPaymentBatch.getPaymentSupportItems() == null
                    || mNeoPaymentBatch.getPaymentSupportItems().length == 0) {
                //don't show any payment supports that are based on the payment support items
                paymentDetailExpandableListView.getPaymentSupportButton().setVisibility(View.GONE);
                paymentDetailExpandableListView.getHeaderView().setPaymentStatusHelpButtonVisible(false);
            }
            else {
                paymentDetailExpandableListView.getPaymentSupportButton().setVisibility(View.VISIBLE);
                //show payment support dialog on ? button click
                paymentDetailExpandableListView.getHeaderView().setCallbackListener(new PaymentsDetailListHeaderView.Callback() {
                    @Override
                    public void onRequestStatusSupportButtonClicked() {
                        PaymentsUtil.showPaymentSupportReasonsDialog(
                                PaymentsDetailFragment.this,
                                mNeoPaymentBatch.getPaymentSupportItems()
                        );
                    }
                });
            }
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        setActionBar(R.string.payments_details, true);
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        final ExpandableListAdapter parentListAdapter = parent.getExpandableListAdapter();

        final PaymentGroup paymentGroup = (PaymentGroup) parentListAdapter.getGroup(groupPosition);
        bus.post(new LogEvent.AddLogEvent(
                new PaymentsLog.DetailSelected(paymentGroup.getMachineName())));

        final Payment payment = (Payment) parentListAdapter.getChild(groupPosition, childPosition);

        if (payment.getBookingId() == null || payment.getBookingType() == null) { return false; }

        showBookingDetails(payment.getBookingId(), payment.getBookingType());
        return true;
    }

    private void showBookingDetails(String bookingId, String bookingType) {
        Bundle arguments = new Bundle();
        arguments.putString(BundleKeys.BOOKING_ID, bookingId);
        arguments.putString(BundleKeys.BOOKING_TYPE, bookingType);
        mNavigationManager.navigateToPage(getFragmentManager(), MainViewPage.JOB_PAYMENT_DETAILS,
                arguments, null, true);
    }

    /**
     * show a new screen depending on which support item was submitted
     *
     * @param paymentSupportItem
     */
    @Override
    public void onPaymentSupportItemSubmitted(PaymentSupportItem paymentSupportItem) {
        String itemMachineName = paymentSupportItem.getMachineName();

        mBus.post(new LogEvent.AddLogEvent(
                new PaymentsLog.BatchTransaction.SupportDialogSubmitted(
                        String.valueOf(mNeoPaymentBatch.getBatchId()),
                        itemMachineName,
                        null
                )));

        switch (itemMachineName) {
            case PaymentSupportItem.MachineName.MISSING_DEPOSIT:
            case PaymentSupportItem.MachineName.OTHER:
                showRequestPaymentReviewDialogFragment(paymentSupportItem);
                break;
            case PaymentSupportItem.MachineName.INCORRECT_AMOUNT:
                showRedirectToBookingTransactionsDialogFragment(paymentSupportItem.getDisplayName(),
                        getString(R.string.payment_support_redirect_to_booking_transactions_dialog_payment_instructions));
                break;
            case PaymentSupportItem.MachineName.INCORRECT_FEE:
                showRedirectToBookingTransactionsDialogFragment(paymentSupportItem.getDisplayName(),
                        getString(R.string.payment_support_redirect_to_booking_transactions_dialog_fee_instructions));
                break;
            default:
                //still show request payment review dialog if we get unrecognized machine name
                Crashlytics.logException(new Exception("Unknown payment support reason machine name: " + itemMachineName));
                showRequestPaymentReviewDialogFragment(paymentSupportItem);
                break;
        }
    }

    /**
     * shows a dialog to confirm that the user wants to request a payment review for this batch
     *
     * @param paymentSupportItem
     */
    private void showRequestPaymentReviewDialogFragment(PaymentSupportItem paymentSupportItem) {
        if (getChildFragmentManager().findFragmentByTag(PaymentSupportRequestReviewDialogFragment.FRAGMENT_TAG) == null) {
            //handle case in which we're unable to get the provider email
            ProviderProfile profile = mProviderManager.getCachedProviderProfile();
            if (profile == null
                    || profile.getProviderPersonalInfo() == null
                    || TextUtils.isNullOrEmpty(profile.getProviderPersonalInfo().getEmail())) {
                //should never happen
                Crashlytics.logException(new Exception("Unable to get provider email from cached provider profile"));
                showToast(R.string.error_missing_server_data);
                return;
            }
            final DialogFragment fragment = PaymentSupportRequestReviewDialogFragment.newInstance(
                    profile.getProviderPersonalInfo().getEmail(),
                    DateTimeUtils.formatDayOfWeekMonthDateYear(mNeoPaymentBatch.getExpectedDepositDate()),
                    paymentSupportItem
            );
            FragmentUtils.safeLaunchDialogFragment(fragment,
                    this,
                    PaymentSupportRequestReviewDialogFragment.FRAGMENT_TAG);
        }
    }

    /**
     * shows a dialog to prompt user to go to the booking transactions page
     */
    private void showRedirectToBookingTransactionsDialogFragment(@NonNull String titleText, @NonNull String messageText) {
        if (getChildFragmentManager().findFragmentByTag(InfoDialogFragment.FRAGMENT_TAG) == null) {
            final DialogFragment fragment = InfoDialogFragment.newInstance(titleText, messageText);
            FragmentUtils.safeLaunchDialogFragment(fragment,
                    this,
                    InfoDialogFragment.FRAGMENT_TAG);
        }
    }

    /**
     * user requested a deposit review for this batch
     */
    @Override
    public void onRequestDepositReviewButtonClicked(@NonNull PaymentSupportItem paymentSupportItem) {
        BatchPaymentReviewRequest paymentReviewRequest
                = new BatchPaymentReviewRequest(
                String.valueOf(mNeoPaymentBatch.getBatchId()),
                mNeoPaymentBatch.getStartDate(),
                mNeoPaymentBatch.getEndDate(),
                paymentSupportItem.getMachineName(),
                null
        );

        bus.post(new LogEvent.AddLogEvent(new PaymentsLog.BatchTransaction.RequestReviewSubmitted(
                String.valueOf(mNeoPaymentBatch.getBatchId()),
                paymentSupportItem.getMachineName()

        )));

        bus.post(new HandyEvent.SetLoadingOverlayVisibility(true));
        mPaymentsManager.submitBatchPaymentReviewRequest(paymentReviewRequest,
                new FragmentSafeCallback<PaymentReviewResponse>(this) {
                    @Override
                    public void onCallbackSuccess(final PaymentReviewResponse response) {
                        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
                        int drawableResourceId = response.isSuccess() ?
                                R.drawable.ic_green_envelope : R.drawable.ic_exclaimation_red;
                        UIUtils.getDefaultSnackbarWithImage(getContext(),
                                mMainContentLayout,
                                response.getMessage(),
                                drawableResourceId).show();
                    }

                    @Override
                    public void onCallbackError(final DataManager.DataManagerError error) {
                        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
                        UIUtils.getDefaultSnackbarWithImage(getContext(),
                                mMainContentLayout,
                                getString(R.string.an_error_has_occurred),
                                R.drawable.ic_exclaimation_red).show();
                    }
                });
    }

    @Override
    public void onCashOutSuccess(@NonNull final String message) {
        //parent fragment is null, not payments fragment, so can't use its callback
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        mNavigationManager.navigateToPage(getFragmentManager(), MainViewPage.PAYMENTS, null, null, false);
    }
}
