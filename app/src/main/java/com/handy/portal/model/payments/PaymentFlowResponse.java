package com.handy.portal.model.payments;

import com.google.gson.annotations.SerializedName;

public class PaymentFlowResponse
{
    @SerializedName("account_details")
    private String accountDetails;

    public String getAccountDetails()
    {
        return accountDetails;
    }
}
