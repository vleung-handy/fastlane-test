package com.handy.portal.payments.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.core.constant.BundleKeys;
import com.handy.portal.core.constant.MainViewPage;
import com.handy.portal.core.event.HandyEvent;
import com.handy.portal.core.manager.PageNavigationManager;
import com.handy.portal.core.ui.fragment.ActionBarFragment;
import com.handy.portal.library.util.CurrencyUtils;
import com.handy.portal.payments.PaymentEvent;
import com.handy.portal.payments.model.Payment;
import com.handy.portal.payments.model.PaymentOutstandingFees;
import com.handy.portal.payments.ui.element.PaymentFeeBreakdownView;

import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;


public class OutstandingFeesFragment extends ActionBarFragment {
    @Inject
    PageNavigationManager mNavigationManager;

    @BindView(R.id.fetch_error_view)
    ViewGroup mFetchErrorView;
    @BindView(R.id.fetch_error_text)
    TextView mFetchErrorText;
    @BindView(R.id.total_fees_text)
    TextView mTotalFeesText;
    @BindView(R.id.no_outstanding_fees_layout)
    ViewGroup mNoOutstandingFeesLayout;
    @BindView(R.id.outstanding_fee_breakdown_layout)
    ViewGroup mOutstandingFeeBreakdownLayout;

    private View fragmentView;

    @Override
    protected MainViewPage getAppPage() {
        return MainViewPage.OUTSTANDING_FEES;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        if (fragmentView == null) {
            fragmentView = inflater.inflate(R.layout.fragment_outstanding_fees, container, false);
        }

        ButterKnife.bind(this, fragmentView);

        return fragmentView;
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setOptionsMenuEnabled(true);
        setBackButtonEnabled(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        setActionBar(R.string.outstanding_fees, true);

        bus.register(this);
        requestOutstandingFees();
    }

    @Override
    public void onPause() {
        bus.unregister(this);
        super.onPause();
    }

    private void requestOutstandingFees() {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(true));
        bus.post(new PaymentEvent.RequestPaymentOutstandingFees());
    }

    @Subscribe
    public void onReceivePaymentOutstandingFeesSuccess(
            PaymentEvent.ReceivePaymentOutstandingFeesSuccess event) {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));

        PaymentOutstandingFees paymentOutstandingFees = event.getPaymentOutstandingFees();

        mTotalFeesText.setText(CurrencyUtils.formatPriceWithCents(
                paymentOutstandingFees.getTotalFeesInCents(),
                paymentOutstandingFees.getCurrencySymbol()));
        List<Payment> paymentOutstandingFeesList = paymentOutstandingFees.getFeesList();
        if (paymentOutstandingFeesList.isEmpty()) {
            mNoOutstandingFeesLayout.setVisibility(View.VISIBLE);
            mOutstandingFeeBreakdownLayout.setVisibility(View.GONE);
        }
        else {
            mOutstandingFeeBreakdownLayout.removeAllViews();
            for (final Payment payment : paymentOutstandingFeesList) {
                PaymentFeeBreakdownView paymentFeeBreakdownView = new PaymentFeeBreakdownView(getContext());
                paymentFeeBreakdownView.setDisplay(payment);
                paymentFeeBreakdownView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        if (payment.getBookingId() == null || payment.getBookingType() == null) {
                            return;
                        }

                        Bundle arguments = new Bundle();
                        arguments.putString(BundleKeys.BOOKING_ID, payment.getBookingId());
                        arguments.putString(BundleKeys.BOOKING_TYPE, payment.getBookingType());
                        mNavigationManager.navigateToPage(getActivity().getSupportFragmentManager(),
                                MainViewPage.JOB_PAYMENT_DETAILS, arguments, null, true);
                    }
                });
                mOutstandingFeeBreakdownLayout.addView(paymentFeeBreakdownView);
            }
        }
    }

    @Subscribe
    public void onReceivePaymentOutstandingFeesError(
            PaymentEvent.ReceivePaymentOutstandingFeesError event) {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));

        mFetchErrorView.setVisibility(View.VISIBLE);
        mFetchErrorText.setText(R.string.error_outstanding_fees);
    }
}
