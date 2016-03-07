package com.handy.portal.model;

import com.google.gson.annotations.SerializedName;

public class ConfigurationResponse
{
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

    @SerializedName("onboarding_info")
    private OnboardingParams mOnboardingParams;

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

    public OnboardingParams getOnboardingParams()
    {

        //HACK FOR TESTING
        if(mOnboardingParams == null)
        {
            mOnboardingParams = new OnboardingParams();
            mOnboardingParams.HACK_SET(true, true, "http://www.google.com");
        }

        return mOnboardingParams;
    }

    public boolean shouldShowOnboarding()
    {
        return getOnboardingParams() != null && mOnboardingParams.shouldShowOnboarding();
    }
}
