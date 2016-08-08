package com.handy.portal.logger.handylogger.model;

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
}
