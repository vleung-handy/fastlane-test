package com.handy.portal.model;

import com.google.gson.annotations.SerializedName;

public class ResupplyInfo
{
    @SerializedName("request_supplies_allowed")
    private boolean requestSuppliesAllowed;
    @SerializedName("provider_can_request_supplies")
    private boolean providerCanRequestSupplies;
    @SerializedName("helper_text")
    private String helperText;

    public boolean isRequestSuppliesAllowed()
    {
        return requestSuppliesAllowed;
    }

    public boolean providerCanRequestSupplies()
    {
        return providerCanRequestSupplies;
    }

    public String getHelperText()
    {
        return helperText;
    }
}
