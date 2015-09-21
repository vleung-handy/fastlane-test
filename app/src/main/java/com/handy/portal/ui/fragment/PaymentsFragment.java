package com.handy.portal.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.handy.portal.R;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.constant.MainViewTab;
import com.handy.portal.constant.TransitionStyle;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.event.PaymentEvents;
import com.handy.portal.manager.PaymentsManager;
import com.handy.portal.model.payments.AnnualPaymentSummaries;
import com.handy.portal.model.payments.NeoPaymentBatch;
import com.handy.portal.model.payments.PaymentBatch;
import com.handy.portal.model.payments.PaymentBatches;
import com.handy.portal.retrofit.HandyRetrofitEndpoint;
import com.handy.portal.ui.element.payments.PaymentsBatchListView;
import com.handy.portal.util.CurrencyUtils;
import com.squareup.otto.Subscribe;

import java.util.Calendar;
import java.util.Date;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public final class PaymentsFragment extends ActionBarFragment implements AdapterView.OnItemClickListener
{
    @Inject
    HandyRetrofitEndpoint endpoint;

    @Inject
    PaymentsManager paymentsManager;

    @InjectView(R.id.payments_scroll_view)
    ScrollView scrollView;

    @InjectView(R.id.payments_content_container)
    View contentContainer;

    @InjectView(R.id.payments_batch_list_view)
    PaymentsBatchListView paymentsBatchListView;

    @InjectView(R.id.element_payments_year_summary_text)
    TextView yearSummaryText;

    @InjectView(R.id.payments_loading)
    TextView loadingText;

    @InjectView(R.id.payments_no_history_text)
    TextView paymentsNoHistoryText;

//    @InjectView(R.id.select_year_spinner)
//    Spinner selectYearSpinner;

    @Override
    public final View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                                   final Bundle savedInstanceState)
    {
        final View view = inflater
                .inflate(R.layout.fragment_payments, container, false);

        ButterKnife.inject(this, view);

        return view;
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
        paymentsBatchListView.setOnItemClickListener(this);
        yearSummaryText.setText((Calendar.getInstance().get(Calendar.YEAR) + ""));
    }

    @Override
    public void onResume()
    {
        super.onResume();
        setActionBar(R.string.payments, false);
        if (!MainActivityFragment.clearingBackStack)
        {
            requestPaymentsInfo();
        }
    }

    private void requestPaymentsInfo()
    {
//        requestAnnualPaymentSummaries();
        requestPaymentBatches();
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(true));

    }

    private void requestAnnualPaymentSummaries()
    {
        bus.post(new PaymentEvents.RequestAnnualPaymentSummaries());
    }

    private void requestPaymentBatches()
    {
        //these are only arbitrary dummy dates
        Date endDate = new Date();
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, c.get(Calendar.YEAR));
        c.set(Calendar.DAY_OF_YEAR, 1);
        Date startDate = c.getTime();
        loadingText.setVisibility(View.VISIBLE);
        scrollView.setVisibility(View.GONE);
        bus.post(new PaymentEvents.RequestPaymentBatches(startDate, endDate));
    }

    private void updateYearSummaryText(AnnualPaymentSummaries annualPaymentSummaries) //not used for now
    {
        //update with annual summary
        AnnualPaymentSummaries.AnnualPaymentSummary paymentSummary = annualPaymentSummaries.getAnnualPaymentSummaries()[0];
        //TODO: use string with formatting placeholders
        yearSummaryText.setText("YTD  ⋅  " + paymentSummary.getNumCompletedJobs() + " jobs  ⋅  " + CurrencyUtils.formatPrice(CurrencyUtils.centsToDollars(paymentSummary.getNetEarnings().getAmount()), paymentSummary.getNetEarnings().getCurrencySymbol()));
    }

    public void updatePaymentsView(PaymentBatches paymentBatches)
    {
        //update the current pay week
        paymentsBatchListView.updateData(paymentBatches);
        paymentsNoHistoryText.setVisibility(paymentBatches.isEmpty() ? View.VISIBLE : View.GONE);
        loadingText.setVisibility(View.GONE);
        scrollView.setVisibility(View.VISIBLE);
        paymentsBatchListView.setOnScrollListener(new AbsListView.OnScrollListener()
        {
            private int previousLastItem = -1;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState)
            {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
            {
                int lastItem = firstVisibleItem + visibleItemCount;
                if (lastItem == totalItemCount)
                {
                    //scrolled to bottom!
                    if (lastItem != previousLastItem) //TODO: refine
                    {
                        previousLastItem = lastItem;
                        System.out.println("SCROLLED TO BOTTOM OF LIST VIEW");
                        //request more entries!
                    }
                }
            }
        });
    }

    @Subscribe
    public void onReceivePaymentBatchesSuccess(PaymentEvents.ReceivePaymentBatchesSuccess event)
    {
        PaymentBatches paymentBatches = event.getPaymentBatches();
        updatePaymentsView(paymentBatches);
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
    }

    @Subscribe
    public void onReceiveAnnualPaymentSummariesSuccess(PaymentEvents.ReceiveAnnualPaymentSummariesSuccess event)
    {
        updateYearSummaryText(event.getAnnualPaymentSummaries());
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        PaymentBatch paymentBatch = (PaymentBatch) paymentsBatchListView.getAdapter().getItem(position);
        if (paymentBatch instanceof NeoPaymentBatch)
        {
            Bundle arguments = new Bundle();
            arguments.putSerializable(BundleKeys.PAYMENT_BATCH, paymentBatch);
            bus.post(new HandyEvent.NavigateToTab(MainViewTab.PAYMENTS_DETAIL, arguments));
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_update_banking:
                bus.post(new HandyEvent.NavigateToTab(MainViewTab.PROFILE, null, TransitionStyle.REFRESH_TAB));
                return true;
            case R.id.action_email_verification:
                bus.post(new HandyEvent.SetLoadingOverlayVisibility(true));
                bus.post(new HandyEvent.SendIncomeVerification());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Subscribe
    public void onSendIncomeVerificationSuccess(HandyEvent.SendIncomeVerificationSuccess event)
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        bus.post(new HandyEvent.NavigateToTab(MainViewTab.PAYMENTS, null, TransitionStyle.SEND_VERIFICAITON_SUCCESS));
    }

    @Subscribe
    public void onSendIncomeVerificationError(HandyEvent.SendIncomeVerificationError event)
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        Toast.makeText(getActivity(), R.string.send_verification_failed, Toast.LENGTH_SHORT).show();
    }
}
