package com.handy.portal.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PaymentInfo implements Serializable
{
    @SerializedName("amount")
    private int mAmount;
    // TODO: Remove
    @SerializedName("adjusted_amount")
    private float mAdjustedAmount;
    @SerializedName("symbol")
    private String mCurrencySymbol;

    public int getAmount()
    {
        return mAmount;
    }

    public float getAdjustedAmount()
    {
        return mAdjustedAmount;
    }

    public String getCurrencySymbol()
    {
        return mCurrencySymbol;
    }

    public static class Builder
    {
        private int mAmount;
        private String mCurrencySymbol;

        public Builder withAmount(final int amount)
        {
            mAmount = amount;
            return this;
        }

        public Builder withCurrencySymbol(final String currencySymbol)
        {
            mCurrencySymbol = currencySymbol;
            return this;
        }

        public PaymentInfo build()
        {
            final PaymentInfo paymentInfo = new PaymentInfo();
            paymentInfo.mAmount = this.mAmount;
            paymentInfo.mCurrencySymbol = this.mCurrencySymbol;
            return paymentInfo;
        }
    }
}
