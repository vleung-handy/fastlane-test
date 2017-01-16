package com.handy.portal.payments.model;

import com.google.gson.annotations.SerializedName;

/**
 * a payment review request for a payment batch
 */
public class PaymentBatchReviewRequest
{
    /**
     * the id of the payment batch to review
     */
    @SerializedName("batch_id")
    private String mBatchId;
    public PaymentBatchReviewRequest(final String batchId)
    {
        mBatchId = batchId;
    }
}
