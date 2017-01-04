package com.handy.portal.payments.model;

import com.google.gson.annotations.SerializedName;

public class PaymentFlow
{
    public static final String STATUS_NEW = "new";
    public static final String STATUS_VALIDATED = "validated";
    public static final String STATUS_VERIFIED = "verified";
    public static final String STATUS_ERRORED = "errored";

    /**
     * processor names for the new stripe connect payment flow.
     * confirmed with backend that we no longer need to support the old payment flow
     * with processor names "stripe" and "stripe_debit"
     */
    private static final String PROCESSOR_STRIPE_CONNECT = "stripe_connect_bank_account";
    private static final String PROCESSOR_STRIPE_CONNECT_DEBIT = "stripe_connect_debit";

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
        return processorName != null
                && processorName.equalsIgnoreCase(PROCESSOR_STRIPE_CONNECT);
    }

    public boolean isDebitCard()
    {
        return processorName != null
                && processorName.equalsIgnoreCase(PROCESSOR_STRIPE_CONNECT_DEBIT);
    }
}
