package com.handy.portal.location.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.Arrays;
import java.util.LinkedList;

/**
 * model received from the server. contains a list of location strategies
 */
public class LocationQuerySchedule implements Parcelable
{

    //TODO: move
    public final static String EXTRA_LOCATION_SCHEDULE = "location_query_schedule";
    public LocationQuerySchedule(final LinkedList<LocationQueryStrategy> locationQueryStrategies)
    {
        mLocationQueryStrategies = locationQueryStrategies;
    }

    /**
     * TODO: ensure this is sorted by start date
     */
    @SerializedName("location_query_strategies")
    LinkedList<LocationQueryStrategy> mLocationQueryStrategies;

    protected LocationQuerySchedule(Parcel in)
    {
        mLocationQueryStrategies = new LinkedList<>(Arrays.asList(in.createTypedArray(LocationQueryStrategy.CREATOR)));
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
        dest.writeTypedArray(mLocationQueryStrategies.toArray(new LocationQueryStrategy[mLocationQueryStrategies.size()]), flags);
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

    public static LocationQuerySchedule fromJson(String jsonString)
    {
        if(jsonString == null) return null;
        Gson gson = new Gson();
        LocationQuerySchedule locationQuerySchedule = gson.fromJson(jsonString, LocationQuerySchedule.class);
        return locationQuerySchedule;
    }

    public final String toJson()
    {
        Gson gson = new Gson();
        return gson.toJson(this);
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
