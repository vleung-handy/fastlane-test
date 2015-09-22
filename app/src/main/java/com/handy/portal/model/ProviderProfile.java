package com.handy.portal.model;

import com.google.gson.annotations.SerializedName;

public class ProviderProfile
{
    @SerializedName("personal_info")
    private ProviderPersonalInfo providerPersonalInfo;


    public ProviderPersonalInfo getProviderPersonalInfo()
    {
        return providerPersonalInfo;
    }
}
