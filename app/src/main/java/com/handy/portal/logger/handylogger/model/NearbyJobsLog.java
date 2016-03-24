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

        @SerializedName("booking_id")
        private String mBookingId;

        public PinSelected(final String bookingId)
        {
            super(EVENT_TYPE);
            mBookingId = bookingId;
        }
    }

}
