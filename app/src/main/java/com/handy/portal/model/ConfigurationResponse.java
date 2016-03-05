package com.handy.portal.model;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

public class ConfigurationResponse
{
    //public static int QUICKHACK = 0;

    @SerializedName("hours_to_start_sending_messages")
    private int mHoursSpanningAvailableBookings; //we use this value for amount of time forward to display available bookings

    @SerializedName("complementary_jobs_enabled")
    private boolean mComplementaryJobsEnabled;

    @SerializedName("block_cleaner")
    private boolean mIsBlockCleaner;

    @SerializedName("checkout_rating_flow")
    private boolean mCheckoutRatingFlowEnabled;

    @SerializedName("block_payment_info")
    private boolean mBlockClaimsIfMissingAccountInformation;

    @SerializedName("notifications_in_pro_app")
    private boolean mShowNotificationMenuButton;

    @SerializedName("show_late_dispatch_opt_in")
    private boolean mShowLateDispatchOptIn;

    @SerializedName("location_schedule_service_enabled")
    private boolean mLocationScheduleServiceEnabled; //false by default

    @SerializedName("onboarding_enabled")
    private boolean mOnboardingEnabled;

    @SerializedName("onboarding_blocking")
    private boolean mOnboardingBlocking;

    @SerializedName("onboarding_web_url")
    private String mOnboardingWebUrl;

    public boolean isLocationScheduleServiceEnabled()
    {
        return mLocationScheduleServiceEnabled;
    }

    public boolean isComplementaryJobsEnabled()
    {
        return mComplementaryJobsEnabled;
    }

    public boolean isBlockCleaner()
    {
        return mIsBlockCleaner;
    }

    public boolean isCheckoutRatingFlowEnabled()
    {
        return mCheckoutRatingFlowEnabled;
    }

    public int getHoursSpanningAvailableBookings()
    {
        return mHoursSpanningAvailableBookings;
    }

    public boolean shouldBlockClaimsIfMissingAccountInformation()
    {
        return mBlockClaimsIfMissingAccountInformation;
    }

    public boolean shouldShowNotificationMenuButton()
    {
        return mShowNotificationMenuButton;
    }

    public boolean shouldShowLateDispatchOptIn()
    {
        return mShowLateDispatchOptIn;
    }

    public boolean isOnboardingEnabled()
    {
        return mOnboardingEnabled;
    }

    //may not be off handy domain, is full url
    @Nullable
    public String getOnboardingFullWebUrl()
    {
//        if(QUICKHACK < 10)
//        {
//            mOnboardingWebUrl = "http://www.google.com";
//        }
//        else
//        {
//            mOnboardingWebUrl = null;
//        }


        return mOnboardingWebUrl;
    }

    public boolean isOnboardingBlocking()
    {
//        if(QUICKHACK < 5)
//        {
//            mOnboardingBlocking = true;
//        }
//        else
//        {
//            mOnboardingBlocking = false;
//        }

        return mOnboardingBlocking;
    }

    public boolean shouldShowOnboarding()
    {
        return isOnboardingEnabled() && getOnboardingFullWebUrl() != null && !getOnboardingFullWebUrl().isEmpty();
    }
}
