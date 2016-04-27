package com.handy.portal.model.payments;


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class PaymentOutstandingFees implements Serializable
{
    @SerializedName("currency_symbol")
    private String mCurrencySymbol;
    @SerializedName("total_fees_amount_in_cents")
    private int mTotalFeesInCents;
    @SerializedName("fees")
    private List<Payment> mFeesList;

    public String getCurrencySymbol()
    {
        return mCurrencySymbol;
    }

    public int getTotalFeesInCents()
    {
        return mTotalFeesInCents;
    }

    public List<Payment> getFeesList()
    {
        return mFeesList;
    }
}
