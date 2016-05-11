package com.handy.portal.model;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;
import com.handy.portal.model.onboarding.OnboardingParams;

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

    @SerializedName("booking_geofencing_service_enabled")
    private boolean mBookingGeofenceServiceEnabled;

    @SerializedName("boxed_supplies_enabled")
    private boolean mBoxedSuppliesEnabled;

    @SerializedName("onboarding_info")
    private OnboardingParams mOnboardingParams;

    @SerializedName("help_center")
    private HelpCenterInfo mHelpCenterInfo;

    @SerializedName("number_of_days_for_available_jobs")
    private int mNumberOfDaysForAvailableJobs;

    public boolean isLocationScheduleServiceEnabled()
    {
        return mLocationScheduleServiceEnabled;
    }

    public boolean isBookingGeofenceServiceEnabled()
    {
        return mBookingGeofenceServiceEnabled;
    }

    public boolean isBoxedSuppliesEnabled()
    {
        return mBoxedSuppliesEnabled;
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

    @Nullable
    public OnboardingParams getOnboardingParams()
    {
        return mOnboardingParams;
    }

    public boolean shouldShowWebOnboarding()
    {
        return getOnboardingParams() != null && mOnboardingParams.shouldShowWebOnboarding();
    }

    public boolean shouldShowNativeOnboarding()
    {
        return getOnboardingParams() != null && mOnboardingParams.shouldShowNativeOnboarding();
    }

    public boolean shouldUseHelpCenterWebView()
    {
        return mHelpCenterInfo != null && mHelpCenterInfo.shouldUseHelpCenterWebView();
    }

    public int getNumberOfDaysForAvailableJobs()
    {
        return mNumberOfDaysForAvailableJobs;
    }

    public String getHelpCenterUrl()
    {
        return mHelpCenterInfo == null ? null : mHelpCenterInfo.getHelpCenterUrl();
    }

    public static class HelpCenterInfo
    {
        @SerializedName("should_use_help_center_web_view")
        private boolean mShouldUseHelpCenterWebView;

        @SerializedName("help_center_url")
        private String mHelpCenterUrl;

        public boolean shouldUseHelpCenterWebView()
        {
            return mShouldUseHelpCenterWebView;
        }

        public String getHelpCenterUrl()
        {
            return mHelpCenterUrl;
        }
    }
}
