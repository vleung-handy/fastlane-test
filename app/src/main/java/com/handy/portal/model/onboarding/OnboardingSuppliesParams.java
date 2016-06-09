package com.handy.portal.model.onboarding;

import com.google.gson.annotations.SerializedName;

public class OnboardingSuppliesParams
{
    @SerializedName("enabled")
    private boolean mEnabled;
    @SerializedName("info")
    private SuppliesInfo mInfo;

    public boolean isEnabled()
    {
        return mEnabled;
    }

    public SuppliesInfo getInfo()
    {
        return mInfo;
    }
}
