package com.handy.portal.model;

import com.google.gson.annotations.SerializedName;

public class ResupplyInfo
{
    @SerializedName("can_request_supplies")
    private boolean canRequestSupplies;
    @SerializedName("can_request_supplies_now")
    private boolean canRequestSuppliesNow;
    @SerializedName("helper_text")
    private String helperText;

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
}
