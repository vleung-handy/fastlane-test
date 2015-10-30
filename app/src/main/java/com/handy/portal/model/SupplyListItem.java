package com.handy.portal.model;


import com.google.gson.annotations.SerializedName;

public class SupplyListItem
{
    @SerializedName("type")
    private String type;
    @SerializedName("amount")
    private int amount;

    public String getType()
    {
        return type;
    }

    public int getAmount()
    {
        return amount;
    }
}
