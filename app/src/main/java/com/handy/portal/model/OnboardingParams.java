package com.handy.portal.model;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;
import com.handy.portal.util.TextUtils;

public class OnboardingParams
{
    public static int QUICKHACK = 0;

    @SerializedName("onboarding_enabled")
    private boolean mOnboardingEnabled;

    @SerializedName("onboarding_blocking")
    private boolean mOnboardingBlocking;

    @SerializedName("onboarding_web_url")
    private String mOnboardingWebUrl;


    public void HACK_SET(boolean enable, boolean blocking, String url)
    {
        mOnboardingEnabled = enable;
        mOnboardingBlocking = blocking;
        mOnboardingWebUrl = url;
    }

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
        if(QUICKHACK < 10)
        {
            mOnboardingEnabled = true;
        }
        else
        {
            mOnboardingEnabled = false;
        }

        return mOnboardingEnabled;
    }

    //may not be off handy domain, is full url
    @Nullable
    public String getOnboardingFullWebUrl()
    {
        if(QUICKHACK < 10)
        {
            mOnboardingWebUrl = "http://www.google.com";
        }
        else
        {
            mOnboardingWebUrl = null;
        }

        return mOnboardingWebUrl;
    }

    public boolean isOnboardingBlocking()
    {
        if(QUICKHACK < 5)
        {
            mOnboardingBlocking = true;
        }
        else
        {
            mOnboardingBlocking = false;
        }

        return mOnboardingBlocking;
    }

    public boolean shouldShowOnboarding()
    {
        return isOnboardingEnabled() && !TextUtils.isNullOrEmpty(getOnboardingFullWebUrl());
    }
}
