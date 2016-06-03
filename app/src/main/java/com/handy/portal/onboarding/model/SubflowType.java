package com.handy.portal.onboarding.model;

import com.google.gson.annotations.SerializedName;

public enum SubflowType
{
    @SerializedName("status")
    STATUS(StatusSubflowData.class),
    @SerializedName("claim")
    CLAIM(ClaimSubflowData.class),
    @SerializedName("supplies")
    SUPPLIES(SuppliesSubflowData.class),
    @SerializedName("confirmation")
    CONFIRMATION(ConfirmationSubflowData.class),;

    private Class dataClass;

    SubflowType(final Class dataClass)
    {
        this.dataClass = dataClass;
    }

    public Class getDataClass()
    {
        return dataClass;
    }
}
