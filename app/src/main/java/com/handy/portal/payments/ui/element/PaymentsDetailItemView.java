package com.handy.portal.payments.ui.element;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.library.util.CurrencyUtils;
import com.handy.portal.library.util.DateTimeUtils;
import com.handy.portal.payments.model.NeoPaymentBatch;
import com.handy.portal.payments.model.Payment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PaymentsDetailItemView extends LinearLayout
{

    @BindView(R.id.payments_detail_month_text)
    protected TextView monthText;

    @BindView(R.id.payments_detail_date_text)
    protected TextView dateText;

    @BindView(R.id.payments_detail_location_text)
    protected TextView locationText;

    @BindView(R.id.payments_detail_time_text)
    protected TextView timeText;

    @BindView(R.id.payments_detail_payment_text)
    protected TextView paymentText;

    public PaymentsDetailItemView(Context context)
    {
        super(context);
    }

    public PaymentsDetailItemView(Context context, AttributeSet attributeSet)
    {
        super(context, attributeSet);
    }

    @Override
    protected void onFinishInflate()
    {
        super.onFinishInflate();
        ButterKnife.bind(this);
    }

    public void updateDisplay(Payment payment, NeoPaymentBatch parentBatch)
    {
        monthText.setText(DateTimeUtils.getMonthShortName(payment.getDate()));
        dateText.setText(Integer.toString(DateTimeUtils.getDayOfMonth(payment.getDate())));
        locationText.setText(payment.getTitle());
        timeText.setText(payment.getSubTitle());
        paymentText.setText(CurrencyUtils.formatPriceWithCents(payment.getAmount(), parentBatch.getCurrencySymbol()));
        paymentText.setTextColor(ContextCompat.getColor(getContext(), payment.getAmount() < 0 ? R.color.plumber_red : R.color.black));
    }

}

