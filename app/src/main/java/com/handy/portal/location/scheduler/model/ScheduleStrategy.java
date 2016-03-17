package com.handy.portal.location.scheduler.model;

import android.os.Parcelable;

import java.util.Date;

/**
 * An abstract strategy that contains start and end dates. Used in schedules
 */
public abstract class ScheduleStrategy implements Parcelable
{
    public abstract Date getStartDate();
    public abstract Date getEndDate();

}
