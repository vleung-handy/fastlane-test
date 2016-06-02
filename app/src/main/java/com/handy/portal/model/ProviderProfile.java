package com.handy.portal.model;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ProviderProfile implements Serializable
{
    @SerializedName("provider_id")
    private String mProviderId;
    @SerializedName("personal_info")
    private ProviderPersonalInfo mProviderPersonalInfo;
    @SerializedName("referral_info")
    private ReferralInfo mReferralInfo;
    @SerializedName("performance_info")
    private PerformanceInfo mPerformanceInfo;
    @SerializedName("resupply_info")
    private ResupplyInfo mResupplyInfo;

    @Nullable
    public String getProviderId()
    {
        return mProviderId;
    }

    @Nullable
    public ProviderPersonalInfo getProviderPersonalInfo()
    {
        return mProviderPersonalInfo;
    }

    @Nullable
    public ReferralInfo getReferralInfo()
    {
        return mReferralInfo;
    }

    @Nullable
    public PerformanceInfo getPerformanceInfo()
    {
        return mPerformanceInfo;
    }

    @Nullable
    public ResupplyInfo getResupplyInfo()
    {
        return mResupplyInfo;
    }

    public void setResupplyInfo(ResupplyInfo resupplyInfo)
    {
        mResupplyInfo = resupplyInfo;
    }
}
