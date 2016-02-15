package com.handy.portal.location.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.handy.portal.util.DateTimeUtils;

import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;

/**
 * model received from the server. contains a list of location strategies
 */
public class LocationQuerySchedule implements Parcelable
{

    public LocationQuerySchedule(final LinkedList<LocationQueryStrategy> locationQueryStrategies)
    {
        mLocationQueryStrategies = locationQueryStrategies;
    }

    /**
     * TODO: ensure this is sorted by start date
     */
    @SerializedName("location_query_strategies")
    LinkedList<LocationQueryStrategy> mLocationQueryStrategies;

    public LocationQuerySchedule()
    {
        //TODO: remove this eventually, FOR TESTING ONLY
        Date date = new Date();
        Date date2 = new Date();
        date2.setTime(date2.getTime() + DateTimeUtils.MILLISECONDS_IN_SECOND * 10);
        Date date3 = new Date();
        date3.setTime(date3.getTime() + DateTimeUtils.MILLISECONDS_IN_SECOND * 20);
        Date date4 = new Date();
        date4.setTime(date4.getTime() + DateTimeUtils.MILLISECONDS_IN_30_MINS * 3);
        LocationQueryStrategy locationQueryStrategies[] =
                new LocationQueryStrategy[]{
                        new LocationQueryStrategy("1", date, 2, 1, 60, 1),
                        new LocationQueryStrategy("2", date2, 60, 30, 120, 2),
//                        new LocationQueryStrategy(date3, 1, 1, 2),
//                        new LocationQueryStrategy(date4, 30, 1, 2)
                };
        mLocationQueryStrategies = new LinkedList<>(Arrays.asList(locationQueryStrategies));
    }


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
        dest.writeTypedArray(mLocationQueryStrategies.toArray(new LocationQueryStrategy[]{}), flags);
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
