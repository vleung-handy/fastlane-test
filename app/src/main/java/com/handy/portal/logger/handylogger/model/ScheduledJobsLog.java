package com.handy.portal.logger.handylogger.model;

import android.support.annotation.StringDef;

import com.google.gson.annotations.SerializedName;
import com.handy.portal.bookings.model.Booking;

public class ScheduledJobsLog extends EventLog {
    private static final String EVENT_CONTEXT = "scheduled_jobs";

    public ScheduledJobsLog(String eventType) {
        super(eventType, EVENT_CONTEXT);
    }


    public static class Clicked extends JobsLog {
        private static final String EVENT_TYPE = "job_selected";

        public Clicked(final Booking booking) {
            super(EVENT_TYPE, EVENT_CONTEXT, booking);
        }
    }

    // Job removal events


    public static abstract class RemoveJobLog extends JobsLog {
        public static final String REASON_FLOW = "reason_flow";
        public static final String POPUP = "popup";
        public static final String KEEP_RATE = "keep_rate";
        public static final String CANCELLATION_POLICY = "cancellation_policy";

        @SerializedName("removal_type")
        private String mRemovalType;
        @SerializedName("removal_reason")
        private String mRemovalReason;
        @SerializedName("withholding_amount")
        private int mFeeAmount;
        @SerializedName("waived_withholding_amount")
        private int mWaivedAmount;
        @SerializedName("keep_rate_old")
        private Float mOldKeepRate;
        @SerializedName("keep_rate_new")
        private Float mNewKeepRate;
        @SerializedName("warning_message")
        private String mWarningMessage;

        public RemoveJobLog(final String eventType,
                            final Booking booking,
                            final String removalType,
                            final String removalReason,
                            final int feeAmount,
                            final int waivedAmount,
                            final String warningMessage) {
            super(eventType, EVENT_CONTEXT, booking);
            mRemovalType = removalType;
            mRemovalReason = removalReason;
            mFeeAmount = feeAmount;
            mWaivedAmount = mWaivedAmount;
            mWarningMessage = warningMessage;
            final Booking.Action removeAction = booking.getAction(Booking.Action.ACTION_REMOVE);
            if (removeAction != null) {
                final Booking.Action.Extras.KeepRate keepRate = removeAction.getKeepRate();
                if (keepRate != null) {
                    mOldKeepRate = keepRate.getCurrent();
                    mNewKeepRate = keepRate.getOnNextUnassign();
                }
            }
        }
    }


    public static class RemoveJobConfirmationShown extends RemoveJobLog {
        private static final String EVENT_TYPE = "remove_job_confirmation_shown";

        public RemoveJobConfirmationShown(final Booking booking,
                                          final String removalType,
                                          final int feeAmount,
                                          final int waivedAmount,
                                          final String warningMessage) {
            super(EVENT_TYPE, booking, removalType, null, feeAmount, waivedAmount, warningMessage);
        }
    }


    public static class RemoveJobSubmitted extends RemoveJobLog {
        private static final String EVENT_TYPE = "remove_job_submitted";

        public RemoveJobSubmitted(final Booking booking,
                                  final String removalType,
                                  final String removalReason,
                                  final int feeAmount,
                                  final int waivedAmount,
                                  final String warningMessage) {
            super(EVENT_TYPE, booking, removalType, removalReason, feeAmount, waivedAmount, warningMessage);
        }
    }


    public static class RemoveJobSuccess extends RemoveJobLog {
        private static final String EVENT_TYPE = "remove_job_success";

        public RemoveJobSuccess(final Booking booking,
                                final String removalType,
                                final String removalReason,
                                final int feeAmount,
                                final int waivedAmount,
                                final String warningMessage) {
            super(EVENT_TYPE, booking, removalType, removalReason, feeAmount, waivedAmount, warningMessage);
        }
    }


    public static class RemoveJobError extends RemoveJobLog {
        private static final String EVENT_TYPE = "remove_job_error";

        @SerializedName("error_message")
        private String mErrorMessage;

        public RemoveJobError(final Booking booking,
                              final String removalType,
                              final String removalReason,
                              final int feeAmount,
                              final int waivedAmount,
                              final String warningMessage,
                              final String errorMessage) {
            super(EVENT_TYPE, booking, removalType, removalReason, feeAmount, waivedAmount, warningMessage);
            mErrorMessage = errorMessage;
        }
    }


    public static class BookingInstructionsShown extends ScheduledJobsLog {
        private static final String EVENT_TYPE = "job_instructions_shown";

        @SerializedName("booking_id")
        private String mBookingId;

        public BookingInstructionsShown(String bookingId) {
            super(EVENT_TYPE);
            mBookingId = bookingId;
        }
    }


    public static class JobSupportSelected extends ScheduledJobsLog {
        private static final String EVENT_TYPE = "job_support_selected";

        @SerializedName("booking_id")
        private String mBookingId;

        public JobSupportSelected(String bookingId) {
            super(EVENT_TYPE);
            mBookingId = bookingId;
        }
    }


    public static class JobSupportItemSelected extends ScheduledJobsLog {
        private static final String EVENT_TYPE = "job_support_item_selected";

        @SerializedName("booking_id")
        private String mBookingId;
        @SerializedName("action_name")
        private String mActionName;


        public JobSupportItemSelected(String bookingId, String actionName) {
            super(EVENT_TYPE);
            mBookingId = bookingId;
            mActionName = actionName;
        }
    }


    public static class CustomerNoShowModalShown extends ScheduledJobsLog {
        private static final String EVENT_TYPE = "customer_no_show_modal_shown";

        @SerializedName("booking_id")
        private String mBookingId;

        public CustomerNoShowModalShown(String bookingId) {
            super(EVENT_TYPE);
            mBookingId = bookingId;
        }
    }


    public static class SetWeekAvailabilitySelected extends ScheduledJobsLog {
        @SerializedName("date")
        private String mDate;

        private static final String EVENT_TYPE = "set_week_availability_selected";

        public SetWeekAvailabilitySelected(final String date) {
            super(EVENT_TYPE);
            mDate = date;
        }
    }


    public static class SetDayAvailabilitySelected extends ScheduledJobsLog {
        @SerializedName("date")
        private String mDate;

        private static final String EVENT_TYPE = "set_day_availability_selected";

        public SetDayAvailabilitySelected(final String date) {
            super(EVENT_TYPE);
            mDate = date;
        }
    }


    public static class ContactCustomerLog extends ScheduledJobsLog {
        @StringDef({
                EventType.CALL_CUSTOMER_SELECTED,
                EventType.CALL_CUSTOMER_FAILED,
                EventType.TEXT_CUSTOMER_SELECTED,
                EventType.TEXT_CUSTOMER_FAILED,
                EventType.IN_APP_CHAT_WITH_CUSTOMER_SELECTED,
                EventType.IN_APP_CHAT_WITH_CUSTOMER_FAILED
        })
        public @interface ContactEventType {}


        @SerializedName("user_id")
        private String mUserId;

        public ContactCustomerLog(@ContactEventType String eventType, String userId) {
            super(eventType);
            mUserId = userId;
        }
    }

}
