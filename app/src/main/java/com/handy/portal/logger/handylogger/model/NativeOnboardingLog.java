package com.handy.portal.logger.handylogger.model;

import com.google.gson.annotations.SerializedName;
import com.handy.portal.bookings.model.Booking;

import java.util.ArrayList;
import java.util.Date;

/**
 * FWIW, this is where the logging docs are:
 * <p/>
 * https://handybook.atlassian.net/wiki/display/engineeringwiki/Portal+Analytics+Library#PortalAnalyticsLibrary-OnboardingJobEvents
 */
public class NativeOnboardingLog extends EventLog
{
    private static final String EVENT_CONTEXT = "onboarding";

    public NativeOnboardingLog(final String eventType)
    {
        super(eventType, EVENT_CONTEXT);
    }

    public static class StatusPageShown extends NativeOnboardingLog
    {
        private static final String EVENT_TYPE = "status_page_shown";
        @SerializedName("status")
        private String mStatus;

        public StatusPageShown(final String status)
        {
            super(EVENT_TYPE);
            mStatus = status;
        }
    }


    public static class StatusPageSubmitted extends NativeOnboardingLog
    {
        private static final String EVENT_TYPE = "status_page_submitted";
        @SerializedName("status")
        private String mStatus;

        public StatusPageSubmitted(final String status)
        {
            super(EVENT_TYPE);
            mStatus = status;
        }
    }


    public static class HelpLinkSelected extends NativeOnboardingLog
    {
        private static final String EVENT_TYPE = "help_link_selected";
        @SerializedName("help_link")
        private String mHelpLink;

        public HelpLinkSelected(final String helpLink)
        {
            super(EVENT_TYPE);
            mHelpLink = helpLink;
        }
    }


    public static class Types
    {
        public static final String JOB_SEARCH_SHOWN = "job_search_shown";
        public static final String NO_JOBS_LOADED = "no_jobs_loaded";
        public static final String PURCHASE_SUPPLIES_SHOWN = "purchase_supplies_shown";
        public static final String SUPPLIES_SUGGESTION_SHOWN = "supplies_suggestion_shown";
        public static final String PRODUCTS_LIST_SHOWN = "products_list_shown";
        public static final String PURCHASE_SUPPLIES_SELECTED = "purchase_supplies_selected";
        public static final String DECLINE_SUPPLIES_SELECTED = "decline_supplies_selected";
        public static final String DECLINE_SUPPLIES_CONFIRMED = "decline_supplies_confirmed";
        public static final String SUPPLIES_CONFIRMATION_SHOWN = "supplies_confirmation_shown";
        public static final String EDIT_ADDRESS_SHOWN = "edit_address_shown";
        public static final String SUPPLIES_CONFIRM_PURCHASE_SELECTED = "supplies_confirm_purchase_selected";
        public static final String SUPPLIES_SUGGESTION_TAPPED = "supplies_suggestion_tapped";
        public static final String CONFIRMATION_PAGE_SHOWN = "confirmation_page_shown";
    }


    public enum ServerTypes
    {
        GET_STRIPE_TOKEN,
        UPDATE_CREDIT_CARD,
        UPDATE_ADDRESS,;

        public static final String SUFFIX_SUBMITTED = "_submitted";
        public static final String SUFFIX_SUCCESS = "_success";
        public static final String SUFFIX_ERROR = "_error";

        public String submitted()
        {
            return this.toString().toLowerCase() + SUFFIX_SUBMITTED;
        }

        public String success()
        {
            return this.toString().toLowerCase() + SUFFIX_SUCCESS;
        }

        public String error()
        {
            return this.toString().toLowerCase() + SUFFIX_ERROR;
        }
    }


    public static class StartDateSelected extends NativeOnboardingLog
    {
        private static final String EVENT_TYPE = "start_date_selected";
        @SerializedName("start_date")
        private Date mDate;

