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

    @SerializedName("booking_geofencing_service_enabled")
    private boolean mBookingGeofenceServiceEnabled;

    @SerializedName("boxed_supplies_enabled")
    private boolean mBoxedSuppliesEnabled;

    @SerializedName("help_center")
    private HelpCenterInfo mHelpCenterInfo;

    @SerializedName("number_of_days_for_available_jobs")
    private int mNumberOfDaysForAvailableJobs;

    @SerializedName("show_payment_transactions")
    private boolean mShowBookingTransactionSummary;

    @SerializedName("weekly_payment_tiers_enabled")
    private boolean mWeeklyPaymentTiersEnabled;

    @SerializedName("pending_requests_inbox_enabled")
    private boolean mPendingRequestsInboxEnabled;

    public boolean isPendingRequestsInboxEnabled()
    {
        return mPendingRequestsInboxEnabled;
    }

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

    public boolean shouldShowWeeklyPaymentTiers()
    {
        return mWeeklyPaymentTiersEnabled;
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

    public boolean showBookingTransactionSummary()
    {
        return mShowBookingTransactionSummary;
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
