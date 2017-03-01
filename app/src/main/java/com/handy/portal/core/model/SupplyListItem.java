package com.handy.portal.core.model;


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class SupplyListItem implements Serializable {
    @SerializedName("type")
    private String type;
    @SerializedName("amount")
    private int amount;

    public String getType() {
        return type;
    }

    public int getAmount() {
        return amount;
    }
}
