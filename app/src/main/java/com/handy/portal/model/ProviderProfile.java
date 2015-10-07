package com.handy.portal.model;

import com.google.gson.annotations.SerializedName;

public class ProviderProfile
{
    @SerializedName("personal_info")
    private ProviderPersonalInfo providerPersonalInfo;
    @SerializedName("referral_info")
    private ReferralInfo referralInfo;

    public ProviderPersonalInfo getProviderPersonalInfo()
    {
        return providerPersonalInfo;
    }

    public ReferralInfo getReferralInfo()
    {
        return referralInfo;
    }
}