        public StartDateSelected(final Date date)
        {
            super(EVENT_TYPE);
            mDate = date;
        }
    }


    public static class LocationsSelected extends NativeOnboardingLog
    {
        private static final String EVENT_TYPE = "locations_selected";
        @SerializedName("locations")
        private ArrayList<String> mZipclusterIds;

        public LocationsSelected(final ArrayList<String> zipclusterIds)
        {
            super(EVENT_TYPE);
            mZipclusterIds = zipclusterIds;
        }
    }


    public static class ScheduleJobsShown extends NativeOnboardingLog
    {
        private static final String EVENT_TYPE = "schedule_jobs_shown";
        @SerializedName("jobs_shown")
        private int mJobsShown;

        public ScheduleJobsShown(final int jobsShown)
        {
            super(EVENT_TYPE);
            mJobsShown = jobsShown;
        }
    }


    public static class ScheduleJobsSubmitted extends NativeOnboardingLog
    {
        private static final String EVENT_TYPE = "schedule_jobs_submitted";
        @SerializedName("jobs_to_claim")
        private int mJobsToClaim;

        public ScheduleJobsSubmitted(final int jobsToClaim)
        {
            super(EVENT_TYPE);
            mJobsToClaim = jobsToClaim;
        }
    }


    public static class RequestSupplies extends NativeOnboardingLog
    {
        private static final String EVENT_TYPE = "request_supplies";

        @SerializedName("requested")
        protected boolean mRequested;

        public RequestSupplies(final String suffix)
        {
            super(EVENT_TYPE + suffix);
        }

        public static class Submitted extends RequestSupplies
        {

            public Submitted(final boolean requested)
            {
                super(ServerTypes.SUFFIX_SUBMITTED);
                mRequested = requested;
            }
        }


        public static class Success extends RequestSupplies
        {
            public Success(final boolean requested)
            {
                super(ServerTypes.SUFFIX_SUCCESS);
                mRequested = requested;
            }
        }


        public static class Error extends RequestSupplies
        {
            public Error(final boolean requested)
            {
                super(ServerTypes.SUFFIX_ERROR);
                mRequested = requested;
            }
        }
    }


    public static class ConfirmationPageSubmitted extends NativeOnboardingLog
    {
        private static final String EVENT_TYPE = "confirmation_page_submitted";
        @SerializedName("claimed_jobs")
        private final int mClaimedJobs;
        @SerializedName("supplies_requested")
        private final Boolean mSuppliesRequested;

        public ConfirmationPageSubmitted(final int claimedJobs, final Boolean suppliesRequested)
        {
            super(EVENT_TYPE);
            mClaimedJobs = claimedJobs;
            mSuppliesRequested = suppliesRequested;
        }
    }


    /**
     * User submits jobs to claim through the native onboarding funnel
     */
    public static class ClaimBatchSubmitted extends NativeOnboardingLog
    {
        private static final String EVENT_TYPE = "claim_batch_submitted";
        @SerializedName("booking_ids")
        private String[] mBookingsIds;

        public ClaimBatchSubmitted(final ArrayList<Booking> bookings)
        {
            super(EVENT_TYPE);
            mBookingsIds = extractBookingIds(bookings);
        }
    }


    /**
     * The onboarding jobs claim response was not an error response
     * <p/>
     * This does NOT mean that the bookings were actually claimed–it indicates that there was not
     * an error response from the server
     */
    public static class ClaimBatchSuccess extends NativeOnboardingLog
    {
        private static final String EVENT_TYPE = "claim_batch_success";
        @SerializedName("booking_ids")
        private String[] mBookingsIds;

        public ClaimBatchSuccess(final ArrayList<Booking> bookings)
        {
            super(EVENT_TYPE);
            mBookingsIds = extractBookingIds(bookings);
        }
    }


    /**
     * The onboarding jobs claim response was an error response
     */
    public static class ClaimBatchError extends NativeOnboardingLog
    {
        private static final String EVENT_TYPE = "claim_batch_error";

