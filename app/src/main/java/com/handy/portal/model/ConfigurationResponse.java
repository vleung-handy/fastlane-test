package com.handy.portal.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

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

    @SerializedName("customer_no_show_modal_enabled")
    private boolean mCustomerNoShowModalEnabled;

    @SerializedName("number_of_days_for_requested_jobs")
    private int mNumberOfDaysForRequestedJobs;

    @SerializedName("profile_picture_enabled")
    private boolean mProfilePictureEnabled;

    @SerializedName("profile_picture_upload_enabled")
    private boolean mProfilePictureUploadEnabled;

    @SerializedName("request_dismissal")
    private RequestDismissal mRequestDismissal;

    @SerializedName("appsee_analytics_enabled")
    private boolean mAppseeAnalyticsEnabled;

    @SerializedName("clients_chat_enabled")
    private boolean mClientsChatEnabled;

    @SerializedName("login_slt_enabled")
    private boolean mSltEnabled;


    public boolean isAppseeAnalyticsEnabled()
    {
        return mAppseeAnalyticsEnabled;
    }

    public boolean isCustomerNoShowModalEnabled()
    {
        return mCustomerNoShowModalEnabled;
    }

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

    public boolean isLocationServiceEnabled()
    {
        return mLocationScheduleServiceEnabled || mBookingGeofenceServiceEnabled;
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

    public int getNumberOfDaysForRequestedJobs()
    {
        return mNumberOfDaysForRequestedJobs;
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

    public boolean isProfilePictureEnabled()
    {
        return mProfilePictureEnabled;
    }

    public boolean isProfilePictureUploadEnabled()
    {
        return mProfilePictureUploadEnabled;
    }

    public RequestDismissal getRequestDismissal()
    {
        return mRequestDismissal;
    }

    public boolean isClientsChatEnabled()
    {
        return false;
        // FIXME: Uncomment next line once clients chat is ready
        // return mClientsChatEnabled;
    }

    public boolean isSltEnabled() { return mSltEnabled; }

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


    // Configuration info concerning pro request dismissals
    public static class RequestDismissal
    {
        @SerializedName("enabled")
        private boolean mIsEnabled;

        @SerializedName("reasons")
        private ArrayList<Reason> mReasons;

        public boolean isEnabled()
        {
            return mIsEnabled;
        }

        public ArrayList<Reason> getReasons()
        {
            return mReasons;
        }

        public static class Reason implements Serializable
        {
            public static final String MACHINE_NAME_OTHER = "other";

            @SerializedName("machine_name")
            private String mMachineName;
            @SerializedName("display_name")
            private String mDisplayName;

            public String getMachineName()
            {
                return mMachineName;
            }

            public String getDisplayName()
            {
                return mDisplayName;
            }
        }
    }
}
