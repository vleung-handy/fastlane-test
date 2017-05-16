package com.handy.portal.logger.handylogger.model;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;
import com.handy.portal.bookings.model.Booking;

import java.util.Date;

public abstract class RequestedJobsLog extends EventLog {
    public static final String EVENT_CONTEXT = "requested_jobs";

    public RequestedJobsLog(final String eventType, final String eventContext) {
        super(eventType, eventContext);
    }

    public static class RequestsShown extends RequestedJobsLog {
        private static final String EVENT_TYPE = "requests_shown";

        @SerializedName("pending_requests_count")
        private int mPendingRequestsCount;

        @SerializedName("pending_referral_requests_count")
        private int mPendingReferralRequestsCount;

        @SerializedName("pending_favorite_requests_count")
        private int mPendingFavoriteRequestsCount;

        @SerializedName("date")
        private Date mDate;

        public RequestsShown(
                final String eventContext,
                final int pendingRequestsCount,
                final int pendingReferralRequestsCount,
                final int pendingFavoriteRequestsCount,
                @Nullable Date date
        ) {
            super(EVENT_TYPE, eventContext);
            mPendingRequestsCount = pendingRequestsCount;
            mPendingReferralRequestsCount = pendingReferralRequestsCount;
            mPendingFavoriteRequestsCount = pendingFavoriteRequestsCount;
            mDate = date;
        }
    }


    public static class Clicked extends JobsLog {
        private static final String EVENT_TYPE = "job_selected";

        @SerializedName("request_type")
        private String mRequestType = "";

        public Clicked(final Booking booking) {
            super(EVENT_TYPE, EVENT_CONTEXT, booking);

            if (booking.getAuxiliaryInfo() != null
                    && booking.getAuxiliaryInfo().getType() != null) {
                mRequestType = booking.getAuxiliaryInfo().getType().toString().toLowerCase();
            }
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


    public static class DismissJobShown extends JobsLog {
        private static final String EVENT_TYPE = "dismiss_job_shown";

        public DismissJobShown(final String eventContext, final Booking booking) {
            super(EVENT_TYPE, eventContext, booking);
        }
    }


    public static class DismissJobSubmitted extends JobsLog {
        private static final String EVENT_TYPE = "dismiss_job_submitted";
        @SerializedName("reason_machine_name")
        private final String mReasonMachineName;

        public DismissJobSubmitted(
                final String eventContext,
                final Booking booking,
                final String reasonMachineName
        ) {
            super(EVENT_TYPE, eventContext, booking);
            mReasonMachineName = reasonMachineName;
        }
    }


    public static class DismissJobSuccess extends JobsLog {
        private static final String EVENT_TYPE = "dismiss_job_success";

        public DismissJobSuccess(final String eventContext, final Booking booking) {
            super(EVENT_TYPE, eventContext, booking);
        }
    }


    public static class DismissJobError extends JobsLog {
        private static final String EVENT_TYPE = "dismiss_job_error";

        @SerializedName("error_message")
        private String mErrorMessage;

        public DismissJobError(
                final String eventContext,
                final Booking booking,
                final String errorMessage
        ) {
            super(EVENT_TYPE, eventContext, booking);
            mErrorMessage = errorMessage;
        }
    }
}
