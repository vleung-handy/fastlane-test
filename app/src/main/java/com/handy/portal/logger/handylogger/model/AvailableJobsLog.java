package com.handy.portal.logger.handylogger.model;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;
import com.handy.portal.bookings.model.Booking;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public abstract class AvailableJobsLog extends EventLog
{
    private static final String EVENT_CONTEXT = "available_jobs";

    protected AvailableJobsLog(final String eventType)
    {
        super(eventType, EVENT_CONTEXT);
    }

    public static class DateClicked extends AvailableJobsLog
    {
        private static final String EVENT_TYPE = "date_selected";

        @SerializedName("date")
        private Date mDate;
        @SerializedName("job_count")
        private int mJobCount;

        public DateClicked(final Date date, final int jobCount)
        {
            super(EVENT_TYPE);
            mDate = date;
            mJobCount = jobCount;
        }
    }


    public static class UnavailableJobNoticeShown extends AvailableJobsLog
    {
        private static final String EVENT_TYPE = "unavailable_job_notice_shown";

        @SerializedName("extras")
        private Map<String, Object> mExtras;

        public UnavailableJobNoticeShown(@Nullable final Bundle extras)
        {
            super(EVENT_TYPE);
            if (extras != null)
            {
                mExtras = new HashMap<>(extras.size());
                for (final String key : extras.keySet())
                {
                    mExtras.put(key, extras.get(key));
                }
            }
        }
    }

    // Booking-specific events


    public static class Clicked extends JobsLog
    {
        private static final String EVENT_TYPE = "job_selected";

        @SerializedName("list_index")
        private int mListIndex;

        public Clicked(final Booking booking, final int listIndex)
        {
            super(EVENT_TYPE, EVENT_CONTEXT, booking);
            mListIndex = listIndex;
        }
    }

    // Job claim events

    public static class ConfirmClaimShown extends AvailableJobsLog
    {
        private static final String EVENT_TYPE = "confirm_claim_shown";

        public ConfirmClaimShown()
        {
            super(EVENT_TYPE);
        }
    }

    public static class ConfirmClaimDetailsShown extends AvailableJobsLog
    {
        private static final String EVENT_TYPE = "confirm_claim_details_shown";

        public ConfirmClaimDetailsShown()
        {
            super(EVENT_TYPE);
        }
    }

    public static class ConfirmClaimConfirmed extends AvailableJobsLog
    {
        private static final String EVENT_TYPE = "confirm_claim_confirmed";

        public ConfirmClaimConfirmed()
        {
            super(EVENT_TYPE);
        }
    }

    public static class ConfirmSwitchSubmitted extends AvailableJobsLog
    {
        private static final String EVENT_TYPE = "confirm_switch_submitted";

        public ConfirmSwitchSubmitted()
        {
            super(EVENT_TYPE);
        }
    }

    public static abstract class AvailableJobsBookingClaimLog extends JobsLog
    {
        @SerializedName("claim_source")
        private String mSource;
        @SerializedName("claim_source_extras")
        private Map<String, Object> mSourceExtras;
        @SerializedName("distance_to_job")
        private double mDistanceToJobInMeters;

        public AvailableJobsBookingClaimLog(final String eventType,
                                            final Booking booking,
                                            final String source,
                                            @Nullable final Bundle sourceExtras,
                                            final double distanceToJobInMeters)
        {
            super(eventType, EVENT_CONTEXT, booking);
            mSource = source;
            if (sourceExtras != null)
            {
                mSourceExtras = new HashMap<>(sourceExtras.size());
                for (final String key : sourceExtras.keySet())
                {
                    mSourceExtras.put(key, sourceExtras.get(key));
                }
            }
            mDistanceToJobInMeters = distanceToJobInMeters;
        }
    }


    public static class ClaimSubmitted extends AvailableJobsBookingClaimLog
    {
        private static final String EVENT_TYPE = "claim_submitted";

        public ClaimSubmitted(final Booking booking,
                              final String source,
                              @Nullable final Bundle sourceExtras,
                              final double distanceToJobInMeters)
        {
            super(EVENT_TYPE, booking, source, sourceExtras, distanceToJobInMeters);
        }
    }


    public static class ClaimSuccess extends AvailableJobsBookingClaimLog
    {
        private static final String EVENT_TYPE = "claim_success";

        public ClaimSuccess(final Booking booking,
                            final String source,
                            @Nullable final Bundle sourceExtras,
                            final double distanceToJobInMeters)
        {
            super(EVENT_TYPE, booking, source, sourceExtras, distanceToJobInMeters);
        }
    }


    public static class ClaimError extends AvailableJobsBookingClaimLog
    {
        private static final String EVENT_TYPE = "claim_error";

        @SerializedName("error_message")
        private String mErrorMessage;

        public ClaimError(final Booking booking,
                          final String source,
                          @Nullable final Bundle sourceExtras,
                          final double distanceToJobInMeters,
                          final String errorMessage)
        {
            super(EVENT_TYPE, booking, source, sourceExtras, distanceToJobInMeters);
            mErrorMessage = errorMessage;
        }
    }
}