        @SerializedName("booking_ids")
        private String[] mBookingsIds;
        @SerializedName("error_message")
        private String mErrorMessage;

        public ClaimBatchError(final ArrayList<Booking> bookings, final String errorMessage)
        {
            super(EVENT_TYPE);
            mErrorMessage = errorMessage;
            mBookingsIds = extractBookingIds(bookings);
        }
    }

    private static String[] extractBookingIds(final ArrayList<Booking> bookings)
    {
        final String[] bookingsIds = new String[bookings.size()];
        for (int i = 0; i < bookings.size(); i++)
        {
            bookingsIds[i] = bookings.get(i).getId();
        }
        return bookingsIds;
    }


    /**
     * The specific job was successfully claimed through the onboarding funnel
     */
    public static class ClaimSuccess extends JobsLog
    {
        private static final String EVENT_TYPE = "claim_success";

        public ClaimSuccess(final Booking booking)
        {
            super(EVENT_TYPE, EVENT_CONTEXT, booking);
        }
    }


    /**
     * The onboarding jobs claim response was an error response
     */
    public static class ClaimError extends JobsLog
    {
        private static final String EVENT_TYPE = "claim_error";

        public ClaimError(final Booking booking)
        {
            super(EVENT_TYPE, EVENT_CONTEXT, booking);
        }
    }


    /**
     * Jumio ID verification
     */
    public static class NativeIDVerificationStartedLog extends NativeOnboardingLog
    {
        private static final String EVENT_TYPE = "native_id_verification_started";

        public NativeIDVerificationStartedLog()
        {
            super(EVENT_TYPE);
        }
    }


    /**
     * Jumio ID verification in app flow completed
     */
    public static class NativeIDVerificationCompletedLog extends NativeOnboardingLog
    {
        private static final String EVENT_TYPE = "native_id_verification_completed";

        public NativeIDVerificationCompletedLog()
        {
            super(EVENT_TYPE);
        }
    }


    /**
     * Jumio ID verification in app flow failed
     */
    public static class NativeIDVerificationFailedLog extends NativeOnboardingLog
    {
        private static final String EVENT_TYPE = "native_id_verification_failed";

        public NativeIDVerificationFailedLog()
        {
            super(EVENT_TYPE);
        }
    }


    /**
     * Jumio ID verification in app flow failed
     */
    public static class NativeIDVerificationCancelledLog extends NativeOnboardingLog
    {
        private static final String EVENT_TYPE = "native_id_verification_cancelled";

        public NativeIDVerificationCancelledLog()
        {
            super(EVENT_TYPE);
        }
    }


    /**
     * Jumio ID verification web flow
     */
    public static class WebIDVerificationFlowStarted extends NativeOnboardingLog
    {
        private static final String EVENT_TYPE = "web_id_verification_flow_started";

        public WebIDVerificationFlowStarted()
        {
            super(EVENT_TYPE);
        }
    }


    /**
     * Camera permission granted for Jumio ID verification
     */
    public static class CameraPermissionGrantedLog extends NativeOnboardingLog
    {
        private static final String EVENT_TYPE = "camera_permission_granted";

        public CameraPermissionGrantedLog()
        {
            super(EVENT_TYPE);
        }
    }


    /**
     * Camera permission denied for Jumio ID verification
     */
    public static class CameraPermissionDeniedLog extends NativeOnboardingLog
    {
        private static final String EVENT_TYPE = "camera_permission_denied";

        public CameraPermissionDeniedLog()
        {
            super(EVENT_TYPE);
        }
    }


    /**
     * Camera settings opened for Jumio ID verification
     */
    public static class CameraSettingsOpenedLog extends NativeOnboardingLog
    {
        private static final String EVENT_TYPE = "camera_settings_opened";

        public CameraSettingsOpenedLog()
        {
            super(EVENT_TYPE);
        }
    }
}
