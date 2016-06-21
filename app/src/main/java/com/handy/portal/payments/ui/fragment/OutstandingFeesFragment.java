package com.handy.portal.payments.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.constant.MainViewPage;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.event.NavigationEvent;
import com.handy.portal.payments.PaymentEvent;
import com.handy.portal.payments.model.Payment;
import com.handy.portal.payments.model.PaymentOutstandingFees;
import com.handy.portal.payments.ui.element.PaymentFeeBreakdownView;
import com.handy.portal.ui.fragment.ActionBarFragment;
import com.handy.portal.library.util.CurrencyUtils;
import com.squareup.otto.Subscribe;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


public class OutstandingFeesFragment extends ActionBarFragment
{
    @Bind(R.id.fetch_error_view)
    ViewGroup mFetchErrorView;
    @Bind(R.id.fetch_error_text)
    TextView mFetchErrorText;
    @Bind(R.id.total_fees_text)
    TextView mTotalFeesText;
    @Bind(R.id.no_outstanding_fees_layout)
    ViewGroup mNoOutstandingFeesLayout;
    @Bind(R.id.outstanding_fee_breakdown_layout)
    ViewGroup mOutstandingFeeBreakdownLayout;

    private View fragmentView;

    @Override
    protected MainViewPage getAppPage()
    {
        return MainViewPage.OUTSTANDING_FEES;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        if (fragmentView == null)
        {
            fragmentView = inflater.inflate(R.layout.fragment_outstanding_fees, container, false);
        }

        ButterKnife.bind(this, fragmentView);

        return fragmentView;
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        setOptionsMenuEnabled(true);
        setBackButtonEnabled(true);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        setActionBar(R.string.outstanding_fees, true);
        requestOutstandingFees();
    }

    private void requestOutstandingFees()
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(true));
        bus.post(new PaymentEvent.RequestPaymentOutstandingFees());
    }

    @Subscribe
    public void onReceivePaymentOutstandingFeesSuccess(
            PaymentEvent.ReceivePaymentOutstandingFeesSuccess event)
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));

        PaymentOutstandingFees paymentOutstandingFees = event.getPaymentOutstandingFees();

        mTotalFeesText.setText(CurrencyUtils.formatPriceWithCents(
                paymentOutstandingFees.getTotalFeesInCents(),
                paymentOutstandingFees.getCurrencySymbol()));
        List<Payment> paymentOutstandingFeesList = paymentOutstandingFees.getFeesList();
        if (paymentOutstandingFeesList.isEmpty())
        {
            mNoOutstandingFeesLayout.setVisibility(View.VISIBLE);
            mOutstandingFeeBreakdownLayout.setVisibility(View.GONE);
        }
        else
        {
            mOutstandingFeeBreakdownLayout.removeAllViews();
            for (final Payment payment : paymentOutstandingFeesList)
            {
                PaymentFeeBreakdownView paymentFeeBreakdownView = new PaymentFeeBreakdownView(getContext());
                paymentFeeBreakdownView.setDisplay(payment);
                paymentFeeBreakdownView.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(final View v)
                    {
                        if (payment.getBookingId() == null || payment.getBookingType() == null)
                        { return; }

                        Bundle arguments = new Bundle();
                        arguments.putString(BundleKeys.BOOKING_ID, payment.getBookingId());
                        arguments.putString(BundleKeys.BOOKING_TYPE, payment.getBookingType());
                        NavigationEvent.NavigateToPage event =
                                new NavigationEvent.NavigateToPage(MainViewPage.JOB_PAYMENT_DETAILS, arguments, true);
                        bus.post(event);
                    }
                });
                mOutstandingFeeBreakdownLayout.addView(paymentFeeBreakdownView);
            }
        }
    }

    @Subscribe
    public void onReceivePaymentOutstandingFeesError(
            PaymentEvent.ReceivePaymentOutstandingFeesError event)
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));

        mFetchErrorView.setVisibility(View.VISIBLE);
        mFetchErrorText.setText(R.string.error_outstanding_fees);
    }
}
