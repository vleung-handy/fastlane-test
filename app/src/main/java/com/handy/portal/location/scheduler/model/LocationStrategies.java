package com.handy.portal.location.scheduler.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.handy.portal.location.scheduler.geofences.model.BookingGeofenceStrategy;
import com.handy.portal.location.scheduler.tracker.model.LocationTrackerStrategy;

import java.util.LinkedList;

/**
 * model received from the server. contains a list of location strategies
 */
public class LocationStrategies implements Parcelable
{
    /**
     * this should be already sorted by start date
     */
    @SerializedName("location_schedules")
    private LinkedList<LocationTrackerStrategy> mLocationQueryStrategies;

    @SerializedName("booking_geofences")
    private LinkedList<BookingGeofenceStrategy> mBookingGeofenceStrategies;

    @SerializedName("success")
    private boolean mSuccess;

    protected LocationStrategies(Parcel in)
    {
        mLocationQueryStrategies = new LinkedList<>();
        in.readTypedList(mLocationQueryStrategies, LocationTrackerStrategy.CREATOR);

        mBookingGeofenceStrategies = new LinkedList<>();
        in.readTypedList(mBookingGeofenceStrategies, BookingGeofenceStrategy.CREATOR);

    }

    public LinkedList<BookingGeofenceStrategy> getBookingGeofenceStrategies()
    {
        return mBookingGeofenceStrategies;
    }

    public LinkedList<LocationTrackerStrategy> getLocationQueryStrategies()
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
        dest.writeTypedList(mBookingGeofenceStrategies);
    }

    public static final Creator<LocationStrategies> CREATOR = new Creator<LocationStrategies>()
    {
        @Override
        public LocationStrategies createFromParcel(Parcel in)
        {
            return new LocationStrategies(in);
        }

        @Override
        public LocationStrategies[] newArray(int size)
        {
            return new LocationStrategies[size];
        }
    };

    public boolean getSuccess()
    {
        return mSuccess;
    }

    public boolean isEmpty()
    {
        return (mLocationQueryStrategies == null || mLocationQueryStrategies.isEmpty())
                && (mBookingGeofenceStrategies == null || mBookingGeofenceStrategies.isEmpty());
    }

    /**
     * for debugging purposes only
     *
     * @return
     */
    @Override
    public String toString()
    {
        String result = "Location tracker schedules:\n";
        for (LocationTrackerStrategy locationTrackerStrategy : mLocationQueryStrategies)
        {
            result = result + locationTrackerStrategy.toString() + "\n";
        }
        result = result + "Booking geofence schedules:\n";
        for (BookingGeofenceStrategy bookingGeofenceStrategy : mBookingGeofenceStrategies)
        {
            result = result + bookingGeofenceStrategy.toString() + "\n";
        }
        return result;
    }
}
