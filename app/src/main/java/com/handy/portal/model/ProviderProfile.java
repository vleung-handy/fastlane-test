package com.handy.portal.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ProviderProfile implements Serializable
{
    @SerializedName("personal_info")
    private ProviderPersonalInfo mProviderPersonalInfo;
    @SerializedName("referral_info")
    private ReferralInfo mReferralInfo;
    @SerializedName("performance_info")
    private PerformanceInfo mPerformanceInfo;
    @SerializedName("resupply_info")
    private ResupplyInfo mResupplyInfo;

    public ProviderPersonalInfo getProviderPersonalInfo()
    {
        return mProviderPersonalInfo;
    }

    public ReferralInfo getReferralInfo()
    {
        return mReferralInfo;
    }

    public PerformanceInfo getPerformanceInfo()
    {
        return mPerformanceInfo;
    }

    public ResupplyInfo getResupplyInfo()
    {
        return mResupplyInfo;
    }

    public void setResupplyInfo(ResupplyInfo resupplyInfo)
    {
        mResupplyInfo = resupplyInfo;
    }
}
