package com.handy.portal.ui.element.payments;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.model.payments.NeoPaymentBatch;
import com.handy.portal.model.payments.PaymentBatches;
import com.handy.portal.util.DateTimeUtils;
import com.handy.portal.util.TextUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by vleung on 9/14/15.
 */
public class PaymentsBatchListHeaderView extends LinearLayout
{
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

    public PaymentsBatchListHeaderView(Context context)
    {
        super(context);
    }

    public PaymentsBatchListHeaderView(Context context, AttributeSet attributeSet)
    {
        super(context, attributeSet);
    }

    @Override
    protected void onFinishInflate()
    {
        super.onFinishInflate();
        ButterKnife.inject(this);
    }

    public void updateDisplay(PaymentBatches paymentBatches)
    {
        NeoPaymentBatch neoPaymentBatch = paymentBatches.getNeoPaymentBatches()[0];
        currentWeekDateRangeText.setText(DateTimeUtils.formatDateDayOfWeekMonthDay(neoPaymentBatch.getStartDate()) + " - " + DateTimeUtils.formatDateDayOfWeekMonthDay(neoPaymentBatch.getEndDate()));
        currentWeekRemainingWithholdingsText.setText(TextUtils.formatPrice(neoPaymentBatch.getRemainingWithholdingDollarAmount(), neoPaymentBatch.getCurrencySymbol()));
        currentWeekExpectedPaymentText.setText(TextUtils.formatPrice(neoPaymentBatch.getTotalAmountDollars(), neoPaymentBatch.getCurrencySymbol()));
        currentWeekWithholdingsText.setText(TextUtils.formatPrice(neoPaymentBatch.getWithholdingsTotalAmount(), neoPaymentBatch.getCurrencySymbol()));
        currentWeekTotalEarningsText.setText(TextUtils.formatPrice(neoPaymentBatch.getTotalAmountDollars(), neoPaymentBatch.getCurrencySymbol()));
    }

}

