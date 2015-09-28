package com.handy.portal.model.payments;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class RequiresUpdate implements Serializable
{
    @SerializedName("needs_update")
    private Boolean needsUpdate;


    public Boolean getNeedsUpdate()
    {
        return needsUpdate;
    }
}
