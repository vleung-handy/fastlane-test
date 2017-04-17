package com.handy.portal.logger.handylogger.model;

import com.google.gson.annotations.SerializedName;

public class ProAvailabilityLog extends EventLog {

    public ProAvailabilityLog(final String eventType, final String eventContext) {
        super(eventType, eventContext);
    }

    public static class SetHoursSubmitted extends ProAvailabilityLog {
        private static final String EVENT_TYPE = "set_hours_submitted";

        @SerializedName("date")
        private String mDate;
        @SerializedName("num_hours")
        private int mHours;
        @SerializedName("is_closed")
        private boolean mIsClosed;

        public SetHoursSubmitted(
                final String eventContext,
                final String date,
                final int hours,
                final boolean isClosed
        ) {
            super(EVENT_TYPE, eventContext);
            mDate = date;
            mHours = hours;
            mIsClosed = isClosed;
        }
    }


    public static class SetHoursSuccess extends ProAvailabilityLog {
        private static final String EVENT_TYPE = "set_hours_success";

        @SerializedName("date")
        private String mDate;
        @SerializedName("num_hours")
        private int mHours;
        @SerializedName("is_closed")
        private boolean mIsClosed;

        public SetHoursSuccess(
                final String eventContext,
                final String date,
                final int hours,
                final boolean isClosed
        ) {
            super(EVENT_TYPE, eventContext);
            mDate = date;
            mHours = hours;
            mIsClosed = isClosed;
        }
    }


    public static class SetHoursError extends ProAvailabilityLog {
        private static final String EVENT_TYPE = "set_hours_error";

        @SerializedName("date")
        private String mDate;
        @SerializedName("num_hours")
        private int mHours;
        @SerializedName("is_closed")
        private boolean mIsClosed;

        public SetHoursError(
                final String eventContext,
                final String date,
                final int hours,
                final boolean isClosed
        ) {
            super(EVENT_TYPE, eventContext);
            mDate = date;
            mHours = hours;
            mIsClosed = isClosed;
        }
    }


    public static class SetDayAvailabilitySelected extends ProAvailabilityLog {
        @SerializedName("date")
        private String mDate;

        private static final String EVENT_TYPE = "set_day_availability_selected";

        public SetDayAvailabilitySelected(final String eventContext, final String date) {
            super(EVENT_TYPE, eventContext);
            mDate = date;
        }
    }


    public static class CopyCurrentWeekSelected extends ProAvailabilityLog {
        private static final String EVENT_TYPE = "copy_current_week_selected";

        public CopyCurrentWeekSelected(final String eventContext) {
            super(EVENT_TYPE, eventContext);
        }
    }
}
