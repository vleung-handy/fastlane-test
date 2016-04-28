package com.handy.portal.model;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;
import com.handy.portal.util.TextUtils;

public class OnboardingParams
{
    @SerializedName("onboarding_enabled")
    private boolean mOnboardingEnabled;

    @SerializedName("onboarding_is_blocking")
    private boolean mOnboardingBlocking;

    @SerializedName("onboarding_complete_web_url")
    private String mOnboardingCompleteWebUrl;

    @SerializedName("onboarding_use_native_flow")
    private boolean mOnboardingUseNativeFlow;

    @Override
    public boolean equals(Object obj)
    {
        if (!(obj instanceof OnboardingParams))
        {
            return false;
        }

        if (obj == this)
        {
            return true;
        }

        OnboardingParams other = (OnboardingParams) obj;

        if (mOnboardingCompleteWebUrl == null && other.getOnboardingCompleteWebUrl() != null)
        {
            return false;
        }

        return (mOnboardingEnabled == other.isOnboardingEnabled() &&
                mOnboardingBlocking == other.isOnboardingBlocking() &&
                (mOnboardingCompleteWebUrl != null && mOnboardingCompleteWebUrl.equals(other.getOnboardingCompleteWebUrl()))
        );
    }

    public boolean isOnboardingEnabled()
    {
        return mOnboardingEnabled;
    }

    //may not be off handy domain, is full url
    @Nullable
    public String getOnboardingCompleteWebUrl()
    {
        return mOnboardingCompleteWebUrl;
    }

    public boolean isOnboardingBlocking()
    {
        return mOnboardingBlocking;
    }

    public boolean shouldShowWebOnboarding()
    {
        return isOnboardingEnabled() && !TextUtils.isNullOrEmpty(getOnboardingCompleteWebUrl());
    }

    public boolean shouldShowNativeOnboarding()
    {
        return isOnboardingEnabled() && mOnboardingUseNativeFlow;
    }

}
