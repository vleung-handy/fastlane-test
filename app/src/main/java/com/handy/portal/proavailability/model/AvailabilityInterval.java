package com.handy.portal.proavailability.model;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;
import com.handy.portal.library.util.DateTimeUtils;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AvailabilityInterval implements Serializable {
    private static final SimpleDateFormat TIME_FORMAT =
            new SimpleDateFormat("HH:mm:ss", Locale.getDefault());

    @SerializedName("start_time")
    private String mStartTime;
    @SerializedName("end_time")
    private String mEndTime;

    public AvailabilityInterval(final int startHour, final int endHour) {
        mStartTime = TIME_FORMAT.format(DateTimeUtils.parseDateString(String.valueOf(startHour),
                DateTimeUtils.HOUR_INT_FORMATTER));
        mEndTime = TIME_FORMAT.format(DateTimeUtils.parseDateString(String.valueOf(endHour),
                DateTimeUtils.HOUR_INT_FORMATTER));
    }

    public int getStartHour() {
        return DateTimeUtils.getHourInt(getStartTime());
    }

    public int getEndHour() {
        return DateTimeUtils.getHourInt(getEndTime());
    }

    @Nullable
    public Date getStartTime() {
        return DateTimeUtils.parseDateString(mStartTime, TIME_FORMAT);
    }

    @Nullable
    public Date getEndTime() {
        return DateTimeUtils.parseDateString(mEndTime, TIME_FORMAT);
    }

    @Override
    public boolean equals(final Object object) {
        if (object instanceof AvailabilityInterval) {
            final AvailabilityInterval otherInterval = (AvailabilityInterval) object;
            return otherInterval.getStartHour() == getStartHour()
                    && otherInterval.getEndHour() == getEndHour();
        }
        else {
            return super.equals(object);
        }
    }
}
