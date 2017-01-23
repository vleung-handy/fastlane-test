package com.handy.portal.payments.model;

import com.google.gson.annotations.SerializedName;

/**
 * a payment review request for a batch payment
 */
public class BatchPaymentReviewRequest
{
    /**
     * the id of the batch payment to review
     */
    @SerializedName("batch_id")
    private String mBatchId;

    @SerializedName("machine_name")
    private String mPaymentSupportItemMachineName;

    /**
     * text inputted by the user when the “other” option is selected
     * (currently unsupported, so just sending null for now)
     */
    @SerializedName("other_info")
    private String mOtherInfo;

    public BatchPaymentReviewRequest(final String batchId,
                                     final String paymentSupportItemMachineName,
                                     final String otherInfo)
    {
        mBatchId = batchId;
        mPaymentSupportItemMachineName = paymentSupportItemMachineName;
        mOtherInfo = otherInfo;
    }
}
