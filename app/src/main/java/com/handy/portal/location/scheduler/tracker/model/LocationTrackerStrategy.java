package com.handy.portal.location.scheduler.tracker.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.handy.portal.location.scheduler.model.LocationStrategy;

import java.util.Date;

//TODO: at testing stage. not finalized, needs lots of refactoring


/**
 * part of the LocationQuerySchedule model received from the server
 * <p/>
 * the location service uses this to determine when and at what accuracy we should get location updates
 */
public class LocationTrackerStrategy extends LocationStrategy implements Parcelable
{
    public static final Creator<LocationTrackerStrategy> CREATOR = new Creator<LocationTrackerStrategy>()
    {
        @Override
        public LocationTrackerStrategy createFromParcel(Parcel in)
        {
            return new LocationTrackerStrategy(in);
        }

        @Override
        public LocationTrackerStrategy[] newArray(int size)
        {
            return new LocationTrackerStrategy[size];
        }
    };
    @SerializedName("start_date")
    Date mStartDate;
    @SerializedName("end_date")
    Date mEndDate;
    @SerializedName("poll_frequency")
    int mLocationPollingIntervalSeconds; //every N seconds
    @SerializedName("post_frequency")
    int mServerPollingIntervalSeconds; //every N seconds
    @SerializedName("accuracy") //how accurate we want the location updates to be
    int mAccuracy;
    @SerializedName("distance_filter")
    int mDistanceFilterMeters;

    public LocationTrackerStrategy()
    {
    }

    public LocationTrackerStrategy setDistanceFilterMeters(final int distanceFilterMeters)
    {
        mDistanceFilterMeters = distanceFilterMeters;
        return this;
    }

    public int getDistanceFilterMeters()
    {
        return mDistanceFilterMeters;
    }

    public LocationTrackerStrategy setServerPollingIntervalSeconds(final int serverPollingIntervalSeconds)
    {
        mServerPollingIntervalSeconds = serverPollingIntervalSeconds;
        return this;
    }

    protected LocationTrackerStrategy(Parcel in)
    {
        mStartDate = new Date(in.readLong());
        mEndDate = new Date(in.readLong());
        mServerPollingIntervalSeconds = in.readInt();
        mLocationPollingIntervalSeconds = in.readInt();
        mDistanceFilterMeters = in.readInt();
        mAccuracy = in.readInt();
    }

    public int getServerPollingIntervalSeconds()
    {
        return mServerPollingIntervalSeconds;
    }

    public int getAccuracy()
    {
        return mAccuracy;
    }

    public LocationTrackerStrategy setAccuracy(final int accuracy)
    {
        mAccuracy = accuracy;
        return this;
    }

    public int getLocationPollingIntervalSeconds()
    {
        return mLocationPollingIntervalSeconds;
    }

    public LocationTrackerStrategy setLocationPollingIntervalSeconds(final int locationPollingIntervalSeconds)
    {
        mLocationPollingIntervalSeconds = locationPollingIntervalSeconds;
        return this;
    }

    public Date getEndDate()
    {
        return mEndDate;
    }

    public LocationTrackerStrategy setEndDate(final Date endDate)
    {
        mEndDate = endDate;
        return this;
    }

    public Date getStartDate()
    {
        return mStartDate;
    }

    public LocationTrackerStrategy setStartDate(final Date startDate)
    {
        mStartDate = startDate;
        return this;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags)
    {
        dest.writeLong(mStartDate.getTime());
        dest.writeLong(mEndDate.getTime());
        dest.writeInt(mServerPollingIntervalSeconds);
        dest.writeInt(mLocationPollingIntervalSeconds);
        dest.writeInt(mDistanceFilterMeters);
        dest.writeInt(mAccuracy);
    }

    /**
     * for debugging purposes only
     *
     * @return
     */
    @Override
    public String toString()
    {
        return "start date: " + mStartDate.toString()
                + "\nend date: " + mEndDate.toString()
                + "\nserver posting frequency (s): " + mServerPollingIntervalSeconds
                + "\npolling frequency (s): " + mLocationPollingIntervalSeconds
                + "\ndistance filter (m): " + mDistanceFilterMeters
                + "\nlocation accuracy: " + mAccuracy;
    }
}
