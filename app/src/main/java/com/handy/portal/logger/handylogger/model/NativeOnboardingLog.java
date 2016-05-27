package com.handy.portal.logger.handylogger.model;

import com.google.gson.annotations.SerializedName;
import com.handy.portal.bookings.model.Booking;

import java.util.ArrayList;

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


    /**
     * User submits jobs to claim through the native onboarding funnel
     */
    public static class ClaimBatchSubmitted extends NativeOnboardingLog
    {
        private static final String EVENT_TYPE = "claim_batch_submitted";

        @SerializedName("booking_ids")
        private ArrayList mBookingsIds;

        public ClaimBatchSubmitted(final ArrayList<String> bookingIds)
        {
            super(EVENT_TYPE);
            mBookingsIds = bookingIds;
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
        private ArrayList mBookingsIds;

        public ClaimBatchSuccess(final ArrayList<String> bookingIds)
        {
            super(EVENT_TYPE);
            mBookingsIds = bookingIds;
        }
    }


    /**
     * The onboarding jobs claim response was an error response
     */
    public static class ClaimBatchError extends NativeOnboardingLog
    {
        private static final String EVENT_TYPE = "claim_batch_error";

        @SerializedName("booking_ids")
        private ArrayList mBookingsIds;

        @SerializedName("error_message")
        private String mErrorMessage;

        public ClaimBatchError(final ArrayList<String> bookingIds, String errorMessage)
        {
            super(EVENT_TYPE);
            mBookingsIds = bookingIds;
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
