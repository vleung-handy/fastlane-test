package com.handy.portal.payments.ui.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SwitchCompat;
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
import com.handy.portal.core.event.NavigationEvent;
import com.handy.portal.core.manager.ConfigManager;
import com.handy.portal.core.manager.ProviderManager;
import com.handy.portal.core.model.SuccessWrapper;
import com.handy.portal.core.ui.activity.FragmentContainerActivity;
import com.handy.portal.core.ui.fragment.ActionBarFragment;
import com.handy.portal.data.DataManager;
import com.handy.portal.data.callback.FragmentSafeCallback;
import com.handy.portal.library.ui.layout.SlideUpPanelLayout;
import com.handy.portal.library.ui.widget.InfiniteScrollListView;
import com.handy.portal.library.util.DateTimeUtils;
import com.handy.portal.logger.handylogger.LogEvent;
import com.handy.portal.logger.handylogger.model.PaymentsLog;
import com.handy.portal.payments.PaymentsManager;
import com.handy.portal.payments.PaymentsUtil;
import com.handy.portal.payments.model.DailyCashOutRequest;
import com.handy.portal.payments.model.NeoPaymentBatch;
import com.handy.portal.payments.model.PaymentBatch;
import com.handy.portal.payments.model.PaymentBatches;
import com.handy.portal.payments.ui.adapter.PaymentBatchListAdapter;
import com.handy.portal.payments.ui.element.DailyCashOutToggleView;
import com.handy.portal.payments.ui.element.PaymentsBatchListView;
import com.handy.portal.payments.ui.fragment.dialog.PaymentCashOutDialogFragment;
import com.handy.portal.webview.PortalWebViewFragment;

