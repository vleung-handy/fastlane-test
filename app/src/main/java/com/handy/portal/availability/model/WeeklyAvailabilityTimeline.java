package com.handy.portal.availability.model;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;
import com.handy.portal.library.util.DateTimeUtils;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

public class WeeklyAvailabilityTimeline implements Serializable
{
    @SerializedName("start_date")
    private String mStartDate;
    @SerializedName("end_date")
    private String mEndDate;
    @SerializedName("timelines")
    private ArrayList<DailyAvailabilityTimeline> mDailyAvailabilityTimelines;

    public ArrayList<DailyAvailabilityTimeline> getDailyAvailabilityTimelines()
    {
        return mDailyAvailabilityTimelines;
    }

    @Nullable
    public Date getStartDate()
    {
        try
        {
            return DateTimeUtils.YEAR_MONTH_DAY_FORMATTER.parse(mStartDate);
        }
        catch (ParseException e)
        {
            return null;
        }
    }

    @Nullable
    public Date getEndDate()
    {
        try
        {
            return DateTimeUtils.YEAR_MONTH_DAY_FORMATTER.parse(mEndDate);
        }
        catch (ParseException e)
        {
            return null;
        }
    }

    @Nullable
    public DailyAvailabilityTimeline getAvailabilityForDate(final Date date)
    {
        for (DailyAvailabilityTimeline dailyAvailabilityTimeline : getDailyAvailabilityTimelines())
        {
            if (dailyAvailabilityTimeline.matchesDate(date))
            {
                return dailyAvailabilityTimeline;
            }
        }
        return null;
    }

    public boolean covers(final Date date)
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
