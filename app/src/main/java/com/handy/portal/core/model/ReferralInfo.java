package com.handy.portal.core.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ReferralInfo implements Serializable
{
    @SerializedName("referral_code")
    private String mReferralCode;
    @SerializedName("referral_link")
    private String mReferralLink;
    @SerializedName("bonus_amount")
    private String mBonusAmount;
    @SerializedName("profile_url")
    private String mProfileUrl;

    public String getReferralCode()
    {
        return mReferralCode;
    }

    public String getReferralLink()
    {
        return mReferralLink;
    }

    public String getBonusAmount()
    {
        return mBonusAmount;
    }

    public String getProfileUrl()
    {
        return mProfileUrl;
    }
}
