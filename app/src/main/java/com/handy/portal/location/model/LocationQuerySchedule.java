package com.handy.portal.location.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.ListIterator;

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

//    public static LocationQuerySchedule fromJson(String jsonString)
//    {
//        if(jsonString == null) return null;
//        Gson gson = new Gson();
//        LocationQuerySchedule locationQuerySchedule = gson.fromJson(jsonString, LocationQuerySchedule.class);
//        return locationQuerySchedule;
//    }
//
//    public final String toJson()
//    {
//        Gson gson = new Gson();
//        return gson.toJson(this);
//    }

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

    /**
     *
     * DARK. NOT USED RIGHT NOW
     *
     * just in case the schedule returned from the server is actually the same, don't want to rebuild all the alarms
     * TODO we won't need this anymore when the app is better at detecting when bookings are updated
     * @param locationQuerySchedule
     * @return
     */
    public boolean equals(LocationQuerySchedule locationQuerySchedule)
    {
        if(mLocationQueryStrategies == locationQuerySchedule.getLocationQueryStrategies()) return true;
        if(mLocationQueryStrategies == null || locationQuerySchedule.getLocationQueryStrategies() == null
                || mLocationQueryStrategies.size()!= locationQuerySchedule.getLocationQueryStrategies().size())
        {
            return false;
        }

        ListIterator<LocationQueryStrategy> listIterator1 = mLocationQueryStrategies.listIterator();
        ListIterator<LocationQueryStrategy> listIterator2 = locationQuerySchedule.getLocationQueryStrategies().listIterator();
        while(listIterator1.hasNext())
        {
            LocationQueryStrategy locationQueryStrategy1 = listIterator1.next();
            LocationQueryStrategy locationQueryStrategy2 = listIterator2.next();
            if(!locationQueryStrategy1.equals(locationQueryStrategy2))
            {
                return false;
            }
        }

        return true;
    }
}
