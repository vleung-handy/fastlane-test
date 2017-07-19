package com.handy.portal.payments.ui.element;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.TableLayout;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.library.util.CurrencyUtils;
import com.handy.portal.library.util.DateTimeUtils;
import com.handy.portal.payments.model.LegacyPaymentBatch;
import com.handy.portal.payments.model.NeoPaymentBatch;
import com.handy.portal.payments.model.PaymentBatch;
import com.handy.portal.payments.model.PaymentGroup;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PaymentsBatchListItemView extends TableLayout {

    @BindView(R.id.payments_batch_list_item_date_text)
    protected TextView dateText;

    @BindView(R.id.payments_batch_list_item_payment_amount_text)
    protected TextView paymentAmountText;

    @BindView(R.id.payments_batch_list_item_job_info_text)
    protected TextView jobInfoText;

    @BindView(R.id.payments_batch_list_item_status_text)
    protected TextView statusText;

    public PaymentsBatchListItemView(Context context) {
        super(context);
    }

    public PaymentsBatchListItemView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
    }

    public void updateDisplay(PaymentBatch paymentBatch) {
        if (paymentBatch instanceof NeoPaymentBatch) {
            NeoPaymentBatch neoPaymentBatch = (NeoPaymentBatch) paymentBatch;
            dateText.setText(DateTimeUtils.formatDayRange(DateTimeUtils.SUMMARY_DATE_FORMATTER, neoPaymentBatch.getStartDate(), neoPaymentBatch.getEndDate()));
            paymentAmountText.setText(CurrencyUtils.formatPriceWithCents(neoPaymentBatch.getNetEarningsTotalAmount(), neoPaymentBatch.getCurrencySymbol()));
            statusText.setText(neoPaymentBatch.getStatus());
            //color status text

            statusText.setTextColor(ContextCompat.getColor(getContext(), NeoPaymentBatch.Status.FAILED.equalsIgnoreCase(neoPaymentBatch.getStatus()) ? R.color.plumber_red : R.color.tertiary_gray));

            PaymentGroup paymentGroups[] = neoPaymentBatch.getPaymentGroups();
            int numJobs = 0;
            int numFees = 0;
            for (PaymentGroup paymentGroup : paymentGroups) {
                if (PaymentGroup.MachineName.completed_jobs.name().equals(paymentGroup.getMachineName())) {
                    numJobs = paymentGroup.getPayments().length;
                }
                else if (PaymentGroup.MachineName.withholdings.name().equals(paymentGroup.getMachineName())) {
                    numFees = paymentGroup.getPayments().length;
                }
            }
            jobInfoText.setText(getResources().getString(R.string.payment_batch_list_entry_subtitle, numJobs, numFees));
            setEnabled(true);
        }
        else if (paymentBatch instanceof LegacyPaymentBatch) {
            LegacyPaymentBatch legacyPaymentBatch = (LegacyPaymentBatch) paymentBatch;
            dateText.setText(DateTimeUtils.formatDateMonthDay(legacyPaymentBatch.getDate()));
            paymentAmountText.setText(CurrencyUtils.formatPriceWithCents(legacyPaymentBatch.getEarnedByProvider(), legacyPaymentBatch.getCurrencySymbol()));
            statusText.setText(legacyPaymentBatch.getStatus());
            statusText.setTextColor(ContextCompat.getColor(getContext(), R.color.tertiary_gray));
            jobInfoText.setText(getResources().getString(R.string.job_number_formatted,
                    Integer.toString(legacyPaymentBatch.getBookingId())));
            setEnabled(false);
        }

    }
}

