package com.handy.portal.model;

import com.google.gson.annotations.SerializedName;

public class ResupplyInfo
{
    @SerializedName("can_request_supplies")
    private boolean canRequestSupplies;

    public boolean canRequestSupplies()
    {
        return canRequestSupplies;
    }
}
