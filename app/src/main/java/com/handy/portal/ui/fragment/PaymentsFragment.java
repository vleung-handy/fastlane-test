package com.handy.portal.ui.fragment;

import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import com.handy.portal.ui.view.PaymentsBatchListView;
import com.handy.portal.util.DateTimeUtils;
import com.handy.portal.util.TextUtils;
import com.squareup.otto.Subscribe;

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
    NestedScrollView scrollView;

    @InjectView(R.id.payments_batch_list_view)
    PaymentsBatchListView paymentsBatchListView;

    @InjectView(R.id.element_payments_year_summary_text)
    TextView yearSummaryText;

    @InjectView(R.id.payments_loading)
    TextView loadingText;

    @InjectView(R.id.payments_no_history_text)
    TextView paymentsNoHistoryText;

    @InjectView(R.id.payments_current_week_date_range_text)
    TextView currentWeekDateRangeText;

    @InjectView(R.id.payments_current_week_total_earnings)
    TextView currentWeekTotalEarningsText;

    @InjectView(R.id.payments_current_week_withholdings)
    TextView currentWeekWithholdingsText;

    @InjectView(R.id.payments_current_week_expected_payment)
    TextView currentWeekExpectedPaymentText;

    @InjectView(R.id.payments_current_week_remaining_withholdings)
    TextView currentWeekRemainingWithholdingsText;

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
        paymentsBatchListView.addOnLayoutChangeListener(new View.OnLayoutChangeListener()
        {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom)
            {
                scrollView.smoothScrollTo(0, 0);
            }
        });
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
        requestAnnualPaymentSummaries();
        requestPaymentBatches();
    }
    private void requestAnnualPaymentSummaries()
    {
        bus.post(new PaymentEvents.RequestAnnualPaymentSummaries());
    }
    private void requestPaymentBatches()
    {
        //these are only arbitrary dummy dates
        Date startDate = new Date();
        startDate = new Date(startDate.getTime()-DateTimeUtils.MILLISECONDS_IN_HOUR*DateTimeUtils.HOURS_IN_DAY*300);//TODO: REMOVE - TEST ONLY
        Date endDate = new Date();
        loadingText.setVisibility(View.VISIBLE);
        bus.post(new PaymentEvents.RequestPaymentBatches(startDate, endDate));
    }
    private void updateYearSummaryText(AnnualPaymentSummaries annualPaymentSummaries)
    {
        //update with annual summary

        //show only most recent year for now
        AnnualPaymentSummaries.AnnualPaymentSummary paymentSummary = annualPaymentSummaries.getAnnualPaymentSummaries()[0];

        //TODO: use formatter
        yearSummaryText.setText(paymentSummary.getYear() + " ⋅ YTD ⋅ " + paymentSummary.getNumCompletedJobs() + " jobs ⋅ " + TextUtils.formatPrice(paymentSummary.getNetEarnings().getAmount()/100, paymentSummary.getNetEarnings().getCurrencySymbol()));
    }

    private void updateCurrentPayWeekView(PaymentBatches paymentBatches)
    {
        //should be first one in payment batch
        NeoPaymentBatch neoPaymentBatch = paymentBatches.getNeoPaymentBatches()[0];
        //make sure start/end dates are correct

        currentWeekDateRangeText.setText(DateTimeUtils.formatDateDayOfWeekMonthDay(neoPaymentBatch.getStartDate()) + " - " + DateTimeUtils.formatDateDayOfWeekMonthDay(neoPaymentBatch.getEndDate()));
        currentWeekRemainingWithholdingsText.setText(TextUtils.formatPrice(neoPaymentBatch.getRemainingWithholdingDollarAmount(), neoPaymentBatch.getCurrencySymbol()));
        currentWeekExpectedPaymentText.setText(TextUtils.formatPrice(neoPaymentBatch.getTotalAmountDollars(), neoPaymentBatch.getCurrencySymbol()));
        currentWeekWithholdingsText.setText(TextUtils.formatPrice(neoPaymentBatch.getWithholdingsTotalAmount(), neoPaymentBatch.getCurrencySymbol()));
        currentWeekTotalEarningsText.setText(TextUtils.formatPrice(neoPaymentBatch.getTotalAmountDollars(), neoPaymentBatch.getCurrencySymbol()));

    }

    public void updatePaymentsView(PaymentBatches paymentBatches)
    {
        //update the current pay week
        updateCurrentPayWeekView(paymentBatches);
        paymentsBatchListView.populateList(paymentBatches);
        paymentsNoHistoryText.setVisibility(!paymentBatches.isEmpty() ? View.GONE : View.VISIBLE);
        loadingText.setVisibility(View.GONE);
    }

    @Subscribe
    public void onReceivePaymentBatchesSuccess(PaymentEvents.ReceivePaymentBatchesSuccess event)
    {
        PaymentBatches paymentBatches = event.getPaymentBatches();
        updatePaymentsView(paymentBatches);

//        paymentsBatchListView.repopulate(paymentBatches);
        //populate the batch list view
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
