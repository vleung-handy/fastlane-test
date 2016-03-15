package com.handy.portal.location.scheduler.model;

import android.os.Parcelable;

import java.util.Date;

/**
 * Created by vleung on 3/16/16.
 */
public abstract class LocationStrategy implements Parcelable
{
    public abstract Date getStartDate();
    public abstract Date getEndDate();

}
