package com.handy.portal.ui.element.payments;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.model.payments.Payment;
import com.handy.portal.model.payments.NeoPaymentBatch;
import com.handy.portal.util.DateTimeUtils;
import com.handy.portal.util.TextUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by vleung on 9/14/15.
 */
public class PaymentsDetailItemView extends LinearLayout
{

    @InjectView(R.id.payments_detail_month_text)
    protected TextView monthText;

    @InjectView(R.id.payments_detail_date_text)
    protected TextView dateText;

    @InjectView(R.id.payments_detail_location_text)
    protected TextView locationText;

    @InjectView(R.id.payments_detail_time_text)
    protected TextView timeText;

    @InjectView(R.id.payments_detail_payment_text)
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
        ButterKnife.inject(this);
    }

    public void updateDisplay(Payment payment, NeoPaymentBatch parentBatch)
    {
        monthText.setText(DateTimeUtils.getMonthShortName(payment.getDate()));
        dateText.setText(DateTimeUtils.getDayOfMonth(payment.getDate()) + "");
        locationText.setText(payment.getTitle());
        timeText.setText(payment.getSubTitle());
        paymentText.setText(TextUtils.formatPrice(payment.getDollarAmount(), parentBatch.getCurrencySymbol()));
        paymentText.setTextColor(getResources().getColor(payment.getDollarAmount() < 0 ? R.color.handy_love : R.color.black));
    }

}

