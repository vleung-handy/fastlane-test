package com.handy.portal.model;

import com.google.gson.annotations.SerializedName;

public class ProviderProfile
{
    @SerializedName("personal_info")
    private ProviderPersonalInfo providerPersonalInfo;
    @SerializedName("referral_info")
    private ReferralInfo referralInfo;
    @SerializedName("performance_info")
    private PerformanceInfo performanceInfo;
    @SerializedName("resupply_info")
    private ResupplyInfo resupplyInfo;

    public ProviderPersonalInfo getProviderPersonalInfo()
    {
        return providerPersonalInfo;
    }

    public ReferralInfo getReferralInfo()
    {
        return referralInfo;
    }

    public PerformanceInfo getPerformanceInfo()
    {
        return performanceInfo;
    }

    public ResupplyInfo getResupplyInfo()
    {
        return resupplyInfo;
    }
}
