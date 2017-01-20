package com.handy.portal.proavailability.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class AvailabilityTimelinesWrapper implements Serializable
{
    @SerializedName("timelines")
    private ArrayList<DailyAvailabilityTimeline> mDailyAvailabilityTimelines;

    public AvailabilityTimelinesWrapper()
    {
        mDailyAvailabilityTimelines = new ArrayList<>();
    }

    public void addTimeline(final Date date, final ArrayList<AvailabilityInterval> intervals)
    {
        mDailyAvailabilityTimelines.add(new DailyAvailabilityTimeline(date, intervals));
    }

    public ArrayList<DailyAvailabilityTimeline> getTimelines()
    {
        return mDailyAvailabilityTimelines;
    }
}
