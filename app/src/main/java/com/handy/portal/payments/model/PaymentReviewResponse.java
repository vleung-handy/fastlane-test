package com.handy.portal.payments.model;

import com.google.gson.annotations.SerializedName;

/**
 * response to the payment review requests
 */
public class PaymentReviewResponse {
    @SerializedName("success")
    private boolean mSuccess;
    /**
     * user-facing message to be displayed
     */
    @SerializedName("message")
    private String mMessage;

    public String getMessage() {
        return mMessage;
    }

    public boolean isSuccess() {
        return mSuccess;
    }
}

