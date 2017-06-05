package com.handy.portal.logger.handylogger.model;

import com.google.gson.annotations.SerializedName;
import com.handy.portal.availability.model.Availability;

public class ProAvailabilityLog extends EventLog {

    public ProAvailabilityLog(final String eventType, final String eventContext) {
        super(eventType, eventContext);
    }

    public static class SetHoursLog extends ProAvailabilityLog {
        @SerializedName("date")
        private String mDate;
        @SerializedName("num_hours")
        private int mHours;
        @SerializedName("is_closed")
        private boolean mIsClosed;

        public SetHoursLog(
                final String eventType,
                final String eventContext,
                final String date,
                final int hours,
                final boolean isClosed
        ) {
            super(eventType, eventContext);
            mDate = date;
            mHours = hours;
            mIsClosed = isClosed;
        }
    }


    public static class SetTemplateHoursLog extends ProAvailabilityLog {
        @SerializedName("day")
        private Integer mDay;
        @SerializedName("num_hours")
        private int mHours;
        @SerializedName("is_closed")
        private boolean mIsClosed;

        public SetTemplateHoursLog(
                final String eventType,
                final String eventContext,
                final Availability.TemplateTimeline.Day day,
                final int hours,
                final boolean isClosed
        ) {
            super(eventType, eventContext);
            mDay = day.ordinal();
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


    public static class SetTemplateDayAvailabilitySelected extends ProAvailabilityLog {
        private static final String EVENT_TYPE = "set_template_day_availability_selected";
        @SerializedName("day")
        private Integer mDay;

        public SetTemplateDayAvailabilitySelected(
                final String eventContext,
                final Availability.TemplateTimeline.Day day
        ) {
            super(EVENT_TYPE, eventContext);
            mDay = day.ordinal();
        }
    }


    public static class CopyCurrentWeekSelected extends ProAvailabilityLog {
        private static final String EVENT_TYPE = "copy_current_week_selected";

        public CopyCurrentWeekSelected(final String eventContext) {
            super(EVENT_TYPE, eventContext);
        }
    }
}
