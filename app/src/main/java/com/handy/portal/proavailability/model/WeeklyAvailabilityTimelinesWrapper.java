package com.handy.portal.proavailability.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;
import com.handy.portal.library.util.DateTimeUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class WeeklyAvailabilityTimelinesWrapper implements Serializable {
    @SerializedName("start_date")
    private String mStartDate;
    @SerializedName("end_date")
    private String mEndDate;
    @SerializedName("timelines")
    private ArrayList<DailyAvailabilityTimeline> mDailyAvailabilityTimelines;

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

    public ArrayList<DailyAvailabilityTimeline> getDailyAvailabilityTimelines() {
        return mDailyAvailabilityTimelines;
    }

    @Nullable
    public DailyAvailabilityTimeline getAvailabilityForDate(@NonNull final Date date) {
        for (DailyAvailabilityTimeline dailyAvailabilityTimeline : mDailyAvailabilityTimelines) {
            if (dailyAvailabilityTimeline.matchesDate(date)) {
                return dailyAvailabilityTimeline;
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
            final DailyAvailabilityTimeline availability =
                    getAvailabilityForDate(calendar.getTime());
            if (availability != null) {
                hasAvailableHours = availability.hasIntervals();
            }
            if (hasAvailableHours) {
                break;
            }
            calendar.add(Calendar.DATE, 1);
        }
        return hasAvailableHours;
    }
}
