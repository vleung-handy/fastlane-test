package com.handy.portal.proavailability.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;
import com.handy.portal.library.util.DateTimeUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class WeeklyAvailabilityTimelinesWrapper implements Serializable
{
    @SerializedName("start_date")
    private String mStartDate;
    @SerializedName("end_date")
    private String mEndDate;
    @SerializedName("timelines")
    private ArrayList<DailyAvailabilityTimeline> mDailyAvailabilityTimelines;

    @Nullable
    public Date getStartDate()
    {
        return DateTimeUtils.parseDateString(mStartDate, DateTimeUtils.YEAR_MONTH_DAY_FORMATTER);
    }

    @Nullable
    public Date getEndDate()
    {
        return DateTimeUtils.parseDateString(mEndDate, DateTimeUtils.YEAR_MONTH_DAY_FORMATTER);
    }

    public ArrayList<DailyAvailabilityTimeline> getDailyAvailabilityTimelines()
    {
        return mDailyAvailabilityTimelines;
    }

    @Nullable
    public DailyAvailabilityTimeline getAvailabilityForDate(@NonNull final Date date)
    {
        for (DailyAvailabilityTimeline dailyAvailabilityTimeline : mDailyAvailabilityTimelines)
        {
            if (dailyAvailabilityTimeline.matchesDate(date))
            {
                return dailyAvailabilityTimeline;
            }
        }
        return null;
    }

    public boolean covers(@NonNull final Date date)
    {
        final Date startDate = getStartDate();
        final Date endDate = getEndDate();
        if (startDate == null || endDate == null)
        {
            return false;
        }
        else
        {
            return DateTimeUtils.daysBetween(date, startDate) <= 0
                    && DateTimeUtils.daysBetween(date, endDate) >= 0;
        }
    }
}
