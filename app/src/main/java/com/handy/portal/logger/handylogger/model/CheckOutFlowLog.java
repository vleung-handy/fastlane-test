package com.handy.portal.logger.handylogger.model;


import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;
import com.handy.portal.bookings.model.Booking;
import com.handy.portal.library.util.LogUtils;
import com.handy.portal.library.util.MathUtils;
import com.handy.portal.model.LocationData;

public class CheckOutFlowLog extends EventLog
{

    private static final String EVENT_CONTEXT = "checkout_flow";


    public static class CheckOutBookingLog extends CheckOutFlowLog
    {
        @SerializedName("booking_id")
        private String mBookingId;
        @SerializedName("pro_latitude")
        private double mProLatitude;
        @SerializedName("pro_longitude")
        private double mProLongitude;
        @SerializedName("booking_latitude")
        private double mBookingLatitude;
        @SerializedName("booking_longitude")
        private double mBookingLongitude;
        @SerializedName("accuracy")
        private double mAccuracy;
        @SerializedName("distance_to_job")
        private double mDistance;

        public CheckOutBookingLog(final String eventType,
                                  @NonNull final Booking booking,
                                  final LocationData location)
        {
            super(eventType);
            mBookingId = booking.getId();
            mProLatitude = LogUtils.getLatitude(location);
            mProLongitude = LogUtils.getLongitude(location);
            mBookingLatitude = LogUtils.getLatitude(booking);
            mBookingLongitude = LogUtils.getLongitude(booking);
            mAccuracy = LogUtils.getAccuracy(location);
            mDistance = MathUtils.getDistance(mProLatitude, mProLatitude, mBookingLatitude,
                    mBookingLongitude);
        }
    }

    public CheckOutFlowLog(final String eventType)
    {
        super(eventType, EVENT_CONTEXT);
    }

    public static class CheckOutSubmitted extends CheckOutBookingLog
    {
        private static final String EVENT_TYPE = "manual_checkout_submitted";

        public CheckOutSubmitted(final Booking booking, final LocationData location)
        {
            super(EVENT_TYPE, booking, location);
        }
    }


    public static class CheckOutSuccess extends CheckOutBookingLog
    {
        private static final String EVENT_TYPE = "manual_checkout_success";

        public CheckOutSuccess(final Booking booking, final LocationData location)
        {
            super(EVENT_TYPE, booking, location);
        }
    }


    public static class CheckOutError extends CheckOutBookingLog
    {
        private static final String EVENT_TYPE = "manual_checkout_error";

        public CheckOutError(final Booking booking, final LocationData location)
        {
            super(EVENT_TYPE, booking, location);
        }
    }

    // Customer rating events


    public static class CustomerRatingShown extends CheckOutFlowLog
    {
        private static final String EVENT_TYPE = "customer_rating_shown";

        public CustomerRatingShown()
        {
            super(EVENT_TYPE);
        }
    }


    public static class CustomerRatingSubmitted extends CheckOutFlowLog
    {
        private static final String EVENT_TYPE = "customer_rating_submitted";

        @SerializedName("rating")
        private int mRating;

        public CustomerRatingSubmitted(int rating)
        {
            super(EVENT_TYPE);
            mRating = rating;
        }
    }

    // Post-checkout events


    public static class ProTeamJobsReturned extends CheckOutFlowLog
    {
        private static final String EVENT_TYPE = "pro_team_jobs_returned";
        @SerializedName("num_jobs")
        private int mJobsCount;

        public ProTeamJobsReturned(final int jobsCount)
        {
            super(EVENT_TYPE);
            mJobsCount = jobsCount;
        }
    }

    // Claim events


    public static class ClaimBatchSubmitted extends CheckOutFlowLog
    {
        private static final String EVENT_TYPE = "claim_batch_submitted";
        @SerializedName("num_jobs")
        private int mJobsCount;

        public ClaimBatchSubmitted(final int jobsCount)
        {
            super(EVENT_TYPE);
            mJobsCount = jobsCount;
        }
    }


    public static class ClaimBatchSuccess extends CheckOutFlowLog
    {
        private static final String EVENT_TYPE = "claim_batch_success";
        @SerializedName("num_jobs")
        private int mJobsCount;

        public ClaimBatchSuccess(final int jobsCount)
        {
            super(EVENT_TYPE);
            mJobsCount = jobsCount;
        }
    }


    public static class ClaimBatchError extends CheckOutFlowLog
    {
        private static final String EVENT_TYPE = "claim_batch_error";

        public ClaimBatchError()
        {
            super(EVENT_TYPE);
        }
    }
}
