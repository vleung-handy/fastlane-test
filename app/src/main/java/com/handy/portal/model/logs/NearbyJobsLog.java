package com.handy.portal.model.logs;

import com.google.gson.annotations.SerializedName;

public class NearbyJobsLog extends EventLog
{
    private static final String EVENT_CONTEXT = "nearby_jobs_post_checkout";

    protected NearbyJobsLog(String providerId, String versionTrack, String eventType)
    {
        super(providerId, versionTrack, eventType, EVENT_CONTEXT);
    }


    public static class Shown extends NearbyJobsLog
    {
        private static final String EVENT_TYPE = "shown";

        @SerializedName("number_of_jobs_shown")
        private int mNumberOfJobs;

        public Shown(String providerId, String versionTrack, int numberOfJobs)
        {
            super(providerId, versionTrack, EVENT_TYPE);
            mNumberOfJobs = numberOfJobs;
        }
    }


    public static class PinSelected extends NearbyJobsLog
    {
        private static final String EVENT_TYPE = "pin_selected";

        public PinSelected(String providerId, String versionTrack)
        {
            super(providerId, versionTrack, EVENT_TYPE);
        }
    }


    public static class ClaimJobSelected extends NearbyJobsLog
    {
        private static final String EVENT_TYPE = "claim_job_selected";

        @SerializedName("booking_id")
        private String mBookingId;
        @SerializedName("distance_to_job")
        private double mDistance;
        @SerializedName("payment_to_provider")
        private int mPaymentAmount;

        public ClaimJobSelected(String providerId, String versionTrack, String bookingId,
                                final double distance, final int paymentAmount)
        {
            super(providerId, versionTrack, EVENT_TYPE);
            mBookingId = bookingId;
            mDistance = distance;
            mPaymentAmount = paymentAmount;
        }
    }


    public static class ClaimJobSuccess extends NearbyJobsLog
    {
        private static final String EVENT_TYPE = "claim_job_success";

        @SerializedName("booking_id")
        private String mBookingId;
        @SerializedName("distance_to_job")
        private double mDistance;
        @SerializedName("payment_to_provider")
        private int mPaymentAmount;

        public ClaimJobSuccess(String providerId, String versionTrack, String bookingId,
                               final double distance, final int paymentAmount)
        {
            super(providerId, versionTrack, EVENT_TYPE);
            mBookingId = bookingId;
            mDistance = distance;
            mPaymentAmount = paymentAmount;
        }
    }

}
