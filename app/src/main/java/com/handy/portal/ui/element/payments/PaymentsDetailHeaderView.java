package com.handy.portal.ui.element.payments;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TableLayout;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.model.payments.NeoPaymentBatch;
import com.handy.portal.util.DateTimeUtils;
import com.handy.portal.util.TextUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by vleung on 9/14/15.
 */
public class PaymentsDetailHeaderView extends TableLayout
{
    @InjectView(R.id.payment_detail_date_range_text)
    TextView paymentDetailDateRangeText;

    @InjectView(R.id.payments_detail_total_payment_text)
    TextView paymentDetailTotalPaymentText;

    public PaymentsDetailHeaderView(Context context)
    {
        super(context);
    }

    public PaymentsDetailHeaderView(Context context, AttributeSet attributeSet)
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
        paymentDetailDateRangeText.setText(DateTimeUtils.formatDateDayOfWeekMonthDay(neoPaymentBatch.getStartDate()) + " - " + DateTimeUtils.formatDateDayOfWeekMonthDay(neoPaymentBatch.getEndDate()));
        paymentDetailTotalPaymentText.setText(TextUtils.formatPrice(neoPaymentBatch.getTotalAmountDollars(), neoPaymentBatch.getCurrencySymbol()));
    }

}

