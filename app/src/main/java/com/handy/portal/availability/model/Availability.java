package com.handy.portal.availability.model;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;
import com.handy.portal.library.util.DateTimeUtils;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public abstract class Availability {

    public static abstract class Wrapper {

        public static class Timelines implements Serializable {
            @SerializedName("timelines")
            private ArrayList<Timeline> mTimelines;

            public Timelines() {
                mTimelines = new ArrayList<>();
            }

            public void addTimeline(final Date date, final ArrayList<Interval> intervals) {
                mTimelines.add(new Timeline(date, intervals));
            }

            public ArrayList<Timeline> get() {
                return mTimelines;
            }
        }


        public static class WeekRanges implements Serializable {
            @SerializedName("weekly_timelines")
            private ArrayList<Range> mWeekRanges;

            public ArrayList<Range> get() {
                return mWeekRanges;
            }

            /**
             * @param maxWeeks Cap the number of items returned to max
             * @return
             */
            public ArrayList<Range> get(final int maxWeeks) {
                if (mWeekRanges == null || maxWeeks >= mWeekRanges.size()) {
                    return mWeekRanges;
                }
                else {
                    ArrayList<Range> cappedWeeks = new ArrayList<>();
                    //get the number of weeks up to max weeks
                    for (int i = 0; i < maxWeeks; i++) {
                        cappedWeeks.add(mWeekRanges.get(i));
                    }

                    return cappedWeeks;
                }
            }

            @Nullable
            public Timeline getTimelineForDate(final Date date) {
                final Range weekRange = getWeekRangeForDate(date);
                if (weekRange != null) {
                    return weekRange.getTimelineForDate(date);
                }
                return null;
            }

            @Nullable
            public Range getWeekRangeForDate(final Date date) {
                for (Range weekRange : mWeekRanges) {
                    if (weekRange.covers(date)) {
                        return weekRange;
                    }
                }
                return null;
            }

            public boolean covers(final Date date) {
                return getWeekRangeForDate(date) != null;
            }

            public boolean hasAvailableHours() {
                for (Range weekRange : mWeekRanges) {
                    if (weekRange.hasAvailableHours()) {
                        return true;
                    }
                }
                return false;
            }
        }
    }


    public static class Range implements Serializable {
        @SerializedName("start_date")
        private String mStartDate;
        @SerializedName("end_date")
        private String mEndDate;
        @SerializedName("timelines")
        private ArrayList<Timeline> mTimelines;

        public String getStartDateString() {
            return mStartDate;
        }

        public String getEndDateString() {
            return mEndDate;
        }

        @Nullable
        public Date getStartDate() {
            return DateTimeUtils.parseDateString(mStartDate, DateTimeUtils.YEAR_MONTH_DAY_FORMATTER);
        }

        @Nullable
        public Date getEndDate() {
            return DateTimeUtils.parseDateString(mEndDate, DateTimeUtils.YEAR_MONTH_DAY_FORMATTER);
        }

        // WARNING: Package access here is intentional, always access time lines using AvailabilityManager.
        @Nullable
        Timeline getTimelineForDate(@NonNull final Date date) {
            for (Timeline timeline : mTimelines) {
                if (timeline.matchesDate(date)) {
                    return timeline;
                }
            }
            return null;
        }

        public boolean covers(@NonNull final Date date) {
            final Date startDate = getStartDate();
            final Date endDate = getEndDate();
            if (startDate == null || endDate == null) {
                return false;
            }
            else {
                return DateTimeUtils.daysBetween(date, startDate) <= 0
                        && DateTimeUtils.daysBetween(date, endDate) >= 0;
            }
        }

        public boolean hasAvailableHours() {
            boolean hasAvailableHours = false;
            final Calendar calendar = Calendar.getInstance(Locale.US);
            calendar.setTime(getStartDate());
            while (DateTimeUtils.daysBetween(calendar.getTime(), getEndDate()) >= 0) {
                final Timeline timeline = getTimelineForDate(calendar.getTime());
                if (timeline != null) {
                    hasAvailableHours = timeline.hasIntervals();
                }
                if (hasAvailableHours) {
                    break;
                }
                calendar.add(Calendar.DATE, 1);
            }
            return hasAvailableHours;
        }

        public List<Date> dates() {
            final ArrayList<Date> dates = new ArrayList<>();
            final Calendar calendar = Calendar.getInstance(Locale.US);
            final Date startDate = getStartDate();
            final Date endDate = getEndDate();
            calendar.setTime(startDate);
            while (DateTimeUtils.daysBetween(calendar.getTime(), endDate) >= 0) {
                dates.add(calendar.getTime());
                calendar.add(Calendar.DATE, 1);
            }
            return dates;
        }
    }


    public static class Timeline implements Serializable {
        @SerializedName("timeline_date")
        private String mDate;
        @SerializedName("interval_array")
        private ArrayList<Interval> mIntervals;

        public Timeline(final Date date,
                        final ArrayList<Interval> intervals) {
            mDate = DateTimeUtils.YEAR_MONTH_DAY_FORMATTER.format(date);
            mIntervals = intervals;
        }

        public String getDateString() {
            return mDate;
        }

        @Nullable
        public Date getDate() {
            return DateTimeUtils.parseDateString(mDate, DateTimeUtils.YEAR_MONTH_DAY_FORMATTER);
        }

        /**
         * Checks if the date (excluding time) matches between this object's corresponding date and the
         * date passed in as a parameter.
         *
         * @param date
         * @return
         */
        public boolean matchesDate(final Date date) {
            return DateTimeUtils.daysBetween(getDate(), date) == 0;
        }

        public ArrayList<Interval> getIntervals() {
            return mIntervals;
        }

        public boolean hasIntervals() {
            return mIntervals != null && !mIntervals.isEmpty();
        }
    }


    public static class Interval implements Serializable {
        private static final SimpleDateFormat TIME_FORMAT =
                new SimpleDateFormat("HH:mm:ss", Locale.getDefault());

        @SerializedName("start_time")
        private String mStartTime;
        @SerializedName("end_time")
        private String mEndTime;

        public Interval(final int startHour, final int endHour) {
            mStartTime = TIME_FORMAT.format(DateTimeUtils.parseDateString(String.valueOf(startHour),
                    DateTimeUtils.HOUR_INT_FORMATTER));
            mEndTime = TIME_FORMAT.format(DateTimeUtils.parseDateString(String.valueOf(endHour),
                    DateTimeUtils.HOUR_INT_FORMATTER));
        }

        public int getStartHour() {
            return DateTimeUtils.getHourInt(getStartTime());
        }

        public int getEndHour() {
            return DateTimeUtils.getHourInt(getEndTime());
        }

        @Nullable
        public Date getStartTime() {
            return DateTimeUtils.parseDateString(mStartTime, TIME_FORMAT);
        }

        @Nullable
        public Date getEndTime() {
            return DateTimeUtils.parseDateString(mEndTime, TIME_FORMAT);
        }

        @Override
        public boolean equals(final Object object) {
            if (object instanceof Interval) {
                final Interval otherInterval = (Interval) object;
                return otherInterval.getStartHour() == getStartHour()
                        && otherInterval.getEndHour() == getEndHour();
            }
            else {
                return super.equals(object);
            }
        }
    }


}
