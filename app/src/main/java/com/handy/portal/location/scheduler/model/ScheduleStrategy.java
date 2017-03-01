package com.handy.portal.location.scheduler.model;

import android.os.Parcelable;

import java.util.Date;

/**
 * An abstract strategy model that contains start and end dates that denote when the strategy should be active
 * Used in schedules
 */
public abstract class ScheduleStrategy implements Parcelable {
    public abstract Date getStartDate();

    public abstract Date getEndDate();

}
