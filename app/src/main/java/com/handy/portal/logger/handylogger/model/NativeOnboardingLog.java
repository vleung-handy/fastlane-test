package com.handy.portal.logger.handylogger.model;

import com.google.gson.annotations.SerializedName;
import com.handy.portal.bookings.model.Booking;

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

    public static class Types
    {
        public static final String PURCHASE_SUPPLIES_SHOWN = "purchase_supplies_shown";
        public static final String PRODUCTS_LIST_SHOWN = "products_list_shown";
        public static final String PURCHASE_SUPPLIES_SELECTED = "purchase_supplies_selected";
        public static final String DECLINE_SUPPLIES_SELECTED = "decline_supplies_selected";
        public static final String DECLINE_SUPPLIES_CONFIRMED = "decline_supplies_confirmed";
        public static final String SUPPLIES_CONFIRMATION_SHOWN = "supplies_confirmation_shown";
        public static final String EDIT_ADDRESS_SHOWN = "edit_address_shown";
        public static final String SUPPLIES_CONFIRM_PURCHASE_SELECTED = "supplies_confirm_purchase_selected";
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

    /**
     * User submits jobs to claim through the native onboarding funnel
     */
    public static class ClaimBatchSubmitted extends NativeOnboardingLog
    {
        private static final String EVENT_TYPE = "claim_batch_submitted";

        public ClaimBatchSubmitted()
        {
            super(EVENT_TYPE);
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

        public ClaimBatchSuccess()
        {
            super(EVENT_TYPE);
        }
    }


    /**
     * The onboarding jobs claim response was an error response
     */
    public static class ClaimBatchError extends NativeOnboardingLog
    {
        private static final String EVENT_TYPE = "claim_batch_error";

        @SerializedName("error_message")
        private String mErrorMessage;

        public ClaimBatchError(String errorMessage)
        {
            super(EVENT_TYPE);
            mErrorMessage = errorMessage;
        }
    }


    /**
     * No bookings are returned from the server to display to the user, essentially skipping the flow
     */
    public static class NoJobsLoaded extends NativeOnboardingLog
    {
        private static final String EVENT_TYPE = "no_jobs_loaded";

        public NoJobsLoaded()
        {
            super(EVENT_TYPE);
        }
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

        @SerializedName("error_message")
        private String mErrorMessage;

        public ClaimError(final Booking booking, String errorMessage)
        {
            super(EVENT_TYPE, EVENT_CONTEXT, booking);
            mErrorMessage = errorMessage;
        }
    }
}
