package com.handy.portal.availability.model;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.text.ParseException;
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

    @Nullable
    public Date getStartTime()
    {
        try
        {
            return TIME_FORMAT.parse(mStartTime);
        }
        catch (ParseException e)
        {
            return null;
        }
    }

    @Nullable
    public Date getEndTime()
    {
        try
        {
            return TIME_FORMAT.parse(mEndTime);
        }
        catch (ParseException e)
        {
            return null;
        }
    }
}
