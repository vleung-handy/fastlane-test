package com.handy.portal.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.manager.PaymentsManager;
import com.handy.portal.model.payments.NeoPaymentBatch;
import com.handy.portal.ui.view.PaymentDetailExandableListView;
import com.handy.portal.util.DateTimeUtils;
import com.handy.portal.util.TextUtils;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public final class PaymentsDetailFragment extends ActionBarFragment implements ExpandableListView.OnGroupClickListener
{
    @Inject
    PaymentsManager paymentsManager;

    @InjectView(R.id.payments_detail_list_view)
    PaymentDetailExandableListView paymentDetailExandableListView;

    @InjectView(R.id.payment_detail_date_range_text)
    TextView paymentDetailDateRangeText;

    @InjectView(R.id.payments_detail_total_payment_text)
    TextView paymentDetailTotalPaymentText;

    NeoPaymentBatch neoPaymentBatch;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if(getArguments()!=null)
        {
            this.neoPaymentBatch = (NeoPaymentBatch) getArguments().getSerializable(BundleKeys.PAYMENT_BATCH);
        }
        //else something is really wrong, throw an exception
    }

    @Override
    public final View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                                   final Bundle savedInstanceState)
    {
        final View view = inflater
                .inflate(R.layout.fragment_payments_detail, container, false);

        ButterKnife.inject(this, view);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        paymentDetailExandableListView.setOnGroupClickListener(this);
        paymentDetailDateRangeText.setText(DateTimeUtils.formatDateDayOfWeekMonthDay(neoPaymentBatch.getStartDate()) + " - " + DateTimeUtils.formatDateDayOfWeekMonthDay(neoPaymentBatch.getEndDate()));
        paymentDetailTotalPaymentText.setText(TextUtils.formatPrice(neoPaymentBatch.getTotalAmountDollars(), neoPaymentBatch.getCurrencySymbol()));
        paymentDetailExandableListView.populateList(neoPaymentBatch);
        for(int i = 0; i< neoPaymentBatch.getPaymentGroups().length; i++)
        {
            paymentDetailExandableListView.expandGroup(i);

        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        setActionBar("Payment Details", true);//TODO: change to resource id
        if (!MainActivityFragment.clearingBackStack)
        {
        }
    }

    @Override
    public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id)
    {
        return true;
    }
}
