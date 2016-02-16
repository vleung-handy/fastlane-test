package com.handy.portal.model.logs;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class AvailableJobsLog extends EventLog
{
    private static final String EVENT_CONTEXT = "available_jobs";

    protected AvailableJobsLog(final String eventType)
    {
        super(eventType, EVENT_CONTEXT);
    }

    public static class DateClicked extends AvailableJobsLog
    {
        private static final String EVENT_TYPE = "date_scroller_date_selected";

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

    public static class Clicked extends AvailableJobsLog
    {
        private static final String EVENT_TYPE = "selected";

        @SerializedName("booking_id")
        private String mBookingId;
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
        @SerializedName("list_number")
        private int mListNumber;

        public Clicked(
                final String bookingId, final String serviceId, final int regionId,
                final String zipCode, final boolean requested, final Date dateStart,
                final int listNumber)
        {
            super(EVENT_TYPE);
            mBookingId = bookingId;
            mServiceId = serviceId;
            mRegionId = regionId;
            mZipCode = zipCode;
            mRequested = requested;
            mDateStart = dateStart;
            mListNumber = listNumber;
        }
    }

    public static class ClaimSuccess extends AvailableJobsLog
    {
        private static final String EVENT_TYPE = "claim_success";

        @SerializedName("booking_id")
        private String mBookingId;
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
        @SerializedName("claim_source")
        private String mSource;


        public ClaimSuccess(
                final String bookingId, final String serviceId, final int regionId,
                final String zipCode, final boolean requested, final Date dateStart,
                final int frequency, final String source)
        {
            super(EVENT_TYPE);
            mBookingId = bookingId;
            mServiceId = serviceId;
            mRegionId = regionId;
            mZipCode = zipCode;
            mRequested = requested;
            mDateStart = dateStart;
            mFrequency = frequency;
            mSource = source;
        }
    }

    public static class ClaimError extends AvailableJobsLog
    {
        private static final String EVENT_TYPE = "claim_error";

        @SerializedName("booking_id")
        private String mBookingId;
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
        @SerializedName("claim_source")
        private String mSource;


        public ClaimError(
                final String bookingId, final String serviceId, final int regionId,
                final String zipCode, final boolean requested, final Date dateStart,
                final int frequency, final String source)
        {
            super(EVENT_TYPE);
            mBookingId = bookingId;
            mServiceId = serviceId;
            mRegionId = regionId;
            mZipCode = zipCode;
            mRequested = requested;
            mDateStart = dateStart;
            mFrequency = frequency;
            mSource = source;
        }
    }
}
