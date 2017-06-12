package com.handy.portal.payments.viewmodel;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.handy.portal.R;
import com.handy.portal.library.util.CurrencyUtils;
import com.handy.portal.library.util.DateTimeUtils;
import com.handy.portal.library.util.TextUtils;
import com.handy.portal.payments.model.NeoPaymentBatch;

/**
 * ViewModel for {@link com.handy.portal.payments.ui.element.PaymentsDetailListHeaderView}
 */
public class PaymentDetailHeaderViewModel {

    private final NeoPaymentBatch mNeoPaymentBatch;
    private final boolean mShouldShowCashOutButton;

    public PaymentDetailHeaderViewModel(@NonNull NeoPaymentBatch neoPaymentBatch,
                                        boolean canShowCashOutButton) {
        mNeoPaymentBatch = neoPaymentBatch;
        mShouldShowCashOutButton = canShowCashOutButton;
    }

    public boolean shouldShowPaymentStatusLayout() {
        return !((mNeoPaymentBatch.getPaymentGroups() == null
                || mNeoPaymentBatch.getPaymentGroups().length == 0) //unsure if 0 payment groups means 0 payment
                && mNeoPaymentBatch.getFeesTotalAmount() == 0
                && mNeoPaymentBatch.getNetEarningsTotalAmount() == 0
                && mNeoPaymentBatch.getGrossEarningsTotalAmount() == 0
                && mNeoPaymentBatch.getRemainingFeeAmount() == 0);
    }

    /**
     * only show the payment method view if payment method info present
     * last4 is nullable because there are moments when a batch
     * won't be associated with any payment flow
     */
    public boolean shouldShowPaymentMethodDetails() {
        return !TextUtils.isNullOrEmpty(mNeoPaymentBatch.getPaymentMethodLast4Digits());
    }

    public boolean shouldShowCashOutButton() {
        return mShouldShowCashOutButton;
    }

    public boolean shouldApparentlyEnableCashOutButton() {
        return mNeoPaymentBatch.isCashOutEnabled();
    }

    /**
     * for spannable purposes only
     */
    @Nullable
    public String getPaymentStatus() {
        return mNeoPaymentBatch.getStatus();
    }

    public String getPaymentStatusFormatted(@NonNull Context context) {
        return context.getString(R.string.payment_status_formatted,
                mNeoPaymentBatch.getStatus() == null ? "" : mNeoPaymentBatch.getStatus());
    }

    public String getPaymentMethodDetails(@NonNull Context context) {
        return context.getResources().getString(R.string.payment_method_info_last4_formatted,
                mNeoPaymentBatch.getPaymentMethodLast4Digits());
    }

    /**
     * only show expected deposit date if the payment status reflects a non-terminal state
     * (is not FAILED and not PAID)
     *
     * @return
     */
    public boolean shouldShowExpectedDepositDate() {
        String batchPaymentStatus = mNeoPaymentBatch.getStatus();
        return !NeoPaymentBatch.Status.FAILED.equalsIgnoreCase(batchPaymentStatus)
                && !NeoPaymentBatch.Status.PAID.equalsIgnoreCase(batchPaymentStatus);
    }

    public String getExpectedDepositDate(@NonNull Context context) {
        return context.getResources().getString(R.string.expected_deposit_formatted,
                DateTimeUtils.formatDayOfWeekMonthDate(
                        mNeoPaymentBatch.getExpectedDepositDate()));
    }

    public String getDateRange() {
        return DateTimeUtils.formatDateRange(DateTimeUtils.SHORT_DAY_OF_WEEK_MONTH_DAY_FORMATTER,
                mNeoPaymentBatch.getStartDate(),
                mNeoPaymentBatch.getEndDate());
    }

    public String getTotalPayment() {
        return CurrencyUtils.formatPriceWithCents(mNeoPaymentBatch.getNetEarningsTotalAmount(),
                mNeoPaymentBatch.getCurrencySymbol());
    }
}
