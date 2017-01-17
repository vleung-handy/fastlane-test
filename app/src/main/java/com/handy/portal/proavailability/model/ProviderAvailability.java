package com.handy.portal.proavailability.model;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class ProviderAvailability implements Serializable
{
    @SerializedName("weekly_timelines")
    private ArrayList<WeeklyAvailabilityTimelinesWrapper> mWeeklyAvailabilityTimelineWrappers;

    @Nullable
    public DailyAvailabilityTimeline getAvailabilityForDate(final Date date)
    {
        final WeeklyAvailabilityTimelinesWrapper weeklyAvailabilityTimelinesWrapper =
                getWeeklyAvailabilityForDate(date);
        if (weeklyAvailabilityTimelinesWrapper != null)
        {
            return weeklyAvailabilityTimelinesWrapper.getAvailabilityForDate(date);
        }
        return null;
    }

    @Nullable
    public WeeklyAvailabilityTimelinesWrapper getWeeklyAvailabilityForDate(final Date date)
    {
        for (WeeklyAvailabilityTimelinesWrapper weeklyAvailabilityTimelinesWrapper :
                mWeeklyAvailabilityTimelineWrappers)
        {
            if (weeklyAvailabilityTimelinesWrapper.covers(date))
            {
                return weeklyAvailabilityTimelinesWrapper;
            }
        }
        return null;
    }

    public boolean covers(final Date date)
    {
        return getWeeklyAvailabilityForDate(date) != null;
    }
}
