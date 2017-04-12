package com.handy.portal.logger.handylogger.model;


import com.google.gson.annotations.SerializedName;
import com.handy.portal.bookings.model.Booking;

import java.util.Date;

public abstract class SendAvailabilityLog extends EventLog {
    public static final String EVENT_CONTEXT = "send_availability";

    private SendAvailabilityLog(final String eventType, final String eventContext) {
        super(eventType, eventContext);
    }

    public static class SendAvailabilitySelected extends SendAvailabilityLog {
        @SerializedName("booking_id")
        private String mBookingId;
        @SerializedName("user_id")
        private String mUserId;
        @SerializedName("date_start")
        private Date mStartDate;

        public SendAvailabilitySelected(final String eventContext, final Booking booking) {
            super("send_availability_selected", eventContext);
            mBookingId = booking.getId();
            if (booking.getRequestAttributes() != null) {
                mUserId = booking.getRequestAttributes().getCustomerId();
            }
            mStartDate = booking.getStartDate();
        }
    }


    public static class SendAvailabilitySubmitted extends SendAvailabilityLog {
        @SerializedName("booking_id")
        private String mBookingId;
        @SerializedName("user_id")
        private String mUserId;

        public SendAvailabilitySubmitted(final Booking booking) {
            super("send_availability_submitted", EVENT_CONTEXT);
            mBookingId = booking.getId();
            if (booking.getRequestAttributes() != null) {
                mUserId = booking.getRequestAttributes().getCustomerId();
            }
        }
    }
}
