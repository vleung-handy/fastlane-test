package com.handy.portal.logger.handylogger.model;


import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;
import com.handy.portal.bookings.model.Booking;
import com.handy.portal.library.util.LogUtils;
import com.handy.portal.library.util.MathUtils;
import com.handy.portal.model.LocationData;

import java.util.List;

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
        @SerializedName("booking_ids")
        private String[] mBookingsIds;

        public ProTeamJobsReturned(@NonNull final List<Booking> bookings)
        {
            super(EVENT_TYPE);
            mBookingsIds = extractBookingIds(bookings);
            mJobsCount = mBookingsIds.length;
        }
    }

    // Claim events


    public static class ClaimBatchSubmitted extends CheckOutFlowLog
    {
        private static final String EVENT_TYPE = "claim_batch_submitted";
        @SerializedName("num_jobs")
        private int mJobsCount;
        @SerializedName("booking_ids")
        private String[] mBookingsIds;

        public ClaimBatchSubmitted(@NonNull final List<Booking> bookings)
        {
            super(EVENT_TYPE);
            mBookingsIds = extractBookingIds(bookings);
            mJobsCount = mBookingsIds.length;
        }
    }


    public static class ClaimBatchSuccess extends CheckOutFlowLog
    {
        private static final String EVENT_TYPE = "claim_batch_success";
        @SerializedName("booking_ids")
        private String[] mBookingsIds;
        @SerializedName("num_jobs")
        private int mJobsCount;

        public ClaimBatchSuccess(@NonNull final List<Booking> bookings)
        {
            super(EVENT_TYPE);
            mBookingsIds = extractBookingIds(bookings);
            mJobsCount = mBookingsIds.length;
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


    public static class ClaimBatchError extends CheckOutFlowLog
    {
        private static final String EVENT_TYPE = "claim_batch_error";

        public ClaimBatchError()
        {
            super(EVENT_TYPE);
        }
    }


    private static String[] extractBookingIds(final List<Booking> bookings)
    {
        final String[] bookingsIds = new String[bookings.size()];
        for (int i = 0; i < bookings.size(); i++)
        {
            bookingsIds[i] = bookings.get(i).getId();
        }
        return bookingsIds;
    }
}
