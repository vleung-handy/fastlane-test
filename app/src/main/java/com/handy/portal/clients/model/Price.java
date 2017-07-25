package com.handy.portal.clients.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by sng on 7/24/17.
 */

public class Price implements Serializable {
    @SerializedName("amount")
    private int mAmount;
    @SerializedName("symbol")
    private String mSymbol;
    @SerializedName("code")
    private String mCode;

    public int getAmount() {
        return mAmount;
    }

    public String getSymbol() {
        return mSymbol;
    }

    public String getCode() {
        return mCode;
    }
}
