package com.handy.portal.proavailability.model;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;
import com.handy.portal.library.util.DateTimeUtils;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AvailabilityInterval implements Serializable
{
    private static final SimpleDateFormat TIME_FORMAT =
            new SimpleDateFormat("HH:mm", Locale.getDefault());

    @SerializedName("start")
    private String mStartTime;
    @SerializedName("end")
    private String mEndTime;

    public AvailabilityInterval(final int startTime, final int endTime)
    {
        mStartTime = TIME_FORMAT.format(DateTimeUtils.parseDateString(String.valueOf(startTime),
                DateTimeUtils.HOUR_INT_FORMATTER));
        mEndTime = TIME_FORMAT.format(DateTimeUtils.parseDateString(String.valueOf(endTime),
                DateTimeUtils.HOUR_INT_FORMATTER));
    }

    public int getStartTimeInt()
    {
        return DateTimeUtils.getHourInt(getStartTime());
    }

    public int getEndTimeInt()
    {
        return DateTimeUtils.getHourInt(getEndTime());
    }

    @Nullable
    public Date getStartTime()
    {
        return DateTimeUtils.parseDateString(mStartTime, TIME_FORMAT);
    }

    @Nullable
    public Date getEndTime()
    {
        return DateTimeUtils.parseDateString(mEndTime, TIME_FORMAT);
    }
}
