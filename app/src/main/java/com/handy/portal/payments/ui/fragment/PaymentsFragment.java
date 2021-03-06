package com.handy.portal.payments.ui.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.handy.portal.R;
import com.handy.portal.core.constant.BundleKeys;
import com.handy.portal.core.constant.MainViewPage;
import com.handy.portal.core.manager.ConfigManager;
import com.handy.portal.core.manager.PageNavigationManager;
import com.handy.portal.core.manager.ProviderManager;
import com.handy.portal.core.model.SuccessWrapper;
import com.handy.portal.core.ui.activity.FragmentContainerActivity;
import com.handy.portal.core.ui.fragment.ActionBarFragment;
import com.handy.portal.data.DataManager;
import com.handy.portal.data.callback.FragmentSafeCallback;
import com.handy.portal.library.ui.layout.SlideUpPanelLayout;
import com.handy.portal.library.ui.widget.InfiniteScrollListView;
import com.handy.portal.library.util.DateTimeUtils;
import com.handy.portal.logger.handylogger.model.PaymentsLog;
import com.handy.portal.payments.PaymentsManager;
import com.handy.portal.payments.PaymentsUtil;
import com.handy.portal.payments.model.NeoPaymentBatch;
import com.handy.portal.payments.model.PaymentBatch;
import com.handy.portal.payments.model.PaymentBatches;
import com.handy.portal.payments.model.RecurringCashOutRequest;
import com.handy.portal.payments.ui.adapter.PaymentBatchListAdapter;
import com.handy.portal.payments.ui.element.DailyCashOutToggleContainerView;
import com.handy.portal.payments.ui.element.PaymentsBatchListView;
import com.handy.portal.payments.ui.fragment.dialog.AdhocCashOutDialogFragment;
import com.handy.portal.webview.PortalWebViewFragment;

