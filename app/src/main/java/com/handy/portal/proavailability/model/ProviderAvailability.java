package com.handy.portal.proavailability.model;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class ProviderAvailability implements Serializable
{
    @SerializedName("weekly_timelines")
    private ArrayList<WeeklyAvailabilityTimeline> mWeeklyAvailabilityTimelines;

    @Nullable
    public DailyAvailabilityTimeline getAvailabilityForDate(final Date date)
    {
        final WeeklyAvailabilityTimeline weeklyAvailabilityTimeline =
                getWeeklyAvailabilityForDate(date);
        if (weeklyAvailabilityTimeline != null)
        {
            return weeklyAvailabilityTimeline.getAvailabilityForDate(date);
        }
        return null;
    }

    @Nullable
    public WeeklyAvailabilityTimeline getWeeklyAvailabilityForDate(final Date date)
    {
        for (WeeklyAvailabilityTimeline weeklyAvailabilityTimeline : mWeeklyAvailabilityTimelines)
        {
            if (weeklyAvailabilityTimeline.covers(date))
            {
                return weeklyAvailabilityTimeline;
            }
        }
        return null;
    }

    public boolean covers(final Date date)
    {
        return getWeeklyAvailabilityForDate(date) != null;
    }
}
