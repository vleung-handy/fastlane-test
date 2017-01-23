package com.handy.portal.proavailability.model;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;
import com.handy.portal.library.util.DateTimeUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class DailyAvailabilityTimeline implements Serializable
{
    @SerializedName("timeline_date")
    private String mDate;
    @SerializedName("interval_array")
    private ArrayList<AvailabilityInterval> mAvailabilityIntervals;

    public DailyAvailabilityTimeline(final Date date,
                                     final ArrayList<AvailabilityInterval> intervals)
    {
        mDate = DateTimeUtils.YEAR_MONTH_DAY_FORMATTER.format(date);
        mAvailabilityIntervals = intervals;
    }

    public String getDateString()
    {
        return mDate;
    }

    @Nullable
    public Date getDate()
    {
        return DateTimeUtils.parseDateString(mDate, DateTimeUtils.YEAR_MONTH_DAY_FORMATTER);
    }

    /**
     * Checks if the date (excluding time) matches between this object's corresponding date and the
     * date passed in as a parameter.
     *
     * @param date
     * @return
     */
    public boolean matchesDate(final Date date)
    {
        return DateTimeUtils.daysBetween(getDate(), date) == 0;
    }

    public ArrayList<AvailabilityInterval> getAvailabilityIntervals()
    {
        return mAvailabilityIntervals;
    }

    public boolean hasIntervals()
    {
        return mAvailabilityIntervals != null && !mAvailabilityIntervals.isEmpty();
    }
}