import java.util.Calendar;
import java.util.Date;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PaymentsFragment extends ActionBarFragment implements AdhocCashOutDialogFragment.OnCashOutSuccessListener {
    private static final String HELP_PAYMENTS_SECTION_REDIRECT_PATH = "/sections/203828247";

    @Inject
    ConfigManager mConfigManager;
    @Inject
    PaymentsManager mPaymentsManager;
    @Inject
    PageNavigationManager mNavigationManager;

    @Inject
    ProviderManager mProviderManager;

    //TODO: investigate using @Produce and make manager handle more of this logic
    @BindView(R.id.slide_up_panel_container)
    SlideUpPanelLayout mSlideUpPanelLayout;

    @BindView(R.id.payments_scroll_view)
    ScrollView scrollView;

    @BindView(R.id.payments_batch_list_view)
    PaymentsBatchListView paymentsBatchListView;

    @BindView(R.id.fetch_error_text)
    TextView fetchErrorText;

    @BindView(R.id.fetch_error_view)
    ViewGroup fetchErrorView;

    //TODO: refactor request protocols when we can use new pagination API that allows us to get the N next batches

    private ViewGroup fragmentView;

    /**
     * used for a hack only
     */
    private Bundle mPreviousSavedInstanceState;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /*

        this is a hack to fix an issue in which the view isn't immediately redrawn
        (opening the menu drawer seems to trigger a redraw) when returning to this fragment from another activity

        TODO ideally, we should have a non-hacky way of instantly restoring the list view state (including scroll position)
         */
        boolean wasActivityJustRecreated = mPreviousSavedInstanceState != null;
        if (fragmentView == null || wasActivityJustRecreated) {
            fragmentView = (ViewGroup) super.onCreateView(inflater, container, savedInstanceState);
            fragmentView.addView(inflater.inflate(R.layout.fragment_payments, container, false));
        }

        mPreviousSavedInstanceState = savedInstanceState;

        ButterKnife.bind(this, fragmentView);

        return fragmentView;
    }

    @Override
    protected MainViewPage getAppPage() {
        return MainViewPage.PAYMENTS;
    }

    @Override
    protected void showProgressSpinner(final boolean isBlocking) {
        super.showProgressSpinner(isBlocking);
        scrollView.setVisibility(View.GONE);
    }

    @Override
    protected void hideProgressSpinner() {
        scrollView.setVisibility(View.VISIBLE);
        super.hideProgressSpinner();
    }

    @Override
    public void onResume() {
        super.onResume();

        setActionBar(R.string.payments, true);

        if (paymentsBatchListView.isDataEmpty() && paymentsBatchListView.shouldRequestMoreData())//if initial batch has not been received yet
        {
            requestInitialPaymentsInfo();
        }
        else {
            hideProgressSpinner();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_payments, menu);
    }

    @OnClick(R.id.try_again_button)
    public void doInitialRequestAgain() {
        requestInitialPaymentsInfo();
    }

    private void requestInitialPaymentsInfo() {
        requestNextPaymentBatches(true);
        showProgressSpinner(true);
    }

    private FragmentSafeCallback mCurrentPaymentBatchCallback;

    private void requestNextPaymentBatches(final boolean isInitialRequest) {
        if (isInitialRequest) {
            paymentsBatchListView.clear();
        }
        final Date endDate = paymentsBatchListView.getNextRequestEndDate();

        if (endDate != null) {
            Calendar c = Calendar.getInstance();
            c.setTime(endDate);
            c.add(Calendar.DATE, -PaymentBatchListAdapter.DAYS_TO_REQUEST_PER_BATCH);
            final Date startDate = DateTimeUtils.getBeginningOfDay(c.getTime());
            if (mCurrentPaymentBatchCallback != null) {
                //only one payment batch request at a time
                mCurrentPaymentBatchCallback.cancel();
            }
            mCurrentPaymentBatchCallback
                    = new FragmentSafeCallback<PaymentBatches>(this) {
                @Override
                public void onCallbackSuccess(PaymentBatches response) {
                    onReceivePaymentBatchesSuccess(response,
                            startDate,
                            endDate,
                            isInitialRequest);
                }

                @Override
                public void onCallbackError(DataManager.DataManagerError error) {
                    onReceivePaymentBatchesError(error);
                }
            };
            mPaymentsManager.requestPaymentBatches(startDate, endDate, mCurrentPaymentBatchCallback);
            paymentsBatchListView.showFooter(R.string.loading_more_payments);
        }
        else {
            paymentsBatchListView.showFooter(R.string.no_more_payments);
        }
    }

    private void updateCashOutButtonClickListener(@Nullable PaymentBatches.AdhocCashOutInfo adhocCashOutInfo, boolean isCashOutEnabled) {
        View.OnClickListener onClickListener = PaymentsUtil.CashOut.createCashOutButtonClickListener(
                this,
                isCashOutEnabled,
                adhocCashOutInfo,
                bus);
        paymentsBatchListView.setCashOutButtonClickListener(onClickListener);
    }

    private void updateDailyCashOutListeners(@Nullable final PaymentBatches.RecurringCashOutInfo recurringCashOutInfo) {
        if (recurringCashOutInfo == null) {
            paymentsBatchListView.getWrappedAdapter().setDailyCashOutToggleContainerClickListener(null);
            return;
        }

        paymentsBatchListView.getWrappedAdapter().setDailyCashOutToggleContainerClickListener(
                new DailyCashOutToggleContainerView.ToggleContainerClickListener() {
                    @Override
                    public void onToggleClicked(@NonNull final SwitchCompat toggleView) {
                        boolean requestEnableDailyCashOut = !toggleView.isChecked();

                        bus.post(new PaymentsLog.CashOut.Recurring.ToggleTapped(requestEnableDailyCashOut));
                        showDailyCashOutToggleConfirmationDialog(recurringCashOutInfo.getToggleConfirmationInfo(), requestEnableDailyCashOut);
                    }

                    @Override
                    public void onToggleInfoHelpCenterLinkClicked(@NonNull final SwitchCompat toggleView) {
                        bus.post(new PaymentsLog.CashOut.Recurring.HelpButtonTapped(toggleView.isChecked()));
                        showHelpCenterArticle(recurringCashOutInfo.getHelpCenterArticleUrl());

                    }

                    @Override
                    public void onApparentlyDisabledContainerClicked() {
                        bus.post(new PaymentsLog.CashOut.Recurring.UneditableViewTapped());
                        showDailyCashOutEditDisabledDialog(recurringCashOutInfo.getEditDisabledDialogInfo());
                    }
                });
    }

    private void showDailyCashOutEditDisabledDialog(@Nullable PaymentBatches.RecurringCashOutInfo.EditDisabledDialogInfo editDisabledDialogInfo)
    {
        if(editDisabledDialogInfo == null) return;
        AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                .setPositiveButton(R.string.ok, null)
                .setMessage(editDisabledDialogInfo.getBodyText())
                .setTitle(editDisabledDialogInfo.getTitleText())
                .create();
        alertDialog.show();
    }

    private void showDailyCashOutToggleConfirmationDialog(
            @NonNull PaymentBatches.RecurringCashOutInfo.ToggleConfirmationInfo toggleConfirmationInfo,
            final boolean requestEnableDailyCashOut
    ) {
        AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                .setPositiveButton(toggleConfirmationInfo.getConfirmButtonText(),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialog, final int which) {
                                bus.post(new PaymentsLog.CashOut.Recurring.ToggleConfirmationConfirmed(requestEnableDailyCashOut));
                                RecurringCashOutRequest dailyCashOutRequest
                                        = createUpdateRecurringCashOutRequest((requestEnableDailyCashOut));
                                requestDailyCashOut(dailyCashOutRequest);
                            }
                        })
                .setNegativeButton(toggleConfirmationInfo.getCancelButtonText(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, final int which) {
                        bus.post(new PaymentsLog.CashOut.Recurring.ToggleConfirmationCancelled(requestEnableDailyCashOut));
                    }
                })
                .setMessage(toggleConfirmationInfo.getBodyText())
                .setTitle(toggleConfirmationInfo.getTitleText())
                .create();
        alertDialog.show();
        //can only update buttons after show() is called
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(
                ContextCompat.getColor(getContext(), R.color.handy_tertiary_gray));
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(
                ContextCompat.getColor(getContext(), R.color.handy_blue));
    }

    @NonNull
    private RecurringCashOutRequest createUpdateRecurringCashOutRequest(boolean requestEnableDailyCashOut) {
        int paymentBatchPeriodDays = requestEnableDailyCashOut ?
                PaymentBatches.RecurringCashOutInfo.PaymentBatchPeriodInfo.PAYMENT_BATCH_PERIOD_DAYS_DAILY :
                PaymentBatches.RecurringCashOutInfo.PaymentBatchPeriodInfo.PAYMENT_BATCH_PERIOD_DAYS_WEEKLY;

        return new RecurringCashOutRequest(
                mProviderManager.getLastProviderId(),
                paymentBatchPeriodDays);
    }

    private void requestDailyCashOut(@NonNull final RecurringCashOutRequest dailyCashOutRequest) {
        showProgressSpinner();
        mPaymentsManager.requestRecurringCashOut(dailyCashOutRequest, new FragmentSafeCallback<SuccessWrapper>(this) {
            @Override
            public void onCallbackSuccess(final SuccessWrapper response) {
                hideProgressSpinner();
                if (!TextUtils.isEmpty(response.getMessage())) {
                    Toast.makeText(getContext(), response.getMessage(), Toast.LENGTH_SHORT).show();
                }
                if (response.getSuccess() != null && response.getSuccess()) {
                    requestInitialPaymentsInfo();
                }
            }

            @Override
            public void onCallbackError(final DataManager.DataManagerError error) {
                hideProgressSpinner();
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onInitialPaymentBatchReceived(final PaymentBatches paymentBatches, Date requestStartDate) //should only be called once in this instance. should never be empty
    {
        //reset payment batch list view and its adapter
        paymentsBatchListView.clear();
        hideProgressSpinner();

        //update the current pay week
        if (paymentBatches.getNeoPaymentBatches().length == 0) //this should never happen. always expecting at least one entry (current pay week) from server in initial batch
        {
            Crashlytics.logException(new Exception("Bad initial payment batch received! Non-legacy payment batch list is empty. Expecting first entry to be current pay week"));
            showToast(R.string.an_error_has_occurred);
            return;
        }
        paymentsBatchListView.getWrappedAdapter().setDailyCashOutInfo(paymentBatches.getRecurringCashOutInfo());
        paymentsBatchListView.appendData(paymentBatches, requestStartDate);

        NeoPaymentBatch currentWeekBatch = paymentsBatchListView.getWrappedAdapter().getCurrentWeekBatch();

        updateCashOutButtonClickListener(paymentBatches.getAdhocCashOutInfo(),
                currentWeekBatch != null && currentWeekBatch.isCashOutEnabled());
        updateDailyCashOutListeners(paymentBatches.getRecurringCashOutInfo());

        //updating with data from payment batches
        paymentsBatchListView.setOnDataItemClickListener(new PaymentsBatchListView.OnDataItemClickListener() {
            @Override
            public void onDataItemClicked(PaymentBatch paymentBatch, boolean isCurrentWeekBatch) {
                showPaymentDetailsForBatch(paymentBatch, isCurrentWeekBatch, paymentBatches.getAdhocCashOutInfo());
            }
        });
        paymentsBatchListView.setOnScrollToBottomListener(new InfiniteScrollListView.OnScrollToBottomListener() {
            @Override
            public void onScrollToBottom() {
                if (paymentsBatchListView != null) //this is to handle case in which Butterknife.reset(this) makes paymentBatchListView null but this callback still gets called. TODO: need more general solution
                {
                    requestNextPaymentBatches(false);
                }
            }
        });
    }

    public void showPaymentDetailsForBatch(@NonNull PaymentBatch paymentBatch,
                                           boolean isCurrentWeekBatch,
                                           @Nullable PaymentBatches.AdhocCashOutInfo adhocCashOutInfo) {
        if (paymentBatch instanceof NeoPaymentBatch) {
            Bundle arguments = PaymentsDetailFragment.createBundle((NeoPaymentBatch) paymentBatch,
                    isCurrentWeekBatch,
                    adhocCashOutInfo);
            mNavigationManager.navigateToPage(getActivity().getSupportFragmentManager(),
                    MainViewPage.PAYMENTS_DETAIL, arguments, null, true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_help:
                bus.post(new PaymentsLog.HelpSelected());
                goToHelpCenterWebView();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void goToHelpCenterWebView() {
        final Bundle arguments = new Bundle();
        arguments.putString(BundleKeys.HELP_REDIRECT_PATH, HELP_PAYMENTS_SECTION_REDIRECT_PATH);
        mNavigationManager.navigateToPage(getActivity().getSupportFragmentManager(),
                MainViewPage.HELP_WEBVIEW, arguments, null, true);
    }

    private void onReceivePaymentBatchesSuccess(@NonNull final PaymentBatches paymentBatches,
                                                @NonNull final Date requestStartDate,
                                                @NonNull final Date requestEndDate,
                                                final boolean isFromInitialBatchRequest
    ) {
        fetchErrorView.setVisibility(View.GONE);
        if (!paymentsBatchListView.getWrappedAdapter().canAppendBatch(requestEndDate)) {
            return;
        }
        paymentsBatchListView.setFooterVisible(false);
        if (isFromInitialBatchRequest) //if it was previously empty
        {
            onInitialPaymentBatchReceived(paymentBatches, requestStartDate);
        }
        else {
            paymentsBatchListView.appendData(paymentBatches, requestStartDate);
        }

        //only if the data returned is empty, determine whether we need to re-request
        //TODO: this is gross and we won't need to do this when new payments API comes out
        if (paymentBatches.isEmpty()) {
            if (paymentsBatchListView.shouldRequestMoreData()) {
                requestNextPaymentBatches(false);
            }
            else {
                paymentsBatchListView.showFooter(R.string.no_more_payments);
            }
        }
    }

    private void onReceivePaymentBatchesError(@NonNull DataManager.DataManagerError error) {
        if (paymentsBatchListView.isDataEmpty()) {
            fetchErrorView.setVisibility(View.VISIBLE);
            fetchErrorText.setText(R.string.request_payments_batches_failed);
        }
        else {
            paymentsBatchListView.showFooter(R.string.request_payments_batches_failed);
        }
        hideProgressSpinner();
    }

    @Override
    public void onCashOutSuccess(@NonNull final String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        requestInitialPaymentsInfo();
    }

    private void showHelpCenterArticle(@NonNull String helpUrl) {
        Bundle arguments = PortalWebViewFragment.createBundle(helpUrl, getString(R.string.help));
        Intent webviewIntent = FragmentContainerActivity.getIntent(
                getContext(),
                PortalWebViewFragment.class,
                arguments
        );
        startActivity(webviewIntent);
    }
}
