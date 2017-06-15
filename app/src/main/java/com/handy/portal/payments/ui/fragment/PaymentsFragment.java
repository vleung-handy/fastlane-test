package com.handy.portal.payments.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.handy.portal.core.ui.fragment.ActionBarFragment;
import com.handy.portal.data.DataManager;
import com.handy.portal.data.callback.FragmentSafeCallback;
import com.handy.portal.library.ui.layout.SlideUpPanelLayout;
import com.handy.portal.library.ui.listener.OnScrollToListViewBottomListener;
import com.handy.portal.logger.handylogger.LogEvent;
import com.handy.portal.logger.handylogger.model.PaymentsLog;
import com.handy.portal.payments.PaymentsManager;
import com.handy.portal.payments.PaymentsUtil;
import com.handy.portal.payments.model.NeoPaymentBatch;
import com.handy.portal.payments.model.PaymentBatch;
import com.handy.portal.payments.model.PaymentBatches;
import com.handy.portal.payments.ui.element.PaymentsBatchListView;
import com.handy.portal.payments.ui.fragment.dialog.PaymentCashOutDialogFragment;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public final class PaymentsFragment extends ActionBarFragment implements PaymentCashOutDialogFragment.OnCashOutSuccessListener {
    private static final String HELP_PAYMENTS_SECTION_REDIRECT_PATH = "/sections/203828247";
    private static final int PAYMENT_BATCHES_PAGE_SIZE = 10;

    @Inject
    ConfigManager mConfigManager;

    @Inject
    PaymentsManager mPaymentsManager;

    @BindView(R.id.slide_up_panel_container)
    SlideUpPanelLayout mSlideUpPanelLayout;

    @BindView(R.id.payments_scroll_view)
    ScrollView mScrollView;

    @BindView(R.id.payments_batch_list_view)
    PaymentsBatchListView mPaymentsBatchListView;

    @BindView(R.id.fetch_error_text)
    TextView mFetchErrorText;

    @BindView(R.id.fetch_error_view)
    ViewGroup mFetchErrorView;

    private ViewGroup mFragmentView;

    private FragmentSafeCallback mCurrentPaymentBatchCallback;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mFragmentView == null) {
            mFragmentView = (ViewGroup) super.onCreateView(inflater, container, savedInstanceState);
            mFragmentView.addView(inflater.inflate(R.layout.fragment_payments, container, false));
        }

        ButterKnife.bind(this, mFragmentView);
        mFetchErrorText.setText(R.string.request_payments_batches_failed);
        return mFragmentView;

    }

    @Override
    protected MainViewPage getAppPage() {
        return MainViewPage.PAYMENTS;
    }

    @Override
    protected void showProgressSpinner(final boolean isBlocking) {
        super.showProgressSpinner(isBlocking);
        mScrollView.setVisibility(View.GONE);
    }

    @Override
    protected void hideProgressSpinner() {
        mScrollView.setVisibility(View.VISIBLE);
        super.hideProgressSpinner();
    }

    @Override
    public void onResume() {
        super.onResume();

        boolean enableBack = mConfigManager.getConfigurationResponse().isMoreFullTabEnabled();
        setActionBar(R.string.payments, enableBack);

        if (mPaymentsBatchListView.getWrappedAdapter().isDataEmpty() && mPaymentsBatchListView.getWrappedAdapter().shouldRequestMoreData())//if initial batch has not been received yet
        {
            clearAllAndRequestInitialPaymentsInfo();
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

    /**
     * try again button for the full-screen error view
     */
    @OnClick(R.id.try_again_button)
    public void onTryRequestingInitialPaymentsAgainButtonClicked() {
        clearAllAndRequestInitialPaymentsInfo();
    }

    /**
     * try again button for the pagination error footer
     */
    private final View.OnClickListener mRetryFailedBatchRequestButtonClickedListener
            = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            requestNextPaymentBatches();
        }
    };

    private void clearAllAndRequestInitialPaymentsInfo() {
        showProgressSpinner(true);
        mPaymentsBatchListView.clear();
        requestNextPaymentBatches();
    }

    private void requestNextPaymentBatches() {
        final Integer lastPaymentBatchId = mPaymentsBatchListView.getWrappedAdapter().getLastPaymentBatchId();
        if (mCurrentPaymentBatchCallback != null) {
            //only one payment batch request at a time
            mCurrentPaymentBatchCallback.cancel();
        }

        mCurrentPaymentBatchCallback
                = new FragmentSafeCallback<PaymentBatches>(this) {
            @Override
            public void onCallbackSuccess(PaymentBatches response) {
                onReceivePaymentBatchesSuccess(response);
            }

            @Override
            public void onCallbackError(DataManager.DataManagerError error) {
                onReceivePaymentBatchesError(error);
            }
        };

        mPaymentsBatchListView.showFooter(R.string.loading_more_payments);
        mPaymentsManager.requestPaymentBatchesPage(lastPaymentBatchId, PAYMENT_BATCHES_PAGE_SIZE, mCurrentPaymentBatchCallback);
    }


    /**
     * assumptions:
     * - the batch list is empty
     * - the given PaymentBatches is the first batch of the first page
     * <p>
     * should only be called once in this instance. should never be empty
     */
    private void onInitialPaymentBatchReceived(@NonNull final PaymentBatches paymentBatches) {
        hideProgressSpinner();

        //update the current pay week
        if (paymentBatches.getNeoPaymentBatches().length == 0) //this should never happen. always expecting at least one entry (current pay week) from server in initial batch
        {
            Crashlytics.logException(new Exception("Bad initial payment batch received! Non-legacy payment batch list is empty. Expecting first entry to be current pay week"));
            showToast(R.string.an_error_has_occurred);
            return;
        }
        mPaymentsBatchListView.appendData(paymentBatches);

        NeoPaymentBatch currentWeekBatch = mPaymentsBatchListView.getWrappedAdapter().getCurrentWeekBatch();

        updateCashOutButtonClickListener(paymentBatches.getCashOutInfo(),
                currentWeekBatch != null && currentWeekBatch.isCashOutEnabled());

        //updating with data from payment batches
        mPaymentsBatchListView.setOnDataItemClickListener(new PaymentsBatchListView.OnDataItemClickListener() {
            @Override
            public void onDataItemClicked(PaymentBatch paymentBatch,
                                          boolean isCurrentWeekBatch,
                                          int listIndex) {
                bus.post(new LogEvent.AddLogEvent(
                        new PaymentsLog.BatchSelected(isCurrentWeekBatch, listIndex + 1)));
                //index needs to be one based

                showPaymentDetailsForBatch(paymentBatch, isCurrentWeekBatch, paymentBatches.getCashOutInfo());
            }
        });

        //fixme test. if works, use plain list view or even recyclerview (if time)
        mPaymentsBatchListView.setOnScrollListener(new OnScrollToListViewBottomListener() {
            @Override
            public void onScrollToBottom() {
                if (mPaymentsBatchListView != null) //this is to handle case in which Butterknife.reset(this) makes paymentBatchListView null but this callback still gets called. TODO: need more general solution
                {
                    requestNextPaymentBatches();
                }
            }
        });
//        mPaymentsBatchListView.setOnScrollToBottomListener(new InfiniteScrollListView.OnScrollToBottomListener() {
//            @Override
//            public void onScrollToBottom() {
//                if (mPaymentsBatchListView != null) //this is to handle case in which Butterknife.reset(this) makes paymentBatchListView null but this callback still gets called. TODO: need more general solution
//                {
//                    requestNextPaymentBatches();
//                }
//            }
//        });
    }

    private void onReceivePaymentBatchesSuccess(@NonNull final PaymentBatches paymentBatches) {
        mFetchErrorView.setVisibility(View.GONE);
        mPaymentsBatchListView.setFooterVisible(false);

        //todo more checks to ensure this is the current week batch?
        if (mPaymentsBatchListView.getWrappedAdapter().isDataEmpty())
        //if we received batch containing current pay period
        {
            onInitialPaymentBatchReceived(paymentBatches);
        }
        else {
            mPaymentsBatchListView.appendData(paymentBatches);
        }

        if (mPaymentsBatchListView.getWrappedAdapter().shouldRequestMoreData()) {
            requestNextPaymentBatches();
        }
        else {
            mPaymentsBatchListView.showFooter(R.string.no_more_payments);
        }
    }

    private void onReceivePaymentBatchesError(@NonNull DataManager.DataManagerError error) {
        if (mPaymentsBatchListView.getWrappedAdapter().isDataEmpty()) {
            mFetchErrorView.setVisibility(View.VISIBLE);
        }
        else {
            //we have at least one item in the list
            mPaymentsBatchListView.showFooter(R.string.request_payments_batches_failed,
                    mRetryFailedBatchRequestButtonClickedListener);
        }
        hideProgressSpinner();
    }

    //cash out stuff
    private void updateCashOutButtonClickListener(@Nullable PaymentBatches.CashOutInfo cashOutInfo, boolean isCashOutEnabled) {
        View.OnClickListener onClickListener = PaymentsUtil.CashOut.createCashOutButtonClickListener(
                this,
                isCashOutEnabled,
                cashOutInfo,
                bus);
        mPaymentsBatchListView.setCashOutButtonClickListener(onClickListener);
    }

    @Override
    public void onCashOutSuccess(@NonNull final String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        clearAllAndRequestInitialPaymentsInfo();
    }

    //navigate to payment details fragment
    public void showPaymentDetailsForBatch(@NonNull PaymentBatch paymentBatch,
                                           boolean isCurrentWeekBatch,
                                           @Nullable PaymentBatches.CashOutInfo cashOutInfo) {
        if (paymentBatch instanceof NeoPaymentBatch) {
            Bundle arguments = PaymentsDetailFragment.createBundle((NeoPaymentBatch) paymentBatch,
                    isCurrentWeekBatch,
                    cashOutInfo);
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
}
