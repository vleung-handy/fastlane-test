package com.handy.portal.model;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;
import com.handy.portal.util.TextUtils;

public class OnboardingParams
{
    @SerializedName("onboarding_enabled")
    private boolean mOnboardingEnabled;

    @SerializedName("onboarding_blocking")
    private boolean mOnboardingBlocking;

    @SerializedName("onboarding_web_url")
    private String mOnboardingWebUrl;

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

        if(mOnboardingWebUrl == null && other.getOnboardingFullWebUrl() != null)
        {
            return false;
        }

        return (mOnboardingEnabled == other.isOnboardingEnabled() &&
                mOnboardingBlocking == other.isOnboardingBlocking() &&
                (mOnboardingWebUrl != null && mOnboardingWebUrl.equals(other.getOnboardingFullWebUrl()))
        );
    }

    public boolean isOnboardingEnabled()
    {
        return mOnboardingEnabled;
    }

    //may not be off handy domain, is full url
    @Nullable
    public String getOnboardingFullWebUrl()
    {
       return mOnboardingWebUrl;
    }

    public boolean isOnboardingBlocking()
    {
        return mOnboardingBlocking;
    }

    public boolean shouldShowOnboarding()
    {
        return isOnboardingEnabled() && !TextUtils.isNullOrEmpty(getOnboardingFullWebUrl());
    }
}
