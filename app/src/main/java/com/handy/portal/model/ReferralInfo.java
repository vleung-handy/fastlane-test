package com.handy.portal.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ReferralInfo implements Serializable
{
    @SerializedName("referral_code")
    private String referralCode;
    @SerializedName("referral_link")
    private String referralLink;
    @SerializedName("bonus_amount")
    private String bonusAmount;

    public String getReferralCode()
    {
        return referralCode;
    }

    public String getReferralLink()
    {
        return referralLink;
    }

    public String getBonusAmount()
    {
        return bonusAmount;
    }
}
