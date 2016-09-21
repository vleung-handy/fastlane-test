package com.handy.portal.logger.handylogger.model;

import com.google.gson.annotations.SerializedName;
import com.handy.portal.bookings.model.Booking;

public abstract class RequestedJobsLog extends EventLog
{
    private static final String EVENT_CONTEXT = "requested_jobs";

    protected RequestedJobsLog(final String eventType)
    {
        super(eventType, EVENT_CONTEXT);
    }

    public static class Clicked extends JobsLog
    {
        private static final String EVENT_TYPE = "job_selected";

        public Clicked(final Booking booking)
        {
            super(EVENT_TYPE, EVENT_CONTEXT, booking);
        }
    }


    public static class ConfirmSwapShown extends JobsLog
    {
        private static final String EVENT_TYPE = "confirm_swap_shown";

        public ConfirmSwapShown(final Booking booking)
        {
            super(EVENT_TYPE, EVENT_CONTEXT, booking);
        }
    }


    public static class ConfirmSwapSubmitted extends JobsLog
    {
        private static final String EVENT_TYPE = "confirm_swap_submitted";

        public ConfirmSwapSubmitted(final Booking booking)
        {
            super(EVENT_TYPE, EVENT_CONTEXT, booking);
        }
    }


    public static class ClaimSuccess extends JobsLog
    {
        private static final String EVENT_TYPE = "claim_success";

        public ClaimSuccess(final Booking booking)
        {
            super(EVENT_TYPE, EVENT_CONTEXT, booking);
        }
    }


    public static class ClaimError extends JobsLog
    {
        private static final String EVENT_TYPE = "claim_error";

        @SerializedName("error_message")
        private String mErrorMessage;

        public ClaimError(final Booking booking, final String errorMessage)
        {
            super(EVENT_TYPE, EVENT_CONTEXT, booking);
            mErrorMessage = errorMessage;
        }
    }
}
