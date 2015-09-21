package com.handy.portal.ui.element.payments;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TableLayout;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.model.payments.NeoPaymentBatch;
import com.handy.portal.util.CurrencyUtils;
import com.handy.portal.util.DateTimeUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class PaymentsDetailListHeaderView extends TableLayout
{
    @InjectView(R.id.payment_detail_date_range_text)
    TextView paymentDetailDateRangeText;

    @InjectView(R.id.payments_detail_total_payment_text)
    TextView paymentDetailTotalPaymentText;

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
        ButterKnife.inject(this);
    }

    public void updateDisplay(NeoPaymentBatch neoPaymentBatch)
    {
        paymentDetailDateRangeText.setText(DateTimeUtils.formatDateRange(DateTimeUtils.DAY_OF_WEEK_MONTH_DAY_FORMATTER, neoPaymentBatch.getStartDate(), neoPaymentBatch.getEndDate()));
        paymentDetailTotalPaymentText.setText(CurrencyUtils.formatPrice(neoPaymentBatch.getTotalAmountDollars(), neoPaymentBatch.getCurrencySymbol()));
    }

}

