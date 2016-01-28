package com.handy.portal.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PaymentInfo implements Serializable
{
    @SerializedName("amount")
    private int amount;
    @SerializedName("adjusted_amount")
    private float adjustedAmount;
    @SerializedName("code")
    private String currencyCode;
    @SerializedName("symbol")
    private String currencySymbol;
    @SerializedName("suffix")
    private String currencySuffix;

    public int getAmount()
    {
        return amount;
    }

    public float getAdjustedAmount()
    {
        return adjustedAmount;
    }

    public String getCurrencySymbol()
    {
        return currencySymbol;
    }

    public String getCurrencySuffix()
    {
        return currencySuffix;
    }

    public String getCurrencyCode()
    {
        return currencyCode;
    }
}
