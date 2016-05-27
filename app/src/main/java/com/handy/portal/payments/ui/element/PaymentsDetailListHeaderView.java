package com.handy.portal.payments.ui.element;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.payments.model.NeoPaymentBatch;
import com.handy.portal.library.util.CurrencyUtils;
import com.handy.portal.library.util.DateTimeUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PaymentsDetailListHeaderView extends LinearLayout
{
    @Bind(R.id.payment_detail_date_range_text)
    TextView paymentDetailDateRangeText;

    @Bind(R.id.payments_detail_total_payment_text)
    TextView paymentDetailTotalPaymentText;

    @Bind(R.id.payments_detail_expect_deposit_date)
    TextView paymentDetailExpectDepositDate;

    @Bind(R.id.payments_detail_expect_deposit_date_layout)
    LinearLayout paymentsDetailExpectDepositLayout;

    public PaymentsDetailListHeaderView(Context context)
    {
        super(context);
    }

    public PaymentsDetailListHeaderView(Context context, AttributeSet attributeSet)
    {
        super(context, attributeSet);
    }

    @Override
    protected void onFinishInflate()
    {
        super.onFinishInflate();
        ButterKnife.bind(this);
    }

    public void updateDisplay(NeoPaymentBatch neoPaymentBatch)
    {
        paymentDetailDateRangeText.setText(DateTimeUtils.formatDateRange(DateTimeUtils.SHORT_DAY_OF_WEEK_MONTH_DAY_FORMATTER, neoPaymentBatch.getStartDate(), neoPaymentBatch.getEndDate()));
        paymentDetailTotalPaymentText.setText(CurrencyUtils.formatPriceWithCents(neoPaymentBatch.getNetEarningsTotalAmount(), neoPaymentBatch.getCurrencySymbol()));
        paymentDetailExpectDepositDate.setText(DateTimeUtils.SUMMARY_DATE_FORMATTER.format(neoPaymentBatch.getExpectedDepositDate()));

        if (neoPaymentBatch.getStatus().equalsIgnoreCase(NeoPaymentBatch.Status.FAILED.name()) || neoPaymentBatch.getStatus().equalsIgnoreCase(NeoPaymentBatch.Status.PAID.name()))
        {
            paymentsDetailExpectDepositLayout.setVisibility(GONE);
        }
    }
}