import java.util.Calendar;
import java.util.Date;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public final class PaymentsFragment extends ActionBarFragment implements PaymentCashOutDialogFragment.OnCashOutSuccessListener {
    private static final String HELP_PAYMENTS_SECTION_REDIRECT_PATH = "/sections/203828247";

    @Inject
    ConfigManager mConfigManager;

    @Inject
    PaymentsManager mPaymentsManager;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (fragmentView == null) {
            fragmentView = (ViewGroup) super.onCreateView(inflater, container, savedInstanceState);
            fragmentView.addView(inflater.inflate(R.layout.fragment_payments, container, false));
        }

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

        boolean enableBack = mConfigManager.getConfigurationResponse() != null &&
                mConfigManager.getConfigurationResponse().isMoreFullTabEnabled();
        setActionBar(R.string.payments, enableBack);

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
//            mPaymentsManager.requestPaymentBatches(startDate, endDate, mCurrentPaymentBatchCallback);

            //fixme test only remove
            mPaymentsManager.requestTestPaymentBatches(getContext(),
                    startDate,
                    endDate,
                    mCurrentPaymentBatchCallback);

            paymentsBatchListView.showFooter(R.string.loading_more_payments);
        }
        else {
            paymentsBatchListView.showFooter(R.string.no_more_payments);
        }
    }

    private void updateCashOutButtonClickListener(@Nullable PaymentBatches.OneTimeCashOutInfo oneTimeCashOutInfo, boolean isCashOutEnabled) {
        View.OnClickListener onClickListener = PaymentsUtil.CashOut.createCashOutButtonClickListener(
                this,
                isCashOutEnabled,
                oneTimeCashOutInfo,
                bus);
        paymentsBatchListView.setCashOutButtonClickListener(onClickListener);
    }

    private void updateDailyCashOutListeners(@Nullable final PaymentBatches.DailyCashOutInfo dailyCashOutInfo) {
        if (dailyCashOutInfo == null) {
            paymentsBatchListView.getWrappedAdapter().setDailyCashOutListeners(null, null);
            return;
        }
        final PaymentBatches.DailyCashOutInfo.ToggleConfirmationCopy
                confirmationCopy = dailyCashOutInfo.getToggleConfirmationCopy();
        paymentsBatchListView.getWrappedAdapter().setDailyCashOutListeners(
                new DailyCashOutToggleView.OnToggleClickedListener() {
                    @Override
                    public void onToggleClicked(@NonNull final SwitchCompat toggleView) {
                        AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                                .setPositiveButton(confirmationCopy.getConfirmButtonText(), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(final DialogInterface dialog, final int which) {
                                        DailyCashOutRequest dailyCashOutRequest =
                                                new DailyCashOutRequest(
                                                        mProviderManager.getLastProviderId(),
                                                        !toggleView.isChecked());
                                        requestDailyCashOut(dailyCashOutRequest, toggleView);
                                    }
                                })
                                .setNegativeButton(confirmationCopy.getCancelButtonText(), null)
                                .setMessage(confirmationCopy.getBodyText())
                                .setTitle(confirmationCopy.getTitleText())
                                .create();
                        alertDialog.show();

                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        onHelpCenterUrlLinkClicked(dailyCashOutInfo.getHelpCenterArticleUrl());
                    }
                }
        );
    }

    private void requestDailyCashOut(@NonNull final DailyCashOutRequest dailyCashOutRequest,
                                     @NonNull final SwitchCompat toggleView) {
        mPaymentsManager.requestDailyCashOut(dailyCashOutRequest, new FragmentSafeCallback<SuccessWrapper>(this) {
            @Override
            public void onCallbackSuccess(final SuccessWrapper response) {
                //update the toggle
                toggleView.setChecked(dailyCashOutRequest.isDailyCashOutEnabled());
            }

            @Override
            public void onCallbackError(final DataManager.DataManagerError error) {

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
        paymentsBatchListView.getWrappedAdapter().setDailyCashOutInfo(paymentBatches.getDailyCashOutInfo());
        paymentsBatchListView.appendData(paymentBatches, requestStartDate);

        NeoPaymentBatch currentWeekBatch = paymentsBatchListView.getWrappedAdapter().getCurrentWeekBatch();

        updateCashOutButtonClickListener(paymentBatches.getOneTimeCashOutInfo(),
                currentWeekBatch != null && currentWeekBatch.isCashOutEnabled());
        updateDailyCashOutListeners(paymentBatches.getDailyCashOutInfo());

        //updating with data from payment batches
        paymentsBatchListView.setOnDataItemClickListener(new PaymentsBatchListView.OnDataItemClickListener() {
            @Override
            public void onDataItemClicked(PaymentBatch paymentBatch, boolean isCurrentWeekBatch) {
                showPaymentDetailsForBatch(paymentBatch, isCurrentWeekBatch, paymentBatches.getOneTimeCashOutInfo());
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
                                           @Nullable PaymentBatches.OneTimeCashOutInfo oneTimeCashOutInfo) {
        if (paymentBatch instanceof NeoPaymentBatch) {
            Bundle arguments = PaymentsDetailFragment.createBundle((NeoPaymentBatch) paymentBatch,
                    isCurrentWeekBatch,
                    oneTimeCashOutInfo);
            bus.post(new NavigationEvent.NavigateToPage(MainViewPage.PAYMENTS_DETAIL, arguments, true));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_help:
                bus.post(new LogEvent.AddLogEvent(new PaymentsLog.HelpSelected()));
                goToHelpCenterWebView();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void goToHelpCenterWebView() {
        final Bundle arguments = new Bundle();
        arguments.putString(BundleKeys.HELP_REDIRECT_PATH, HELP_PAYMENTS_SECTION_REDIRECT_PATH);
        bus.post(new NavigationEvent.NavigateToPage(MainViewPage.HELP_WEBVIEW, arguments, true));
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

    private void onHelpCenterUrlLinkClicked(@NonNull String helpUrl) {
        Bundle arguments = PortalWebViewFragment.createBundle(helpUrl, getString(R.string.help));
        Intent webviewIntent = FragmentContainerActivity.getIntent(
                getContext(),
                PortalWebViewFragment.class,
                arguments
        );
        startActivity(webviewIntent);
    }
}
