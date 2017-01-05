package com.handy.portal.availability.model;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;
import com.handy.portal.library.util.DateTimeUtils;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

public class DailyAvailabilityTimeline implements Serializable
{
    @SerializedName("date")
    private String mDate;
    @SerializedName("intervals")
    private ArrayList<AvailabilityInterval> mAvailabilityIntervals;

    @Nullable
    public Date getDate()
    {
        try
        {
            return DateTimeUtils.YEAR_MONTH_DAY_FORMATTER.parse(mDate);
        }
        catch (ParseException e)
        {
            return null;
        }
    }

    public boolean matchesDate(final Date date)
    {
        return DateTimeUtils.daysBetween(getDate(), date) == 0;
    }

    public ArrayList<AvailabilityInterval> getAvailabilityIntervals()
    {
        return mAvailabilityIntervals;
    }
}
