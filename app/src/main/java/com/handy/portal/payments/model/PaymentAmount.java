package com.handy.portal.payments.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * wrapper for payment amount information with currency info
 */
public class PaymentAmount implements Serializable {
    @SerializedName("amount")
    private Integer mAmountCents;
    @SerializedName("currency_symbol")
    private String mCurrencySymbol;
    @SerializedName("currency_code")
    private String mCurrencyCode;

    @Nullable
    public Integer getAmountCents() {
        return mAmountCents;
    }

    @NonNull
    public String getCurrencySymbol() {
        return mCurrencySymbol;
    }

    @Nullable
    public String getCurrencyCode() {
        return mCurrencyCode;
    }
}
