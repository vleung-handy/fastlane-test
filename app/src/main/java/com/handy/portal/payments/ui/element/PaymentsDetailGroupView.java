package com.handy.portal.payments.ui.element;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.library.util.CurrencyUtils;
import com.handy.portal.payments.model.NeoPaymentBatch;
import com.handy.portal.payments.model.PaymentGroup;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PaymentsDetailGroupView extends LinearLayout
{

    @Bind(R.id.payments_detail_group_title_text)
    protected TextView titleText;

    @Bind(R.id.payments_detail_group_payments_text)
    protected TextView paymentsText;

    public PaymentsDetailGroupView(Context context)
    {
        super(context);
    }

    public PaymentsDetailGroupView(Context context, AttributeSet attributeSet)
    {
        super(context, attributeSet);
    }

    @Override
    protected void onFinishInflate()
    {
        super.onFinishInflate();
        ButterKnife.bind(this);
    }

    public void updateDisplay(PaymentGroup paymentGroup, NeoPaymentBatch paymentBatch)
    {
        titleText.setText(getResources().getString(R.string.payment_detail_list_group_header, paymentGroup.getLabel(), paymentGroup.getPayments().length));
        paymentsText.setText(CurrencyUtils.formatPriceWithCents(paymentGroup.getAmount(), paymentBatch.getCurrencySymbol()));
    }

}

