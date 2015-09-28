package com.handy.portal.model.payments;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class RequiresPaymentInfoUpdate implements Serializable
{
    @SerializedName("needs_update")
    private boolean needsUpdate;

    public boolean getNeedsUpdate()
    {
        return needsUpdate;
    }
}
