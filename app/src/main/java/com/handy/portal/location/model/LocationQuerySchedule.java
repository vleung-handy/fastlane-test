package com.handy.portal.location.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.LinkedList;

/**
 * model received from the server. contains a list of location strategies
 */
public class LocationQuerySchedule implements Parcelable
{
    /**
     * this should be already sorted by start date
     */
    @SerializedName("location_schedules")
    private LinkedList<LocationQueryStrategy> mLocationQueryStrategies;

    @SerializedName("success")
    private boolean mSuccess;

    protected LocationQuerySchedule(Parcel in)
    {
        mLocationQueryStrategies = new LinkedList<>();
        in.readTypedList(mLocationQueryStrategies, LocationQueryStrategy.CREATOR);
    }

    public LinkedList<LocationQueryStrategy> getLocationQueryStrategies()
    {
        return mLocationQueryStrategies;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags)
    {
        dest.writeTypedList(mLocationQueryStrategies);
    }

    public static final Creator<LocationQuerySchedule> CREATOR = new Creator<LocationQuerySchedule>()
    {
        @Override
        public LocationQuerySchedule createFromParcel(Parcel in)
        {
            return new LocationQuerySchedule(in);
        }

        @Override
        public LocationQuerySchedule[] newArray(int size)
        {
            return new LocationQuerySchedule[size];
        }
    };

    public boolean getSuccess()
    {
        return mSuccess;
    }

    public boolean isEmpty()
    {
        return mLocationQueryStrategies == null || mLocationQueryStrategies.isEmpty();
    }

    /**
     * for debugging purposes only
     *
     * @return
     */
    @Override
    public String toString()
    {
        String result = "";
        for (LocationQueryStrategy locationQueryStrategy : mLocationQueryStrategies)
        {
            result = result + locationQueryStrategy.toString() + "\n";
        }
        return result;
    }
}
