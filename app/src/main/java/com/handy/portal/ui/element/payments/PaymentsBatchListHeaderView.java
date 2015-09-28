package com.handy.portal.ui.element.payments;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.handy.portal.R;
import com.handy.portal.model.payments.NeoPaymentBatch;
import com.handy.portal.model.payments.PaymentBatches;
import com.handy.portal.util.CurrencyUtils;
import com.handy.portal.util.DateTimeUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class PaymentsBatchListHeaderView extends LinearLayout //TODO: see if we can make this more linked with the batch adapter data
{
    @InjectView(R.id.payments_current_week_date_range_text)
    TextView currentWeekDateRangeText;

    @InjectView(R.id.payments_current_week_total_earnings)
    TextView currentWeekTotalEarningsText;

    @InjectView(R.id.payments_current_week_withholdings)
    TextView currentWeekWithholdingsText;

    @InjectView(R.id.payments_current_week_expected_payment)
    TextView currentWeekExpectedPaymentText;

    @InjectView(R.id.payments_current_week_expected_payment_cents)
    TextView currentWeekExpectedPaymentCentsText;

    @InjectView(R.id.payments_current_week_remaining_withholdings)
    TextView currentWeekRemainingWithholdingsText;

    @InjectView(R.id.payments_current_week_remaining_withholdings_row)
    TableRow currentWeekRemainingWithholdingsRow;

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

    public void updateDisplay(PaymentBatches paymentBatches) //assuming that current pay week is always returned and is the first element
    {
        if (paymentBatches.getNeoPaymentBatches().length == 0)
        {
            Crashlytics.logException(new Exception("No non-legacy payment batches received! Expecting at least one (first entry should be the current week's payment batch)"));
            return;
        }

        NeoPaymentBatch neoPaymentBatch = paymentBatches.getNeoPaymentBatches()[0];
        currentWeekDateRangeText.setText(DateTimeUtils.formatDateRange(DateTimeUtils.DAY_OF_WEEK_MONTH_DAY_FORMATTER, neoPaymentBatch.getStartDate(), neoPaymentBatch.getEndDate()));
        currentWeekRemainingWithholdingsText.setText(CurrencyUtils.formatPriceWithCents(neoPaymentBatch.getRemainingWithholdingAmount(), neoPaymentBatch.getCurrencySymbol()));
        currentWeekExpectedPaymentText.setText(CurrencyUtils.formatPrice(neoPaymentBatch.getNetEarningsTotalAmount() * .01, neoPaymentBatch.getCurrencySymbol()));
        currentWeekExpectedPaymentCentsText.setText(CurrencyUtils.formatCents(neoPaymentBatch.getNetEarningsTotalAmount()));
        currentWeekWithholdingsText.setText(CurrencyUtils.formatPriceWithCents(neoPaymentBatch.getWithholdingsTotalAmount(), neoPaymentBatch.getCurrencySymbol()));
        currentWeekTotalEarningsText.setText(CurrencyUtils.formatPriceWithCents(neoPaymentBatch.getGrossEarningsTotalAmount(), neoPaymentBatch.getCurrencySymbol()));
        currentWeekWithholdingsText.setTextColor(getResources().getColor(neoPaymentBatch.getWithholdingsTotalAmount() < 0 ? R.color.error_red : R.color.black));

        if (neoPaymentBatch.getRemainingWithholdingAmount() == 0)
        {
            currentWeekRemainingWithholdingsRow.setVisibility(GONE);
        }
        else
        {
            currentWeekRemainingWithholdingsText.setText(CurrencyUtils.formatPriceWithCents(neoPaymentBatch.getRemainingWithholdingAmount(), neoPaymentBatch.getCurrencySymbol()));
        }
    }

}
