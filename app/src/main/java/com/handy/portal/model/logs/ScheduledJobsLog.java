package com.handy.portal.model.logs;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class ScheduledJobsLog extends EventLog
{
    private static final String EVENT_CONTEXT = "scheduled_jobs";

    public ScheduledJobsLog(
            String providerId, String versionTrack, String eventType)
    {
        super(providerId, versionTrack, eventType, EVENT_CONTEXT);
    }

    public static class DateClicked extends ScheduledJobsLog
    {
        private static final String EVENT_TYPE = "date_scroller_date_selected";

        @SerializedName("date")
        private Date mDate;
        @SerializedName("job_count")
        private int mJobCount;

        public DateClicked(
                String providerId, String versionTrack, Date date, int jobCount)
        {
            super(providerId, versionTrack, EVENT_TYPE);
            mDate = date;
            mJobCount = jobCount;
        }
    }

    public static class Clicked extends ScheduledJobsLog
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
                String providerId, String versionTrack, String bookingId, String serviceId,
                int regionId, String zipCode, boolean requested, Date dateStart, int listNumber)
        {
            super(providerId, versionTrack, EVENT_TYPE);
            mBookingId = bookingId;
            mServiceId = serviceId;
            mRegionId = regionId;
            mZipCode = zipCode;
            mRequested = requested;
            mDateStart = dateStart;
            mListNumber = listNumber;
        }
    }

    public static class RemoveJobClicked extends ScheduledJobsLog
    {
        private static final String EVENT_TYPE = "remove_confirmation_shown";

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
        @SerializedName("warning")
        private String mWarning;


        public RemoveJobClicked(
                String providerId, String versionTrack, String bookingId, String serviceId,
                int regionId, String zipCode, boolean requested, Date dateStart, String warning)
        {
            super(providerId, versionTrack, EVENT_TYPE);

            mBookingId = bookingId;
            mServiceId = serviceId;
            mRegionId = regionId;
            mZipCode = zipCode;
            mRequested = requested;
            mDateStart = dateStart;
            mWarning = warning;
        }
    }

    public static class RemoveJobConfirmed extends ScheduledJobsLog
    {
        private static final String EVENT_TYPE = "remove_confirmation_accepted";

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
        @SerializedName("warning")
        private String mWarning;


        public RemoveJobConfirmed(
                String providerId, String versionTrack, String bookingId, String serviceId,
                int regionId, String zipCode, boolean requested, Date dateStart, String warning)
        {
            super(providerId, versionTrack, EVENT_TYPE);
            mBookingId = bookingId;
            mServiceId = serviceId;
            mRegionId = regionId;
            mZipCode = zipCode;
            mRequested = requested;
            mDateStart = dateStart;
            mWarning = warning;
        }
    }
}
