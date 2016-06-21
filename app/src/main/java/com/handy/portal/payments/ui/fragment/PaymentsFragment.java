package com.handy.portal.payments.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.handy.portal.R;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.constant.MainViewPage;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.event.NavigationEvent;
import com.handy.portal.library.ui.layout.SlideUpPanelLayout;
import com.handy.portal.library.ui.widget.InfiniteScrollListView;
import com.handy.portal.library.util.DateTimeUtils;
import com.handy.portal.library.util.Utils;
import com.handy.portal.logger.handylogger.LogEvent;
import com.handy.portal.logger.handylogger.model.PaymentsLog;
import com.handy.portal.manager.ConfigManager;
import com.handy.portal.payments.PaymentEvent;
import com.handy.portal.payments.model.AnnualPaymentSummaries;
import com.handy.portal.payments.model.NeoPaymentBatch;
import com.handy.portal.payments.model.PaymentBatch;
import com.handy.portal.payments.model.PaymentBatches;
import com.handy.portal.payments.ui.adapter.PaymentBatchListAdapter;
import com.handy.portal.payments.ui.element.PaymentsBatchListView;
import com.handy.portal.ui.fragment.ActionBarFragment;
import com.squareup.otto.Subscribe;

import java.util.Calendar;
import java.util.Date;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public final class PaymentsFragment extends ActionBarFragment
{
    private static final String HELP_PAYMENTS_SECTION_REDIRECT_PATH = "/sections/203828247";

    @Inject
    ConfigManager mConfigManager;

    //TODO: investigate using @Produce and make manager handle more of this logic
    @Bind(R.id.slide_up_panel_container)
    SlideUpPanelLayout mSlideUpPanelLayout;

    @Bind(R.id.payments_scroll_view)
    ScrollView scrollView;

    @Bind(R.id.payments_batch_list_view)
    PaymentsBatchListView paymentsBatchListView;

    @Bind(R.id.fetch_error_text)
    TextView fetchErrorText;

    @Bind(R.id.fetch_error_view)
    ViewGroup fetchErrorView;

    //TODO: refactor request protocols when we can use new pagination API that allows us to get the N next batches

    private View fragmentView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        if (fragmentView == null)
        {
            fragmentView = inflater.inflate(R.layout.fragment_payments, container, false);
        }

        ButterKnife.bind(this, fragmentView);

        return fragmentView;
    }

    @Override
    protected MainViewPage getAppPage()
    {
        return MainViewPage.PAYMENTS;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        setActionBar(R.string.payments, false);

        if (paymentsBatchListView.isDataEmpty() && paymentsBatchListView.shouldRequestMoreData())//if initial batch has not been received yet
        {
            requestInitialPaymentsInfo();
        }
        else //have to put this logic here because of the way the loading overlay is handled by the bus system e.g. when child fragment is destroyed but caused overlay to show (messy to put in onResume because user could have just navigated away from app)
        {
            setLoadingOverlayVisible(false);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_payments, menu);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        paymentsBatchListView.setOnDataItemClickListener(new PaymentsBatchListView.OnDataItemClickListener()
        {
            @Override
            public void onDataItemClicked(PaymentBatch paymentBatch)
            {
                showPaymentDetailsForBatch(paymentBatch);
            }
        });
    }

    @OnClick(R.id.try_again_button)
    public void doInitialRequestAgain()
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(true));
        requestInitialPaymentsInfo();
    }

    public void setLoadingOverlayVisible(boolean visible)
    {
        scrollView.setVisibility(visible ? View.GONE : View.VISIBLE);
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(visible));
    }

    private void requestInitialPaymentsInfo()
    {
        requestNextPaymentBatches(true);
        setLoadingOverlayVisible(true);
    }

    private void requestNextPaymentBatches(boolean isInitialRequest)
    {
        Date endDate = paymentsBatchListView.getNextRequestEndDate();

        if (endDate != null)
        {
            Calendar c = Calendar.getInstance();
            c.setTime(endDate);
            c.add(Calendar.DATE, -PaymentBatchListAdapter.DAYS_TO_REQUEST_PER_BATCH);
            Date startDate = DateTimeUtils.getBeginningOfDay(c.getTime());
            bus.post(new PaymentEvent.RequestPaymentBatches(startDate, endDate, isInitialRequest, Utils.getObjectIdentifier(this)));

            paymentsBatchListView.showFooter(R.string.loading_more_payments);
        }
        else
        {
            paymentsBatchListView.showFooter(R.string.no_more_payments);
        }
    }

    @Override
    public void onPause()
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));//don't want overlay to persist when this fragment is paused
        super.onPause();
    }

    private void requestAnnualPaymentSummaries() //not used yet
    {
        bus.post(new PaymentEvent.RequestAnnualPaymentSummaries());
    }

    private void updateYearSummaryText(AnnualPaymentSummaries annualPaymentSummaries) //annual summaries not shown or used for now
    {
        //update with annual summary. assuming array is ordered from most to least recent
        AnnualPaymentSummaries.AnnualPaymentSummary paymentSummary = annualPaymentSummaries.getMostRecentYearSummary();
        if (paymentSummary == null)
        {
            Crashlytics.logException(new Exception("Annual payment summaries is null or empty"));
        }
    }

    public void onInitialPaymentBatchReceived(final PaymentBatches paymentBatches, Date requestStartDate) //should only be called once in this instance. should never be empty
    {
        //reset payment batch list view and its adapter
        paymentsBatchListView.clear();
        setLoadingOverlayVisible(false);

        //update the current pay week
        if (paymentBatches.getNeoPaymentBatches().length == 0) //this should never happen. always expecting at least one entry (current pay week) from server in initial batch
        {
            Crashlytics.logException(new Exception("Bad initial payment batch received! Non-legacy payment batch list is empty. Expecting first entry to be current pay week"));
            return;
        }
        paymentsBatchListView.appendData(paymentBatches, requestStartDate);
        paymentsBatchListView.setOnScrollToBottomListener(new InfiniteScrollListView.OnScrollToBottomListener()
        {
            @Override
            public void onScrollToBottom()
            {
                if (paymentsBatchListView != null) //this is to handle case in which Butterknife.reset(this) makes paymentBatchListView null but this callback still gets called. TODO: need more general solution
                {
                    requestNextPaymentBatches(false);
                }
            }
        });
    }

    public void showPaymentDetailsForBatch(PaymentBatch paymentBatch)
    {
        if (paymentBatch instanceof NeoPaymentBatch)
        {
            Bundle arguments = new Bundle();
            arguments.putSerializable(BundleKeys.PAYMENT_BATCH, paymentBatch);
            bus.post(new NavigationEvent.NavigateToPage(MainViewPage.PAYMENTS_DETAIL, arguments, true));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_help:
                bus.post(new LogEvent.AddLogEvent(new PaymentsLog.HelpSelected()));
                goToHelpCenterWebView();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void goToHelpCenterWebView()
    {
        final Bundle arguments = new Bundle();
        arguments.putString(BundleKeys.HELP_REDIRECT_PATH, HELP_PAYMENTS_SECTION_REDIRECT_PATH);
        bus.post(new NavigationEvent.NavigateToPage(MainViewPage.HELP_WEBVIEW, arguments, true));
    }

    @Subscribe
    public void onReceivePaymentBatchesSuccess(PaymentEvent.ReceivePaymentBatchesSuccess event)
    {
        fetchErrorView.setVisibility(View.GONE);
        if (Utils.getObjectIdentifier(this) != event.getCallerIdentifier()
                || !paymentsBatchListView.getWrappedAdapter().canAppendBatch(event.getRequestEndDate()))
        { return; }
        PaymentBatches paymentBatches = event.getPaymentBatches();
        paymentsBatchListView.setFooterVisible(false);
        if (event.isFromInitialBatchRequest) //if it was previously empty
        {
            onInitialPaymentBatchReceived(paymentBatches, event.getRequestStartDate());
        }
        else
        {
            paymentsBatchListView.appendData(paymentBatches, event.getRequestStartDate());
        }

        //only if the data returned is empty, determine whether we need to re-request
        //TODO: this is gross and we won't need to do this when new payments API comes out
        if (paymentBatches.isEmpty())
        {
            if (paymentsBatchListView.shouldRequestMoreData())
            {
                requestNextPaymentBatches(false);
            }
            else
            {
                paymentsBatchListView.showFooter(R.string.no_more_payments);
            }
        }
    }

    @Subscribe
    public void onReceivePaymentBatchesError(PaymentEvent.ReceivePaymentBatchesError event)
    {
        if (paymentsBatchListView.isDataEmpty())
        {
            bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
            fetchErrorView.setVisibility(View.VISIBLE);
            fetchErrorText.setText(R.string.request_payments_batches_failed);
        }
        else
        {
            paymentsBatchListView.showFooter(R.string.request_payments_batches_failed);
        }
    }

    @Subscribe
    public void onReceiveAnnualPaymentSummariesSuccess(PaymentEvent.ReceiveAnnualPaymentSummariesSuccess event)
    {
        updateYearSummaryText(event.getAnnualPaymentSummaries());
    }

    @Subscribe
    public void onReceiveAnnualPaymentSummariesError(PaymentEvent.ReceiveAnnualPaymentSummariesError event)
    {
        //TODO: handle annual payments summary error
    }
}
