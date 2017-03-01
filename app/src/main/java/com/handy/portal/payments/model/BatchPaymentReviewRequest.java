package com.handy.portal.payments.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * a payment review request for a batch payment
 */
public class BatchPaymentReviewRequest {
    /**
     * the id of the batch payment to review
     */
    @SerializedName("batch_id")
    private String mBatchId;

    /**
     * the start date of the batch payment to review
     * needed for a backend fix
     */
    @SerializedName("batch_date_start")
    private Date mBatchStartDate;

    /**
     * the end date of the batch payment to review
     * needed for a backend fix
     */
    @SerializedName("batch_date_end")
    private Date mBatchEndDate;

    @SerializedName("machine_name")
    private String mPaymentSupportItemMachineName;

    /**
     * text inputted by the user when the “other” option is selected
     * (currently unsupported, so just sending null for now)
     */
    @SerializedName("other_info")
    private String mOtherInfo;

    public BatchPaymentReviewRequest(final String batchId,
                                     final Date batchStartDate,
                                     final Date batchEndDate,
                                     final String paymentSupportItemMachineName,
                                     final String otherInfo) {
        mBatchId = batchId;
        mBatchStartDate = batchStartDate;
        mBatchEndDate = batchEndDate;
        mPaymentSupportItemMachineName = paymentSupportItemMachineName;
        mOtherInfo = otherInfo;
    }
}
