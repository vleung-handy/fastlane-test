package com.handy.portal.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PerformanceInfo implements Serializable
{
    @SerializedName("total_jobs_count")
    private int totalJobsCount;
    @SerializedName("total_rating")
    private float totalRating;
    @SerializedName("trailing_28_day_jobs_count")
    private int trailing28DayJobsCount;
    @SerializedName("trailing_28_day_rating")
    private float trailing28DayRating;
    @SerializedName("rate")
    private String rate;
    @SerializedName("tier")
    private int tier;

    public int getTotalJobsCount()
    {
        return totalJobsCount;
    }

    public float getTotalRating()
    {
        return totalRating;
    }

    public int getTrailing28DayJobsCount()
    {
        return trailing28DayJobsCount;
    }

    public float getTrailing28DayRating()
    {
        return trailing28DayRating;
    }

    public String getRate()
    {
        return rate;
    }

    public int getTier()
    {
        return tier;
    }
}
