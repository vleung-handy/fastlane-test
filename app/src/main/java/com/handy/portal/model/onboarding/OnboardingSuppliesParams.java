package com.handy.portal.model.onboarding;

import com.google.gson.annotations.SerializedName;

public class OnboardingSuppliesParams
{
    @SerializedName("enabled")
    private boolean mEnabled;
    @SerializedName("info")
    private OnboardingSuppliesInfo mInfo;

    public boolean isEnabled()
    {
        return mEnabled;
    }

    public OnboardingSuppliesInfo getInfo()
    {
        return mInfo;
    }
}
