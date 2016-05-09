package com.handy.portal.payments.model;

import com.google.gson.annotations.SerializedName;

public class PaymentFlow
{
    public static final String STATUS_NEW = "new";
    public static final String STATUS_VALIDATED = "validated";
    public static final String STATUS_VERIFIED = "verified";
    public static final String STATUS_ERRORED = "errored";

    private static final String PROCESSOR_STRIPE = "stripe";
    private static final String PROCESSOR_STRIPE_DEBIT = "stripe_debit";

    @SerializedName("account_details")
    private String accountDetails;
    @SerializedName("processor_name")
    private String processorName;
    @SerializedName("status")
    private String status;

    public String getAccountDetails()
    {
        return accountDetails;
    }

    public String getStatus()
    {
        return status;
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
