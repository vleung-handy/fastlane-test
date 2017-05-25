package com.handy.portal.logger.handylogger.model;


import com.google.gson.annotations.SerializedName;
import com.handy.portal.bookings.model.Booking;
import com.handy.portal.bookings.model.CheckoutRequest;

import java.util.ArrayList;

public class CheckOutFlowLog extends EventLog {
    @SerializedName("booking_id")
    private String mBookingId;
    @SerializedName("user_id")
    private String mUserId;

    public CheckOutFlowLog(final String eventType, final Booking booking) {
        super(eventType, EventContext.CHECKOUT_FLOW);
        mBookingId = booking.getId();
        if (booking.getUser() != null) {
            mUserId = booking.getUser().getId();
        }
    }

    public static class ManualCheckOutLog extends CheckOutFlowLog {
        @SerializedName("note_to_customer")
        private String mNoteToCustomer;
        @SerializedName("signature_sent")
        private boolean mSignatureSent;
        @SerializedName("completed_tasks")
        private ArrayList<String> mCompletedTasks;
        @SerializedName("completed_task_count")
        private int mCompletedTaskCount;

        public ManualCheckOutLog(
                final String eventType,
                final Booking booking,
                final boolean signatureSent,
                final CheckoutRequest checkoutRequest
        ) {
            super(eventType, booking);
            mSignatureSent = signatureSent;
            mNoteToCustomer = checkoutRequest.getNoteToCustomer();
            mCompletedTaskCount = 0;
            mCompletedTasks = new ArrayList<>();
            for (final Booking.BookingInstructionUpdateRequest request :
                    checkoutRequest.getCustomerPreferences()) {
                if (request.isInstructionCompleted()) {
                    mCompletedTaskCount++;
                    mCompletedTasks.add(request.getMachineName());
                }

            }
        }
    }


    public static class CustomerPreferenceSelected extends CheckOutFlowLog {
        @SerializedName("work_again")
        private boolean mCustomerPreferred;

        public CustomerPreferenceSelected(final Booking booking, final boolean customerPreferred) {
            super("customer_preference_selected", booking);
            mCustomerPreferred = customerPreferred;
        }
    }


    public static class UpcomingJobsShown extends CheckOutFlowLog {
        @SerializedName("num_jobs")
        private int mNumberOfJobs;
        @SerializedName("max_dollar_value")
        private int mMaxDollarValue;

        public UpcomingJobsShown(
                final Booking booking,
                final int numberOfJobs,
                final int maxDollarValue
        ) {
            super("upcoming_jobs_shown", booking);
            mNumberOfJobs = numberOfJobs;
            mMaxDollarValue = maxDollarValue;
        }
    }


    public static class PostCheckoutLog extends CheckOutFlowLog {
        @SerializedName("work_again")
        private boolean mCustomerPreferred;
        @SerializedName("feedback")
        private String mFeedback;
        @SerializedName("num_jobs")
        private int mNumberOfJobs;

        public PostCheckoutLog(
                final String eventType,
                final Booking booking,
                final boolean customerPreferred,
                final String feedback,
                final int numberOfJobs
        ) {
            super(eventType, booking);
            mCustomerPreferred = customerPreferred;
            mFeedback = feedback;
            mNumberOfJobs = numberOfJobs;
        }
    }
}
