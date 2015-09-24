package com.handy.portal.ui.element.payments;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TableLayout;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.model.payments.LegacyPaymentBatch;
import com.handy.portal.model.payments.NeoPaymentBatch;
import com.handy.portal.model.payments.PaymentBatch;
import com.handy.portal.model.payments.PaymentGroup;
import com.handy.portal.util.CurrencyUtils;
import com.handy.portal.util.DateTimeUtils;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class PaymentsBatchListItemView extends TableLayout
{

    @InjectView(R.id.payments_batch_list_item_date_text)
    protected TextView dateText;

    @InjectView(R.id.payments_batch_list_item_payment_amount_text)
    protected TextView paymentAmountText;

    @InjectView(R.id.payments_batch_list_item_job_info_text)
    protected TextView jobInfoText;

    @InjectView(R.id.payments_batch_list_item_status_text)
    protected TextView statusText;

    public PaymentsBatchListItemView(Context context)
    {
        super(context);
    }

    public PaymentsBatchListItemView(Context context, AttributeSet attributeSet)
    {
        super(context, attributeSet);
    }

    @Override
    protected void onFinishInflate()
    {
        super.onFinishInflate();
        ButterKnife.inject(this);
    }

    public void updateDisplay(PaymentBatch paymentBatch)
    {
        if (paymentBatch instanceof NeoPaymentBatch)
        {
            NeoPaymentBatch neoPaymentBatch = (NeoPaymentBatch) paymentBatch;
            dateText.setText(DateTimeUtils.formatDateRange(DateTimeUtils.SUMMARY_DATE_FORMATTER, neoPaymentBatch.getStartDate(), neoPaymentBatch.getEndDate()));
            paymentAmountText.setText(CurrencyUtils.formatPriceWithCents(neoPaymentBatch.getNetEarningsTotalAmount(), neoPaymentBatch.getCurrencySymbol()));
            statusText.setText(neoPaymentBatch.getStatus());
            //color status text

            statusText.setTextColor(getResources().getColor(NeoPaymentBatch.Status.FAILED.toString().equalsIgnoreCase(neoPaymentBatch.getStatus()) ? R.color.error_red : R.color.subtitle_grey));

            PaymentGroup paymentGroups[] = neoPaymentBatch.getPaymentGroups();
            int numJobs = 0;
            int numWithholdings = 0;
            for (int i = 0; i < paymentGroups.length; i++)
            {
                if (PaymentGroup.MachineName.completed_jobs.name().equals(paymentGroups[i].getMachineName()))
                {
                    numJobs = paymentGroups[i].getPayments().length;
                }
                else if (PaymentGroup.MachineName.withholdings.name().equals(paymentGroups[i].getMachineName()))
                {
                    numWithholdings = paymentGroups[i].getPayments().length;
                }
            }
            jobInfoText.setText(getResources().getString(R.string.payment_batch_list_entry_subtitle, numJobs, numWithholdings));

        }
        else if (paymentBatch instanceof LegacyPaymentBatch)
        {
            LegacyPaymentBatch legacyPaymentBatch = (LegacyPaymentBatch) paymentBatch;
            dateText.setText(DateTimeUtils.formatDateMonthDay(legacyPaymentBatch.getDate()));
            paymentAmountText.setText(CurrencyUtils.formatPriceWithCents(legacyPaymentBatch.getEarnedByProvider(), legacyPaymentBatch.getCurrencySymbol()));
            statusText.setText(legacyPaymentBatch.getStatus());
            jobInfoText.setText(getResources().getString(R.string.job_num) + legacyPaymentBatch.getBookingId());
        }

    }
}

