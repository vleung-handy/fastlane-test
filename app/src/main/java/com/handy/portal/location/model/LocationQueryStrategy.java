package com.handy.portal.location.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.crashlytics.android.Crashlytics;
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

    public int getDistanceFilterMeters()
    {
        return mDistanceFilterMeters;
    }

    protected LocationQueryStrategy(Parcel in)
    {
        mStartDate = new Date(in.readLong());
        mEndDate = new Date(in.readLong());
        mServerPollingIntervalSeconds = in.readInt();
        mLocationPollingIntervalSeconds = in.readInt();
        mDistanceFilterMeters = in.readInt();
        mAccuracy = in.readInt();

        //logging this for now to rule out parcel reading errors
        Crashlytics.log("Created location query strategy from parcel: \n" + toString());
    }

    public int getServerPollingIntervalSeconds()
    {
        return mServerPollingIntervalSeconds;
    }

    public int getAccuracy()
    {
        return mAccuracy;
    }

    public int getLocationPollingIntervalSeconds()
    {
        return mLocationPollingIntervalSeconds;
    }

    public Date getEndDate()
    {
        return mEndDate;
    }

    public Date getStartDate()
    {
        return mStartDate;
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
