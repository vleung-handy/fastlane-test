package com.handy.portal.logger.handylogger.model;

import com.google.gson.annotations.SerializedName;

public class NearbyJobsLog extends EventLog
{
    private static final String EVENT_CONTEXT = "nearby_jobs_post_checkout";

    protected NearbyJobsLog(final String eventType)
    {
        super(eventType, EVENT_CONTEXT);
    }


    public static class Shown extends NearbyJobsLog
    {
        private static final String EVENT_TYPE = "shown";

        @SerializedName("number_of_jobs_shown")
        private int mNumberOfJobs;

        public Shown(final int numberOfJobs)
        {
            super(EVENT_TYPE);
            mNumberOfJobs = numberOfJobs;
        }
    }


    public static class PinSelected extends NearbyJobsLog
    {
        private static final String EVENT_TYPE = "pin_selected";

        public PinSelected()
        {
            super(EVENT_TYPE);
        }
    }


    public static class ClaimJobSelected extends NearbyJobsLog
    {
        private static final String EVENT_TYPE = "claim_job_selected";

        @SerializedName("booking_id")
        private String mBookingId;
        @SerializedName("distance_to_job")
        private double mDistanceInKilometer;
        @SerializedName("payment_to_provider")
        private float mPaymentAmount;

        public ClaimJobSelected(
                final String bookingId, final double distanceInKilometer, final float paymentAmount)
        {
            super(EVENT_TYPE);
            mBookingId = bookingId;
            mDistanceInKilometer = distanceInKilometer;
            mPaymentAmount = paymentAmount;
        }
    }


    public static class ClaimJobSuccess extends NearbyJobsLog
    {
        private static final String EVENT_TYPE = "claim_job_success";

        @SerializedName("booking_id")
        private String mBookingId;
        @SerializedName("distance_to_job")
        private double mDistanceInKilometer;
        @SerializedName("payment_to_provider")
        private float mPaymentAmount;

        public ClaimJobSuccess(
                final String bookingId, final double distanceInKilometer, final float paymentAmount)
        {
            super(EVENT_TYPE);
            mBookingId = bookingId;
            mDistanceInKilometer = distanceInKilometer;
            mPaymentAmount = paymentAmount;
        }
    }

}
