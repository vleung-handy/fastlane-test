package com.handy.portal.model.payments;

import com.google.gson.annotations.SerializedName;

public class PaymentFlow
{
    private static final String PROCESSOR_STRIPE = "stripe";
    private static final String PROCESSOR_STRIPE_DEBIT = "stripe_debit";

    @SerializedName("account_details")
    private String accountDetails;
    @SerializedName("processor_name")
    private String processorName;

    public String getAccountDetails()
    {
        return accountDetails;
    }

    public boolean isBankAccount()
    {
        return processorName != null && processorName.equalsIgnoreCase(PROCESSOR_STRIPE);
    }

    public boolean isDebitCard()
    {
        return processorName != null && processorName.equalsIgnoreCase(PROCESSOR_STRIPE_DEBIT);
    }
}
