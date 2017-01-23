package com.handy.portal.payments.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

import com.crashlytics.android.Crashlytics;
import com.handy.portal.R;
import com.handy.portal.core.constant.BundleKeys;
import com.handy.portal.core.constant.MainViewPage;
import com.handy.portal.core.constant.RequestCode;
import com.handy.portal.core.event.NavigationEvent;
import com.handy.portal.core.manager.ProviderManager;
import com.handy.portal.core.model.ProviderProfile;
import com.handy.portal.core.ui.fragment.ActionBarFragment;
import com.handy.portal.data.DataManager;
import com.handy.portal.data.callback.FragmentSafeCallback;
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
import com.handy.portal.payments.model.PaymentGroup;
import com.handy.portal.payments.model.PaymentReviewResponse;
import com.handy.portal.payments.model.PaymentSupportItem;
import com.handy.portal.payments.ui.element.PaymentDetailExpandableListView;
import com.handy.portal.payments.ui.element.PaymentsDetailListHeaderView;
import com.handy.portal.payments.ui.fragment.dialog.PaymentFailedDialogFragment;
import com.handy.portal.payments.ui.fragment.dialog.PaymentSupportReasonsDialogFragment;
import com.handy.portal.payments.ui.fragment.dialog.PaymentSupportRedirectToBookingTransactionsDialogFragment;
import com.handy.portal.payments.ui.fragment.dialog.PaymentSupportRequestReviewDialogFragment;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public final class PaymentsDetailFragment extends ActionBarFragment
        implements ExpandableListView.OnChildClickListener,
        PaymentSupportReasonsDialogFragment.Callback,
        PaymentSupportRequestReviewDialogFragment.Callback
{
    @BindView(R.id.payments_detail_list_view)
    PaymentDetailExpandableListView paymentDetailExpandableListView; //using ExpandableListView because it is the only ListView that offers group view support
    @BindView(R.id.payment_details_list_header)
    PaymentsDetailListHeaderView paymentsDetailListHeaderView;
    @BindView(R.id.fragment_payments_detail_support_button)
    Button mPaymentSupportButton;
    @BindView(R.id.fragment_payments_detail_content)
    CoordinatorLayout mMainContentLayout;

    private NeoPaymentBatch mNeoPaymentBatch;
    private View mFragmentView;

    @Inject
    ProviderManager mProviderManager;
    @Inject
    PaymentsManager mPaymentsManager;
    @Inject
    EventBus mBus;

    @Override
    protected MainViewPage getAppPage()
    {
        return MainViewPage.PAYMENTS;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if (getArguments() != null)
        {
            mNeoPaymentBatch = (NeoPaymentBatch) getArguments().getSerializable(BundleKeys.PAYMENT_BATCH);
        }
        else
        {
            Crashlytics.logException(new Exception("Null arguments for class " + this.getClass().getName()));
        }
    }

    @Override
    public final View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                                   final Bundle savedInstanceState)
    {
        if (mFragmentView == null)
        {
            mFragmentView = inflater
                    .inflate(R.layout.fragment_payments_detail, container, false);
        }
        ButterKnife.bind(this, mFragmentView);
        return mFragmentView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        paymentDetailExpandableListView.setOnChildClickListener(this);
        paymentsDetailListHeaderView.updateDisplay(mNeoPaymentBatch);
        paymentDetailExpandableListView.updateData(mNeoPaymentBatch);
        for (int i = 0; i < mNeoPaymentBatch.getPaymentGroups().length; i++)
        {
            paymentDetailExpandableListView.expandGroup(i);
        }
        if (NeoPaymentBatch.Status.FAILED.toString().equalsIgnoreCase(mNeoPaymentBatch.getStatus()))
        {
            //don't show the bottom payment support button if failed
            mPaymentSupportButton.setVisibility(View.GONE);
            //show dialog on ? button click that suggests why payment failed and directs user to update payment method
            paymentsDetailListHeaderView.setCallbackListener(new PaymentsDetailListHeaderView.Callback()
            {
                @Override
                public void onRequestStatusSupportButtonClicked()
                {
                    if (PaymentsDetailFragment.this.getChildFragmentManager()
                            .findFragmentByTag(PaymentFailedDialogFragment.FRAGMENT_TAG) == null)
                    {
                        PaymentFailedDialogFragment paymentBillBlockerDialogFragment =
                                PaymentFailedDialogFragment.newInstance();
                        FragmentUtils.safeLaunchDialogFragment(paymentBillBlockerDialogFragment,
                                PaymentsDetailFragment.this, PaymentFailedDialogFragment.FRAGMENT_TAG);

                    }
                }
            });
        }
        else
        {
            //if not failed, show payment support button
            mPaymentSupportButton.setVisibility(View.VISIBLE);
            //show payment support dialog on ? button click
            paymentsDetailListHeaderView.setCallbackListener(new PaymentsDetailListHeaderView.Callback()
            {
                @Override
                public void onRequestStatusSupportButtonClicked()
                {
                    PaymentsUtil.showPaymentSupportReasonsDialog(
                            PaymentsDetailFragment.this,
                            mNeoPaymentBatch.getPaymentSupportItems()
                    );
                }
            });
        }

    }

    @Override
    public void onResume()
    {
        super.onResume();
        setActionBar(R.string.payments_details, true);
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id)
    {
        final ExpandableListAdapter parentListAdapter = parent.getExpandableListAdapter();

        final PaymentGroup paymentGroup = (PaymentGroup) parentListAdapter.getGroup(groupPosition);
        bus.post(new LogEvent.AddLogEvent(
                new PaymentsLog.DetailSelected(paymentGroup.getMachineName())));

        final Payment payment = (Payment) parentListAdapter.getChild(groupPosition, childPosition);

        if (payment.getBookingId() == null || payment.getBookingType() == null) { return false; }

        showBookingDetails(payment.getBookingId(), payment.getBookingType());
        return true;
    }

    private void showBookingDetails(String bookingId, String bookingType)
    {
        Bundle arguments = new Bundle();
        arguments.putString(BundleKeys.BOOKING_ID, bookingId);
        arguments.putString(BundleKeys.BOOKING_TYPE, bookingType);
        NavigationEvent.NavigateToPage event =
                new NavigationEvent.NavigateToPage(MainViewPage.JOB_PAYMENT_DETAILS, arguments, true);
        bus.post(event);
    }

    @OnClick(R.id.fragment_payments_detail_support_button)
    public void onPaymentSupportButtonClicked()
    {
        PaymentsUtil.showPaymentSupportReasonsDialog(this, mNeoPaymentBatch.getPaymentSupportItems());
    }

    /**
     * show a new screen depending on which support item was submitted
     * @param paymentSupportItem
     */
    @Override
    public void onPaymentSupportItemSubmitted(PaymentSupportItem paymentSupportItem)
    {
        String itemMachineName = paymentSupportItem.getMachineName();

        mBus.post(new LogEvent.AddLogEvent(
                new PaymentsLog.BatchTransaction.SupportDialogSubmitted(
                        String.valueOf(mNeoPaymentBatch.getBatchId()),
                        itemMachineName,
                        null
                )));

        switch (itemMachineName)
        {
            case PaymentSupportItem.MachineName.MISSING_DEPOSIT:
            case PaymentSupportItem.MachineName.OTHER:
                showRequestPaymentReviewDialogFragment(paymentSupportItem);
                break;
            case PaymentSupportItem.MachineName.INCORRECT_AMOUNT:
            case PaymentSupportItem.MachineName.INCORRECT_FEE:
                showRedirectToBookingTransactionsDialogFragment(paymentSupportItem);
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
     * @param paymentSupportItem
     */
    private void showRequestPaymentReviewDialogFragment(PaymentSupportItem paymentSupportItem)
    {
        if (getChildFragmentManager().findFragmentByTag(PaymentSupportRequestReviewDialogFragment.FRAGMENT_TAG) == null)
        {
            //handle case in which we're unable to get the provider email
            ProviderProfile profile = mProviderManager.getCachedProviderProfile();
            if (profile == null
                    || profile.getProviderPersonalInfo() == null
                    || TextUtils.isNullOrEmpty(profile.getProviderPersonalInfo().getEmail()))
            {
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
            fragment.setTargetFragment(this, RequestCode.PAYMENT_SUPPORT_REQUEST_REVIEW_SUBMITTED);
            FragmentUtils.safeLaunchDialogFragment(fragment,
                    this,
                    PaymentSupportRequestReviewDialogFragment.FRAGMENT_TAG);
        }
    }

    /**
     * shows a dialog to prompt user to go to the booking transactions page
     * @param paymentSupportItem
     */
    private void showRedirectToBookingTransactionsDialogFragment(PaymentSupportItem paymentSupportItem)
    {
        if (getChildFragmentManager().findFragmentByTag(PaymentSupportRedirectToBookingTransactionsDialogFragment.FRAGMENT_TAG) == null)
        {
            String paymentSupportItemDisplayName = paymentSupportItem.getDisplayName();
            final DialogFragment fragment = PaymentSupportRedirectToBookingTransactionsDialogFragment.newInstance(paymentSupportItemDisplayName);
            FragmentUtils.safeLaunchDialogFragment(fragment,
                    this,
                    PaymentSupportRedirectToBookingTransactionsDialogFragment.FRAGMENT_TAG);
        }
    }

    /**
     * user requested a deposit review for this batch
     */
    @Override
    public void onRequestDepositReviewButtonClicked(@NonNull PaymentSupportItem paymentSupportItem)
    {
        BatchPaymentReviewRequest paymentReviewRequest
                = new BatchPaymentReviewRequest(
                String.valueOf(mNeoPaymentBatch.getBatchId()),
                paymentSupportItem.getMachineName(),
                null
        );

        bus.post(new LogEvent.AddLogEvent(new PaymentsLog.BatchTransaction.RequestReviewSubmitted(
                String.valueOf(mNeoPaymentBatch.getBatchId()),
                paymentSupportItem.getMachineName()

        )));

        //TODO remove, test only
//        UIUtils.getDefaultSnackbarWithImage(getContext(),
//                mMainContentLayout, "Test message", R.drawable.ic_green_envelope).show();

        //TODO: THIS ENDPOINT IS NOT IN PRODUCTION YET
        mPaymentsManager.submitBatchPaymentReviewRequest(paymentReviewRequest, new FragmentSafeCallback<PaymentReviewResponse>(this)
        {
            @Override
            public void onCallbackSuccess(final PaymentReviewResponse response)
            {
                int drawableResourceId = response.isSuccess() ?
                        R.drawable.ic_green_envelope : R.drawable.ic_exclaimation_red;
                UIUtils.getDefaultSnackbarWithImage(getContext(),
                        mMainContentLayout,
                        response.getMessage(),
                        drawableResourceId).show();
            }

            @Override
            public void onCallbackError(final DataManager.DataManagerError error)
            {
                UIUtils.getDefaultSnackbarWithImage(getContext(),
                        mMainContentLayout,
                        getString(R.string.an_error_has_occurred),
                        R.drawable.ic_exclaimation_red).show();
            }
        });
    }
}
