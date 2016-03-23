package com.handy.portal.logger.handylogger.model;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;
import com.handy.portal.model.Address;
import com.handy.portal.model.Booking;

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


    public static abstract class AvailableJobsBookingLog extends AvailableJobsLog
    {
        @SerializedName("booking_id")
        private String mBookingId;
        @SerializedName("booking_type")
        private String mBookingType;
        @SerializedName("service_id")
        private String mServiceId;
        @SerializedName("region_id")
        private int mRegionId;
        @SerializedName("zipcode")
        private String mZipCode;
        @SerializedName("requested")
        private boolean mRequested;
        @SerializedName("date_start")
        private Date mDateStart;
        @SerializedName("frequency")
        private int mFrequency;
        @SerializedName("hours")
        private float mHours;
        @SerializedName("min_hours")
        private float mMinimumHours;
        @SerializedName("payment_to_provider")
        private int mPaymentToProvider;
        @SerializedName("hourly_rate")
        private int mHourlyRate;
        @SerializedName("bonus")
        private int mBonus;

        public AvailableJobsBookingLog(final String eventType, final Booking booking)
        {
            super(eventType);
            mBookingId = booking.getId();
            mBookingType = booking.getType().name().toLowerCase();
            mServiceId = booking.getService();
            mRegionId = booking.getRegionId();
            mZipCode = getZipCode(booking.getAddress());
            mRequested = booking.isRequested();
            mDateStart = booking.getStartDate();
            mFrequency = booking.getFrequency();
            mHours = booking.getHours();
            mMinimumHours = booking.getMinimumHours();
            if (booking.getPaymentToProvider() != null)
            {
                mPaymentToProvider = booking.getPaymentToProvider().getAmount();
            }
            if (booking.getHourlyRate() != null)
            {
                mHourlyRate = booking.getHourlyRate().getAmount();
            }
            if (booking.getBonusPaymentToProvider() != null)
            {
                mBonus = booking.getBonusPaymentToProvider().getAmount();
            }
        }
    }


    public static class Clicked extends AvailableJobsBookingLog
    {
        private static final String EVENT_TYPE = "selected";

        @SerializedName("list_number")
        private int mListNumber;

        public Clicked(final Booking booking, final int listNumber)
        {
            super(EVENT_TYPE, booking);
            mListNumber = listNumber;
        }
    }

    // Claim-related events


    public static abstract class AvailableJobsBookingClaimLog extends AvailableJobsBookingLog
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
                                            final int distanceToJobInMeters)
        {
            super(eventType, booking);
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
                              final int distanceToJobInMeters)
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
                            final int distanceToJobInMeters)
        {
            super(EVENT_TYPE, booking, source, sourceExtras, distanceToJobInMeters);
        }
    }


    public static class ClaimError extends AvailableJobsBookingClaimLog
    {
        private static final String EVENT_TYPE = "claim_error";

        public ClaimError(final Booking booking,
                          final String source,
                          @Nullable final Bundle sourceExtras,
                          final int distanceToJobInMeters)
        {
            super(EVENT_TYPE, booking, source, sourceExtras, distanceToJobInMeters);
        }
    }

    private static String getZipCode(Address address)
    {
        if (address != null)
        {
            return address.getZip();
        }
        else
        {
            return "";
        }
    }
}
