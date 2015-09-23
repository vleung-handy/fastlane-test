package com.handy.portal.ui.element.payments;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.model.payments.NeoPaymentBatch;
import com.handy.portal.model.payments.PaymentGroup;
import com.handy.portal.util.CurrencyUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class PaymentsDetailGroupView extends LinearLayout
{

    @InjectView(R.id.payments_detail_group_title_text)
    protected TextView titleText;

    @InjectView(R.id.payments_detail_group_payments_text)
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
        ButterKnife.inject(this);
    }

    public void updateDisplay(PaymentGroup paymentGroup, NeoPaymentBatch paymentBatch)
    {
        titleText.setText(getResources().getString(R.string.payment_detail_list_group_header, paymentGroup.getLabel(), paymentGroup.getPayments().length));
        paymentsText.setText(CurrencyUtils.formatPrice(paymentGroup.getDollarAmount(), paymentBatch.getCurrencySymbol()));
    }

}

