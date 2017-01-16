package com.handy.portal.payments.ui.element;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handy.portal.R;
import com.handy.portal.library.util.CurrencyUtils;
import com.handy.portal.library.util.DateTimeUtils;
import com.handy.portal.payments.model.NeoPaymentBatch;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PaymentsDetailListHeaderView extends LinearLayout
{
    @BindView(R.id.payment_detail_date_range_text)
    TextView paymentDetailDateRangeText;

    @BindView(R.id.payments_detail_total_payment_text)
    TextView paymentDetailTotalPaymentText;

    @BindView(R.id.payment_details_list_header_payment_status_layout)
    View mPaymentStatusLayout;

    @BindView(R.id.payment_details_list_header_payment_status_text)
    TextView mPaymentStatusText;

    @BindView(R.id.payment_details_list_header_payment_status_help_button)
    View mPaymentStatusHelpButton;

    @BindView(R.id.payments_detail_list_header_payment_status_expected_deposit_date)
    TextView paymentStatusExpectedDepositDate;

    @BindView(R.id.payments_detail_list_header_payment_status_payment_method_text)
    TextView mPaymentStatusPaymentMethodText;

    private Callback mCallback;

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

    private void updatePaymentStatusLayout(@NonNull NeoPaymentBatch neoPaymentBatch)
    {
        String batchPaymentStatus = neoPaymentBatch.getStatus();
        if (NeoPaymentBatch.Status.FAILED.name().equalsIgnoreCase(batchPaymentStatus)
                || NeoPaymentBatch.Status.PAID.name().equalsIgnoreCase(batchPaymentStatus))
        {
            //don't show the deposit date if payment status is failed or paid
            paymentStatusExpectedDepositDate.setVisibility(GONE);
        }
        else
        {
            paymentStatusExpectedDepositDate.setVisibility(VISIBLE);
            paymentStatusExpectedDepositDate.setText(
                    getResources().getString(R.string.expected_deposit_formatted,
                            DateTimeUtils.SUMMARY_DATE_FORMATTER.format(
                                    neoPaymentBatch.getExpectedDepositDate())));
        }

        final String paymentStatusFormatted = getContext().getString(R.string.payment_status_formatted, neoPaymentBatch.getStatus());
        mPaymentStatusText.setText(paymentStatusFormatted, TextView.BufferType.SPANNABLE);
        final Spannable spannable = (Spannable) mPaymentStatusText.getText();
        final int start = paymentStatusFormatted.indexOf(neoPaymentBatch.getStatus());
        final int end = start + neoPaymentBatch.getStatus().length();
        int colorResourceId;
        if(NeoPaymentBatch.Status.FAILED.toString().equalsIgnoreCase(neoPaymentBatch.getStatus()))
        {
            colorResourceId = R.color.error_red;
        }
        else
        {
            colorResourceId = R.color.handy_green;
        }
        spannable.setSpan(
                new ForegroundColorSpan(ContextCompat.getColor(getContext(), colorResourceId)),
                start,
                end,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        /*
        only show the payment method view if payment method info present
        last4 is nullable because there are moments when a batch
        won't be associated with any payment flow
         */
        if(TextUtils.isEmpty(neoPaymentBatch.getPaymentMethodLast4Digits()))
        {
            mPaymentStatusPaymentMethodText.setVisibility(GONE);
        }
        else
        {
            mPaymentStatusPaymentMethodText.setVisibility(VISIBLE);
            mPaymentStatusPaymentMethodText.setText(getResources().getString(R.string.payment_method_info_last4_formatted,
                    neoPaymentBatch.getPaymentMethodLast4Digits()));
        }
    }

    public void updateDisplay(@NonNull NeoPaymentBatch neoPaymentBatch)
    {
        paymentDetailDateRangeText.setText(DateTimeUtils.formatDateRange(DateTimeUtils.SHORT_DAY_OF_WEEK_MONTH_DAY_FORMATTER, neoPaymentBatch.getStartDate(), neoPaymentBatch.getEndDate()));
        paymentDetailTotalPaymentText.setText(CurrencyUtils.formatPriceWithCents(neoPaymentBatch.getNetEarningsTotalAmount(), neoPaymentBatch.getCurrencySymbol()));

        if(neoPaymentBatch.getPaymentGroups() == null
                || neoPaymentBatch.getPaymentGroups().length == 0)
        {
            //don't show the payment status layout if there are no payments
            mPaymentStatusLayout.setVisibility(GONE);
        }
        else
        {
            mPaymentStatusLayout.setVisibility(VISIBLE);
            updatePaymentStatusLayout(neoPaymentBatch);
        }
    }

    @OnClick(R.id.payment_details_list_header_payment_status_help_button)
    public void onRequestStatusSupportButtonClicked()
    {
        if(mCallback != null)
        {
            mCallback.onRequestStatusSupportButtonClicked();
        }
    }

    public void setCallbackListener(Callback callbackListener)
    {
        mCallback = callbackListener;
    }

    public interface Callback
    {
        void onRequestStatusSupportButtonClicked();
    }
}
