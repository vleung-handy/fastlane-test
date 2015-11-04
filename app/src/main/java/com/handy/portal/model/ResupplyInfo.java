package com.handy.portal.model;

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
    private String withholdingAmount;
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

    public String getWithholdingAmount()
    {
        return withholdingAmount;
    }

    public List<SupplyListItem> getSupplyList()
    {
        return supplyList;
    }
}
