package com.handy.portal.logger.handylogger.model;

import com.google.gson.annotations.SerializedName;
import com.handy.portal.model.Address;
import com.handy.portal.model.Booking;

import java.util.Date;

public class ScheduledJobsLog extends EventLog
{
    private static final String EVENT_CONTEXT = "scheduled_jobs";

    public ScheduledJobsLog(String eventType)
    {
        super(eventType, EVENT_CONTEXT);
    }

    public static class DateClicked extends ScheduledJobsLog
    {
        private static final String EVENT_TYPE = "date_selected";

        @SerializedName("date")
        private Date mDate;
        @SerializedName("job_count")
        private int mJobCount;

        public DateClicked(Date date, int jobCount)
        {
            super(EVENT_TYPE);
            mDate = date;
            mJobCount = jobCount;
        }
    }


    public static class Clicked extends JobsLog
    {
        private static final String EVENT_TYPE = "job_selected";

        @SerializedName("list_number")
        private int mListNumber;

        public Clicked(final Booking booking, final int listNumber)
        {
            super(EVENT_TYPE, EVENT_CONTEXT, booking);
            mListNumber = listNumber;
        }
    }

    // Job removal events


    public static abstract class RemoveJobLog extends JobsLog
    {
        public static final String REASON_FLOW = "reason_flow";
        public static final String POPUP = "popup";

        @SerializedName("removal_type")
        private String mRemovalType;
        @SerializedName("removal_reason")
        private String mRemovalReason;
        @SerializedName("withholding_amount")
        private int mWithholdingAmount;
        @SerializedName("warning_message")
        private String mWarningMessage;

        public RemoveJobLog(final String eventType,
                            final Booking booking,
                            final String removalType,
                            final String removalReason,
                            final int withholdingAmount,
                            final String warningMessage)
        {
            super(eventType, EVENT_CONTEXT, booking);
            mRemovalType = removalType;
            mRemovalReason = removalReason;
            mWithholdingAmount = withholdingAmount;
            mWarningMessage = warningMessage;
        }
    }


    public static class RemoveJobConfirmationShown extends RemoveJobLog
    {
        private static final String EVENT_TYPE = "remove_confirmation_shown";

        public RemoveJobConfirmationShown(final Booking booking,
                                          final String removalType,
                                          final int withholdingAmount,
                                          final String warningMessage)
        {
            super(EVENT_TYPE, booking, removalType, null, withholdingAmount, warningMessage);
        }
    }


    public static class RemoveJobSubmitted extends RemoveJobLog
    {
        private static final String EVENT_TYPE = "remove_job_submitted";

        public RemoveJobSubmitted(final Booking booking,
                                  final String removalType,
                                  final String removalReason,
                                  final int withholdingAmount,
                                  final String warningMessage)
        {
            super(EVENT_TYPE, booking, removalType, removalReason, withholdingAmount, warningMessage);
        }
    }


    public static class RemoveJobSuccess extends RemoveJobLog
    {
        private static final String EVENT_TYPE = "remove_job_success";

        public RemoveJobSuccess(final Booking booking,
                                final String removalType,
                                final String removalReason,
                                final int withholdingAmount,
                                final String warningMessage)
        {
            super(EVENT_TYPE, booking, removalType, removalReason, withholdingAmount, warningMessage);
        }
    }


    public static class RemoveJobError extends RemoveJobLog
    {
        private static final String EVENT_TYPE = "remove_job_error";

        @SerializedName("error_message")
        private String mErrorMessage;

        public RemoveJobError(final Booking booking,
                              final String removalType,
                              final String removalReason,
                              final int withholdingAmount,
                              final String warningMessage,
                              final String errorMessage)
        {
            super(EVENT_TYPE, booking, removalType, removalReason, withholdingAmount, warningMessage);
            mErrorMessage = errorMessage;
        }
    }

    // Customer rating events


    public static class CustomerRatingShown extends ScheduledJobsLog
    {
        private static final String EVENT_TYPE = "customer_rating_shown";

        public CustomerRatingShown()
        {
            super(EVENT_TYPE);
        }
    }


    public static class CustomerRatingSubmitted extends ScheduledJobsLog
    {
        private static final String EVENT_TYPE = "customer_rating_submitted";

        @SerializedName("rating")
        private int mRating;

        public CustomerRatingSubmitted(int rating)
        {
            super(EVENT_TYPE);
            mRating = rating;
        }
    }


    public static class BookingInstructionsSeen extends ScheduledJobsLog
    {
        private static final String EVENT_TYPE = "job_instructions_viewed";

        @SerializedName("booking_id")
        private String mBookingId;

        public BookingInstructionsSeen(String bookingId)
        {
            super(EVENT_TYPE);
            mBookingId = bookingId;
        }
    }


    public static class SupportSelected extends ScheduledJobsLog
    {
        private static final String EVENT_TYPE = "booking_support_selected";

        @SerializedName("booking_id")
        private String mBookingId;

        public SupportSelected(String bookingId)
        {
            super(EVENT_TYPE);
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


        public HelpItemSelected(String bookingId, String helpItemLabel)
        {
            super(EVENT_TYPE);
            mBookingId = bookingId;
            mHelpItemLabel = helpItemLabel;
        }
    }


    public static class FindJobsSelected extends ScheduledJobsLog
    {
        private static final String EVENT_TYPE = "find_jobs_selected";

        public FindJobsSelected()
        {
            super(EVENT_TYPE);
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
