package com.handy.portal.location.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

//TODO: at testing stage. not finalized, needs lots of refactoring


/**
 * part of the LocationQuerySchedule model received from the server
 * <p/>
 * the location service uses this to determine when and at what accuracy we should get location updates
 */
public class LocationQueryStrategy implements Parcelable
{
    public static final Creator<LocationQueryStrategy> CREATOR = new Creator<LocationQueryStrategy>()
    {
        @Override
        public LocationQueryStrategy createFromParcel(Parcel in)
        {
            return new LocationQueryStrategy(in);
        }

        @Override
        public LocationQueryStrategy[] newArray(int size)
        {
            return new LocationQueryStrategy[size];
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

    public LocationQueryStrategy()
    {
    }

    public LocationQueryStrategy setDistanceFilterMeters(final int distanceFilterMeters)
    {
        mDistanceFilterMeters = distanceFilterMeters;
        return this;
    }

    public int getDistanceFilterMeters()
    {
        return mDistanceFilterMeters;
    }

    public LocationQueryStrategy setServerPollingIntervalSeconds(final int serverPollingIntervalSeconds)
    {
        mServerPollingIntervalSeconds = serverPollingIntervalSeconds;
        return this;
    }

    protected LocationQueryStrategy(Parcel in)
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

    public LocationQueryStrategy setAccuracy(final int accuracy)
    {
        mAccuracy = accuracy;
        return this;
    }

    public int getLocationPollingIntervalSeconds()
    {
        return mLocationPollingIntervalSeconds;
    }

    public LocationQueryStrategy setLocationPollingIntervalSeconds(final int locationPollingIntervalSeconds)
    {
        mLocationPollingIntervalSeconds = locationPollingIntervalSeconds;
        return this;
    }

    public Date getEndDate()
    {
        return mEndDate;
    }

    public LocationQueryStrategy setEndDate(final Date endDate)
    {
        mEndDate = endDate;
        return this;
    }

    public Date getStartDate()
    {
        return mStartDate;
    }

    public LocationQueryStrategy setStartDate(final Date startDate)
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
