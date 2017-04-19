package com.handy.portal.logger.handylogger.model;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;
import com.handy.portal.bookings.model.Booking;

public abstract class RequestedJobsLog extends EventLog {
    public static final String EVENT_CONTEXT = "requested_jobs";

    private RequestedJobsLog(final String eventType) {
        super(eventType, EVENT_CONTEXT);
    }

    public static class RequestsShown extends RequestedJobsLog {
        private static final String EVENT_TYPE = "requests_shown";

        @SerializedName("pending_requests_count")
        private int mPendingRequestsCount;

        public RequestsShown(final int pendingRequestsCount) {
            super(EVENT_TYPE);
            mPendingRequestsCount = pendingRequestsCount;
        }
    }


    public static class Clicked extends JobsLog {
        private static final String EVENT_TYPE = "job_selected";

        public Clicked(final Booking booking) {
            super(EVENT_TYPE, EVENT_CONTEXT, booking);
        }
    }


    public static class ConfirmSwapShown extends JobsLog {
        private static final String EVENT_TYPE = "confirm_swap_shown";

        public ConfirmSwapShown(final Booking booking) {
            super(EVENT_TYPE, EVENT_CONTEXT, booking);
        }
    }


    public static class ConfirmSwapSubmitted extends JobsLog {
        private static final String EVENT_TYPE = "confirm_swap_submitted";

        public ConfirmSwapSubmitted(final Booking booking) {
            super(EVENT_TYPE, EVENT_CONTEXT, booking);
        }
    }


    public static class ClaimSubmitted extends JobsLog {
        private static final String EVENT_TYPE = "claim_submitted";

        public ClaimSubmitted(final Booking booking) {
            super(EVENT_TYPE, EVENT_CONTEXT, booking);
        }
    }


    public static class ClaimSuccess extends JobsLog {
        private static final String EVENT_TYPE = "claim_success";

        @SerializedName("request_type")
        private String mRequestType = "";

        public ClaimSuccess(@NonNull final Booking booking) {
            super(EVENT_TYPE, EVENT_CONTEXT, booking);

            if (booking.getAuxiliaryInfo() != null
                    && booking.getAuxiliaryInfo().getType() != null) {
                mRequestType = booking.getAuxiliaryInfo().getType().toString().toLowerCase();
            }
        }
    }

    public static class ClaimError extends JobsLog {
        private static final String EVENT_TYPE = "claim_error";

        @SerializedName("error_message")
        private String mErrorMessage;

        public ClaimError(final Booking booking, final String errorMessage) {
            super(EVENT_TYPE, EVENT_CONTEXT, booking);
            mErrorMessage = errorMessage;
        }
    }


    public static class DismissJobShown extends JobsLog {
        private static final String EVENT_TYPE = "dismiss_job_shown";

        public DismissJobShown(final Booking booking) {
            super(EVENT_TYPE, EVENT_CONTEXT, booking);
        }
    }


    public static class DismissJobSubmitted extends JobsLog {
        private static final String EVENT_TYPE = "dismiss_job_submitted";
        @SerializedName("reason_machine_name")
        private final String mReasonMachineName;

        public DismissJobSubmitted(final Booking booking,
                                   final String reasonMachineName) {
            super(EVENT_TYPE, EVENT_CONTEXT, booking);
            mReasonMachineName = reasonMachineName;
        }
    }


    public static class DismissJobSuccess extends JobsLog {
        private static final String EVENT_TYPE = "dismiss_job_success";

        public DismissJobSuccess(final Booking booking) {
            super(EVENT_TYPE, EVENT_CONTEXT, booking);
        }
    }


    public static class DismissJobError extends JobsLog {
        private static final String EVENT_TYPE = "dismiss_job_error";

        @SerializedName("error_message")
        private String mErrorMessage;

        public DismissJobError(final Booking booking, final String errorMessage) {
            super(EVENT_TYPE, EVENT_CONTEXT, booking);
            mErrorMessage = errorMessage;
        }
    }
}
