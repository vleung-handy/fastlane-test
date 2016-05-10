package com.handy.portal.logger.handylogger.model;

import com.handy.portal.bookings.model.Booking;

public abstract class CompletedJobsLog extends EventLog
{
    private static final String EVENT_CONTEXT = "completed_jobs";

    protected CompletedJobsLog(final String eventType)
    {
        super(eventType, EVENT_CONTEXT);
    }

    public static class HelpClicked extends JobsLog
    {
        private static final String EVENT_TYPE = "payment_help_center_link_selected";

        public HelpClicked(final Booking booking)
        {
            super(EVENT_TYPE, EVENT_CONTEXT, booking);
        }
    }
}
