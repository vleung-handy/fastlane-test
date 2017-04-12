package com.handy.portal.logger.handylogger.model;

import com.google.gson.annotations.SerializedName;

public class ProAvailabilityLog extends EventLog {
    public static final String EVENT_CONTEXT = "availability";

    public ProAvailabilityLog(final String eventType, final String eventContext) {
        super(eventType, eventContext);
    }

    public static class SetHoursSubmitted extends ProAvailabilityLog {
        private static final String EVENT_TYPE = "set_hours_submitted";

        @SerializedName("date")
        private String mDate;
        @SerializedName("num_hours")
        private int mHours;

        public SetHoursSubmitted(final String eventContext, final String date, final int hours) {
            super(EVENT_TYPE, eventContext);
            mDate = date;
            mHours = hours;
        }
    }


    public static class SetHoursSuccess extends ProAvailabilityLog {
        private static final String EVENT_TYPE = "set_hours_success";

        @SerializedName("date")
        private String mDate;
        @SerializedName("num_hours")
        private int mHours;

        public SetHoursSuccess(final String eventContext, final String date, final int hours) {
            super(EVENT_TYPE, eventContext);
            mDate = date;
            mHours = hours;
        }
    }


    public static class SetHoursError extends ProAvailabilityLog {
        private static final String EVENT_TYPE = "set_hours_error";

        @SerializedName("date")
        private String mDate;
        @SerializedName("num_hours")
        private int mHours;

        public SetHoursError(final String eventContext, final String date, final int hours) {
            super(EVENT_TYPE, eventContext);
            mDate = date;
            mHours = hours;
        }
    }


    public static class RemoveHoursSubmitted extends ProAvailabilityLog {
        private static final String EVENT_TYPE = "remove_hours_submitted";

        @SerializedName("date")
        private String mDate;

        public RemoveHoursSubmitted(final String eventContext, final String date) {
            super(EVENT_TYPE, eventContext);
            mDate = date;
        }
    }


    public static class RemoveHoursSuccess extends ProAvailabilityLog {
        private static final String EVENT_TYPE = "remove_hours_success";

        @SerializedName("date")
        private String mDate;

        public RemoveHoursSuccess(final String eventContext, final String date) {
            super(EVENT_TYPE, eventContext);
            mDate = date;
        }
    }


    public static class RemoveHoursError extends ProAvailabilityLog {
        private static final String EVENT_TYPE = "remove_hours_error";

        @SerializedName("date")
        private String mDate;

        public RemoveHoursError(final String eventContext, final String date) {
            super(EVENT_TYPE, eventContext);
            mDate = date;
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
