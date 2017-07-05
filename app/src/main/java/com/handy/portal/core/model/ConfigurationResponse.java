package com.handy.portal.core.model;

import com.google.gson.annotations.SerializedName;

public class ConfigurationResponse {
    @SerializedName("checkout_rating_flow")
    private boolean mCheckoutRatingFlowEnabled;

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

    @SerializedName("number_of_days_for_scheduled_jobs")
    private int mNumberOfDaysForScheduledJobs;

    @SerializedName("show_payment_transactions")
    private boolean mShowBookingTransactionSummary;

    @SerializedName("weekly_payment_tiers_enabled")
    private boolean mWeeklyPaymentTiersEnabled;

    @SerializedName("customer_no_show_modal_enabled")
    private boolean mCustomerNoShowModalEnabled;

    @SerializedName("number_of_days_for_requested_jobs")
    private int mNumberOfDaysForRequestedJobs;

    @SerializedName("profile_picture_upload_enabled")
    private boolean mProfilePictureUploadEnabled;

    @SerializedName("request_dismissal")
    private RequestDismissal mRequestDismissal;

    @SerializedName("appsee_analytics_enabled")
    private boolean mAppseeAnalyticsEnabled;

    @SerializedName("login_slt_enabled")
    private boolean mSltEnabled;

    @SerializedName("pro_availability_schedule_enabled")
    private boolean mAvailableHoursEnabled;

    @SerializedName("portal_profile_share_enabled")
    private boolean mProfileShareEnabled;

    @SerializedName("more_tab_full_page_enabled")
    private boolean mMoreTabEnabled;

    @SerializedName("pro_announcements_enabled")
    private boolean mAnnouncementsEnabled;

    @SerializedName("schedule_tab_requests_enabled")
    private boolean mScheduleTabRequestedEnabled;

    @SerializedName("daily_pro_payments_enabled")
    private boolean mDailyProPaymentsEnabled;

    @SerializedName("template_availability_enabled")
    private boolean mTemplateAvailabilityEnabled;

    // Getters
    public boolean isDailyProPaymentsEnabled() {
        return true;
    }

    public boolean areAnnouncementsEnabled() {
        return mAnnouncementsEnabled;
    }

    public boolean isAppseeAnalyticsEnabled() {
        return mAppseeAnalyticsEnabled;
    }

    public boolean isCustomerNoShowModalEnabled() {
        return mCustomerNoShowModalEnabled;
    }

    public boolean isLocationScheduleServiceEnabled() {
        return mLocationScheduleServiceEnabled;
    }

    public boolean isBookingGeofenceServiceEnabled() {
        return mBookingGeofenceServiceEnabled;
    }

    public boolean isLocationServiceEnabled() {
        return mLocationScheduleServiceEnabled || mBookingGeofenceServiceEnabled;
    }

    public boolean isBoxedSuppliesEnabled() {
        return mBoxedSuppliesEnabled;
    }

    public boolean isCheckoutRatingFlowEnabled() {
        return mCheckoutRatingFlowEnabled;
    }

    public boolean shouldShowLateDispatchOptIn() {
        return mShowLateDispatchOptIn;
    }

    public boolean shouldShowWeeklyPaymentTiers() {
        return mWeeklyPaymentTiersEnabled;
    }

    public boolean shouldUseHelpCenterWebView() {
        return mHelpCenterInfo != null && mHelpCenterInfo.shouldUseHelpCenterWebView();
    }

    public int getNumberOfDaysForRequestedJobs() {
        return mNumberOfDaysForRequestedJobs;
    }

    public int getNumberOfDaysForAvailableJobs() {
        return mNumberOfDaysForAvailableJobs;
    }

    public int getNumberOfDaysForScheduledJobs() {
        return mNumberOfDaysForScheduledJobs > 0 ? mNumberOfDaysForScheduledJobs : 28;
    }

    public String getHelpCenterUrl() {
        return mHelpCenterInfo == null ? null : mHelpCenterInfo.getHelpCenterUrl();
    }

    public boolean showBookingTransactionSummary() {
        return mShowBookingTransactionSummary;
    }

    public boolean isProfilePictureUploadEnabled() {
        return mProfilePictureUploadEnabled;
    }

    public RequestDismissal getRequestDismissal() {
        return mRequestDismissal;
    }

    public boolean isSltEnabled() { return mSltEnabled; }

    public boolean isAvailableHoursEnabled() {
        return mAvailableHoursEnabled;
    }

    public boolean isProfileShareEnabled() {
        return mProfileShareEnabled;
    }

    public boolean isMoreFullTabEnabled() {
        return mMoreTabEnabled;
    }

    // Setters
    public void setProfilePictureUploadEnabled(final boolean profilePictureUploadEnabled) {
        mProfilePictureUploadEnabled = profilePictureUploadEnabled;
    }

    public boolean isScheduleTabRequestedEnabled() {
        return mScheduleTabRequestedEnabled;
    }

    public boolean isTemplateAvailabilityEnabled() {
        return mTemplateAvailabilityEnabled;
    }

    // Static classes
    public static class HelpCenterInfo {
        @SerializedName("should_use_help_center_web_view")
        private boolean mShouldUseHelpCenterWebView;

        @SerializedName("help_center_url")
        private String mHelpCenterUrl;

        public boolean shouldUseHelpCenterWebView() {
            return mShouldUseHelpCenterWebView;
        }

        public String getHelpCenterUrl() {
            return mHelpCenterUrl;
        }
    }


    // Configuration info concerning pro request dismissals
    public static class RequestDismissal {
        @SerializedName("enabled")
        private boolean mIsEnabled;

        public boolean isEnabled() {
            return mIsEnabled;
        }
    }
}
