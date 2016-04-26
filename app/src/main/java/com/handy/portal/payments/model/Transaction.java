package com.handy.portal.payments.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

public class Transaction implements Serializable
{
    @SerializedName("title")
    private String mTitle;
    @SerializedName("type")
    private String mPaymentType;
    @SerializedName("payment_batches")
    private Batch[] mPaymentBatches;
    @SerializedName("amount")
    private int mAmountInCents;
    @SerializedName("currency")
    private String mCurrencySymbol;
    @SerializedName("policy")
    private Policy mPolicy;

    public Transaction() { }

    public Transaction(final String title, final String paymentType, final Batch[] paymentBatches, final int amountInCents, final String currencySymbol, final Policy policy)
    {
        mTitle = title;
        mPaymentType = paymentType;
        mPaymentBatches = paymentBatches;
        mAmountInCents = amountInCents;
        mCurrencySymbol = currencySymbol;
        mPolicy = policy;
    }

    public String getTitle() { return mTitle; }

    public String getPaymentType() { return mPaymentType; }

    public Batch[] getPaymentBatches() { return mPaymentBatches; }

    public int getAmountInCents() { return mAmountInCents; }

    public String getCurrencySymbol()
    {
        return mCurrencySymbol;
    }

    public Policy getPolicy() { return mPolicy; }


    public static class Batch implements Serializable
    {
        @SerializedName("date_start")
        private Date mDateStart;
        @SerializedName("date_end")
        private Date mDateEnd;

        public Batch() { }

        public Batch(final Date dateStart, final Date dateEnd)
        {
            mDateStart = dateStart;
            mDateEnd = dateEnd;
        }

        public Date getDateStart() { return mDateStart; }

        public Date getDateEnd() { return mDateEnd; }
    }


    public static class Policy implements Serializable
    {
        @SerializedName("reason")
        private String mReason;
        @SerializedName("description")
        private String mDescription;
        @SerializedName("policy_url")
        private String mPolicyUrl;

        public Policy() { }

        public Policy(final String reason, final String description, final String policyUrl)
        {
            mReason = reason;
            mDescription = description;
            mPolicyUrl = policyUrl;
        }

        public String getPolicyUrl() { return mPolicyUrl; }

        public String getDescription() { return mDescription; }

        public String getReason() { return mReason; }
    }
}
