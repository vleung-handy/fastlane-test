package com.handy.portal.ui.element.payments;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.model.payments.NeoPaymentBatch;
import com.handy.portal.util.CurrencyUtils;
import com.handy.portal.util.DateTimeUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PaymentsBatchListHeaderView extends LinearLayout //TODO: see if we can make this more linked with the batch adapter data
{
    @Bind(R.id.payments_current_week_date_range_text)
    TextView currentWeekDateRangeText;

    @Bind(R.id.payments_current_week_total_earnings)
    TextView currentWeekTotalEarningsText;

    @Bind(R.id.payments_current_week_fees)
    TextView currentWeekFeesText;

    @Bind(R.id.payments_current_week_expected_payment)
    TextView currentWeekExpectedPaymentText;

    @Bind(R.id.payments_current_week_expected_payment_cents)
    TextView currentWeekExpectedPaymentCentsText;

    @Bind(R.id.payments_current_week_remaining_fees)
    TextView currentWeekRemainingFeesText;

    @Bind(R.id.payments_current_week_remaining_fees_row)
    ViewGroup currentWeekRemainingFeesRow;

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
        ButterKnife.bind(this);
    }

    public void updateDisplay(NeoPaymentBatch neoPaymentBatch) //assuming that current pay week is always returned and is the first element
    {
        currentWeekDateRangeText.setText(DateTimeUtils.formatDateRange(DateTimeUtils.DAY_OF_WEEK_MONTH_DAY_FORMATTER, neoPaymentBatch.getStartDate(), neoPaymentBatch.getEndDate()));
        currentWeekRemainingFeesText.setText(CurrencyUtils.formatPriceWithCents(neoPaymentBatch.getRemainingFeeAmount(), neoPaymentBatch.getCurrencySymbol()));
        currentWeekExpectedPaymentText.setText(CurrencyUtils.formatPrice(neoPaymentBatch.getNetEarningsTotalAmount() * .01, neoPaymentBatch.getCurrencySymbol()));
        currentWeekExpectedPaymentCentsText.setText(CurrencyUtils.formatCents(neoPaymentBatch.getNetEarningsTotalAmount()));
        currentWeekFeesText.setText(CurrencyUtils.formatPriceWithCents(neoPaymentBatch.getFeesTotalAmount(), neoPaymentBatch.getCurrencySymbol()));
        currentWeekTotalEarningsText.setText(CurrencyUtils.formatPriceWithCents(neoPaymentBatch.getGrossEarningsTotalAmount(), neoPaymentBatch.getCurrencySymbol()));
        currentWeekFeesText.setTextColor(ContextCompat.getColor(getContext(), neoPaymentBatch.getFeesTotalAmount() < 0 ? R.color.error_red : R.color.black));

        if (neoPaymentBatch.getRemainingFeeAmount() == 0)
        {
            currentWeekRemainingFeesRow.setVisibility(GONE);
        }
    }

}
