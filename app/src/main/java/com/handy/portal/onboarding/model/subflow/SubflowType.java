package com.handy.portal.onboarding.model.subflow;

import com.google.gson.annotations.SerializedName;

public enum SubflowType
{
    @SerializedName("status")
    STATUS(1),
    @SerializedName("claim")
    CLAIM(2),
    @SerializedName("supplies")
    SUPPLIES(2),
    @SerializedName("confirmation")
    CONFIRMATION(1),;

    private int mNumberOfSteps;

    SubflowType(final int numberOfSteps)
    {
        mNumberOfSteps = numberOfSteps;
    }

    public int getNumberOfSteps()
    {
        return mNumberOfSteps;
    }
}
