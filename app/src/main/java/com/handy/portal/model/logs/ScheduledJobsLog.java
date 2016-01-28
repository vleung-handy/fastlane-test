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


    public static class RemoveJobError extends ScheduledJobsLog
    {
        private static final String EVENT_TYPE = "remove_job_error";

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


        public RemoveJobError(
                String providerId, String versionTrack, String bookingId, String serviceId,
                int regionId, String zipCode, boolean requested, Date dateStart)
        {
            super(providerId, versionTrack, EVENT_TYPE);
            mBookingId = bookingId;
            mServiceId = serviceId;
            mRegionId = regionId;
            mZipCode = zipCode;
            mRequested = requested;
            mDateStart = dateStart;
        }
    }


    public static class CustomerRatingShown extends ScheduledJobsLog
    {
        private static final String EVENT_TYPE = "customer_rating_shown";

        public CustomerRatingShown(String providerId, String versionTrack)
        {
            super(providerId, versionTrack, EVENT_TYPE);
        }
    }


    public static class CustomerRatingSubmitted extends ScheduledJobsLog
    {
        private static final String EVENT_TYPE = "customer_rating_submitted";

        @SerializedName("rating")
        private int mRating;

        public CustomerRatingSubmitted(String providerId, String versionTrack, int rating)
        {
            super(providerId, versionTrack, EVENT_TYPE);
            mRating = rating;
        }
    }


    public static class BookingInstructionsSeen extends ScheduledJobsLog
    {
        private static final String EVENT_TYPE = "job_instructions_viewed";

        @SerializedName("booking_id")
        private String mBookingId;

        public BookingInstructionsSeen(String providerId, String versionTrack, String bookingId)
        {
            super(providerId, versionTrack, EVENT_TYPE);
            mBookingId = bookingId;
        }
    }


    public static class SupportSelected extends ScheduledJobsLog
    {
        private static final String EVENT_TYPE = "booking_support_selected";

        @SerializedName("booking_id")
        private String mBookingId;

        public SupportSelected(String providerId, String versionTrack, String bookingId)
        {
            super(providerId, versionTrack, EVENT_TYPE);
            mBookingId = bookingId;
        }
    }


    public static class HelpItemSelected extends ScheduledJobsLog
    {
        private static final String EVENT_TYPE = "help_item_selected";

        @SerializedName("booking_id")
        private String mBookingId;
        @SerializedName("help_item_label")
        private String mHelpItemLabel;


        public HelpItemSelected(String providerId, String versionTrack, String bookingId, String helpItemLabel)
        {
            super(providerId, versionTrack, EVENT_TYPE);
            mBookingId = bookingId;
            mHelpItemLabel = helpItemLabel;
        }
    }


    public static class RemoveConfirmationShown extends ScheduledJobsLog
    {
        public static final String REASON_FLOW = "reason_flow";
        public static final String POPUP = "popup";

        private static final String EVENT_TYPE = "remove_confirmation_shown";

        @SerializedName("booking_id")
        private String mBookingId;
        @SerializedName("removal_type")
        private String mRemovalType;


        public RemoveConfirmationShown(String providerId, String versionTrack, String bookingId, String removalType)
        {
            super(providerId, versionTrack, EVENT_TYPE);
            mBookingId = bookingId;
            mRemovalType = removalType;
        }
    }


    public static class RemoveConfirmationAccepted extends ScheduledJobsLog
    {
        private static final String EVENT_TYPE = "remove_confirmation_accepted";

        @SerializedName("booking_id")
        private String mBookingId;
        @SerializedName("reason")
        private String mReason;


        public RemoveConfirmationAccepted(String providerId, String versionTrack, String bookingId, String reason)
        {
            super(providerId, versionTrack, EVENT_TYPE);
            mBookingId = bookingId;
            mReason = reason;
        }
    }


    public static class FindJobsSelected extends ScheduledJobsLog
    {
        private static final String EVENT_TYPE = "find_jobs_selected";

        public FindJobsSelected(String providerId, String versionTrack)
        {
            super(providerId, versionTrack, EVENT_TYPE);
        }
    }
}
