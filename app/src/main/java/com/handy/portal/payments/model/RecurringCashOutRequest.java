package com.handy.portal.payments.model;

import com.google.gson.annotations.SerializedName;

public class RecurringCashOutRequest {
    /**
     * used by server only for rate-limiting
     */
    @SerializedName("user_id")
    private String mUserId;

    /**
     * the recurring cash out payment batch period in days
     */
    @SerializedName("payment_batch_period_days")
    private int mPaymentBatchPeriodDays;

    public RecurringCashOutRequest(final String userId,
                                   final int paymentBatchPeriodDays) {
        mUserId = userId;
        mPaymentBatchPeriodDays = paymentBatchPeriodDays;
    }
}
