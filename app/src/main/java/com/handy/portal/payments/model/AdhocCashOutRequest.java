package com.handy.portal.payments.model;

import com.google.gson.annotations.SerializedName;

public class AdhocCashOutRequest {

    /**
     * used by server only for rate-limiting
     */
    @SerializedName("user_id")
    private String mUserId;

    /**
     * so we can handle the case in which
     * the expected payment on the UI does not match the server
     */
    @SerializedName("expected_payment")
    private int mExpectedPaymentCents;

    public AdhocCashOutRequest(final String userId,
                               final int expectedPaymentCents) {
        mUserId = userId;
        mExpectedPaymentCents = expectedPaymentCents;
    }
}
