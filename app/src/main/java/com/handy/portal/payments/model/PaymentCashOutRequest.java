package com.handy.portal.payments.model;

import com.google.gson.annotations.SerializedName;

public class PaymentCashOutRequest {

    @SerializedName("user_id")
    private String mUserId;

    @SerializedName("expected_payment")
    private int mExpectedPaymentCents;

    public PaymentCashOutRequest(final String userId,
                                 final int expectedPaymentCents) {
        mUserId = userId;
        mExpectedPaymentCents = expectedPaymentCents;
    }
}
