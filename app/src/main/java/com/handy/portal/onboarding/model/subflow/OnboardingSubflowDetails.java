package com.handy.portal.onboarding.model.subflow;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class OnboardingSubflowDetails implements Serializable
{
    @SerializedName("type")
    private SubflowType mType;
    @SerializedName("status")
    private SubflowStatus mStatus;
    @SerializedName("data")
    private SubflowData mData;

    public SubflowType getType()
    {
        return mType;
    }

    public SubflowStatus getStatus()
    {
        return mStatus;
    }

    public SubflowData getData()
    {
        return mData;
    }
}
