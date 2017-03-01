package com.handy.portal.payments.ui.element;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.library.util.CurrencyUtils;
import com.handy.portal.library.util.DateTimeUtils;
import com.handy.portal.payments.model.NeoPaymentBatch;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PaymentsBatchListHeaderView extends LinearLayout //TODO: see if we can make this more linked with the batch adapter data
{
    @BindView(R.id.payments_current_week_date_range_text)
    TextView currentWeekDateRangeText;

    @BindView(R.id.payments_current_week_total_earnings)
    TextView currentWeekTotalEarningsText;

    @BindView(R.id.payments_current_week_fees)
    TextView currentWeekFeesText;

    @BindView(R.id.payments_current_week_expected_payment)
    TextView currentWeekExpectedPaymentText;

    @BindView(R.id.payments_current_week_remaining_fees)
    TextView currentWeekRemainingFeesText;

    @BindView(R.id.payments_current_week_remaining_fees_row)
    ViewGroup currentWeekRemainingFeesRow;

    public PaymentsBatchListHeaderView(Context context) {
        super(context);
    }

    public PaymentsBatchListHeaderView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
    }

    public void updateDisplay(NeoPaymentBatch neoPaymentBatch) //assuming that current pay week is always returned and is the first element
    {
        currentWeekDateRangeText.setText(DateTimeUtils.formatDateRange(DateTimeUtils.SHORT_DAY_OF_WEEK_MONTH_DAY_FORMATTER, neoPaymentBatch.getStartDate(), neoPaymentBatch.getEndDate()));
        currentWeekRemainingFeesText.setText(CurrencyUtils.formatPriceWithCents(neoPaymentBatch.getRemainingFeeAmount(), neoPaymentBatch.getCurrencySymbol()));
        currentWeekExpectedPaymentText.setText(CurrencyUtils.formatPriceWithCents(neoPaymentBatch.getNetEarningsTotalAmount(), neoPaymentBatch.getCurrencySymbol()));
        currentWeekFeesText.setText(CurrencyUtils.formatPriceWithCents(neoPaymentBatch.getFeesTotalAmount(), neoPaymentBatch.getCurrencySymbol()));
        currentWeekTotalEarningsText.setText(CurrencyUtils.formatPriceWithCents(neoPaymentBatch.getGrossEarningsTotalAmount(), neoPaymentBatch.getCurrencySymbol()));
        currentWeekFeesText.setTextColor(ContextCompat.getColor(getContext(), neoPaymentBatch.getFeesTotalAmount() < 0 ? R.color.plumber_red : R.color.black));

        if (neoPaymentBatch.getRemainingFeeAmount() == 0) {
            currentWeekRemainingFeesRow.setVisibility(GONE);
        }
    }

}
