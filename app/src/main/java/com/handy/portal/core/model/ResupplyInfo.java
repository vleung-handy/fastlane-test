package com.handy.portal.core.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class ResupplyInfo implements Serializable
{
    @SerializedName("can_request_supplies")
    private boolean canRequestSupplies;
    @SerializedName("can_request_supplies_now")
    private boolean canRequestSuppliesNow;
    @SerializedName("helper_text")
    private String helperText;
    @SerializedName("withholding_amount")
    private String feeAmount;
    @SerializedName("supply_list")
    private List<SupplyListItem> supplyList;

    public boolean providerCanRequestSupplies()
    {
        return canRequestSupplies;
    }

    public boolean providerCanRequestSuppliesNow()
    {
        return canRequestSuppliesNow;
    }

    public String getHelperText()
    {
        return helperText;
    }

    public String getFeeAmount()
    {
        return feeAmount;
    }

    public List<SupplyListItem> getSupplyList()
    {
        return supplyList;
    }
}
