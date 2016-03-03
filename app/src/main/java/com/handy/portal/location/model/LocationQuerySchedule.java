package com.handy.portal.location.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

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

    /**
     * DARK. NOT USED RIGHT NOW
     * <p/>
     * just in case the schedule returned from the server is actually the same, don't want to rebuild all the alarms
     * TODO we won't need this anymore when the app is better at detecting when bookings are updated
     *
     * @param locationQuerySchedule
     * @return
     */
    public boolean equals(LocationQuerySchedule locationQuerySchedule)
    {
        if (mLocationQueryStrategies == locationQuerySchedule.getLocationQueryStrategies())
        { return true; }
        if (mLocationQueryStrategies == null || locationQuerySchedule.getLocationQueryStrategies() == null
                || mLocationQueryStrategies.size() != locationQuerySchedule.getLocationQueryStrategies().size())
        {
            return false;
        }

        ListIterator<LocationQueryStrategy> listIterator1 = mLocationQueryStrategies.listIterator();
        ListIterator<LocationQueryStrategy> listIterator2 = locationQuerySchedule.getLocationQueryStrategies().listIterator();
        while (listIterator1.hasNext())
        {
            LocationQueryStrategy locationQueryStrategy1 = listIterator1.next();
            LocationQueryStrategy locationQueryStrategy2 = listIterator2.next();
            if (!locationQueryStrategy1.equals(locationQueryStrategy2))
            {
                return false;
            }
        }

        return true;
    }
}
