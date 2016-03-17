package com.handy.portal.location.scheduler.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.handy.portal.location.scheduler.geofences.model.BookingGeofenceStrategy;
import com.handy.portal.location.scheduler.tracking.model.LocationTrackingScheduleStrategy;

import java.util.LinkedList;

/**
 * model received from the server. contains location schedules that should be sorted by start date
 */
public class LocationScheduleStrategies implements Parcelable
{
    @SerializedName("location_schedules")
    private LinkedList<LocationTrackingScheduleStrategy> mLocationTrackingStrategies;

    @SerializedName("booking_geofences")
    private LinkedList<BookingGeofenceStrategy> mBookingGeofenceStrategies;

    @SerializedName("success")
    private boolean mSuccess;

    protected LocationScheduleStrategies(Parcel in)
    {
        mLocationTrackingStrategies = new LinkedList<>();
        in.readTypedList(mLocationTrackingStrategies, LocationTrackingScheduleStrategy.CREATOR);

        mBookingGeofenceStrategies = new LinkedList<>();
        in.readTypedList(mBookingGeofenceStrategies, BookingGeofenceStrategy.CREATOR);

    }

    public LinkedList<BookingGeofenceStrategy> getBookingGeofenceStrategies()
    {
        return mBookingGeofenceStrategies;
    }

    public LinkedList<LocationTrackingScheduleStrategy> getLocationTrackingStrategies()
    {
        return mLocationTrackingStrategies;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags)
    {
        dest.writeTypedList(mLocationTrackingStrategies);
        dest.writeTypedList(mBookingGeofenceStrategies);
    }

    public static final Creator<LocationScheduleStrategies> CREATOR = new Creator<LocationScheduleStrategies>()
    {
        @Override
        public LocationScheduleStrategies createFromParcel(Parcel in)
        {
            return new LocationScheduleStrategies(in);
        }

        @Override
        public LocationScheduleStrategies[] newArray(int size)
        {
            return new LocationScheduleStrategies[size];
        }
    };

    public boolean getSuccess()
    {
        return mSuccess;
    }

    public boolean isEmpty()
    {
        return (mLocationTrackingStrategies == null || mLocationTrackingStrategies.isEmpty())
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
        for (LocationTrackingScheduleStrategy locationTrackerStrategy : mLocationTrackingStrategies)
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
