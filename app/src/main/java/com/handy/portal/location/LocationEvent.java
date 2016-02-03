package com.handy.portal.location;

import android.location.Location;
import android.support.annotation.NonNull;

import com.handy.portal.event.HandyEvent;
import com.handy.portal.location.model.LocationQuerySchedule;

/**
 * events used by the bus
 */
public abstract class LocationEvent
{
    public static class LocationChanged extends HandyEvent.RequestEvent
    {
        private final Location mLocation;

        public LocationChanged(Location location)
        {
            mLocation = location;
        }
        public Location getLocation()
        {
            return mLocation;
        }
    }

    public static class ReceiveLocationSchedule extends HandyEvent.ReceiveSuccessEvent
    {
        private final LocationQuerySchedule mLocationQuerySchedule;
        public ReceiveLocationSchedule(@NonNull LocationQuerySchedule locationQuerySchedule)
        {
            mLocationQuerySchedule = locationQuerySchedule;
        }

        public LocationQuerySchedule getLocationQuerySchedule()
        {
            return mLocationQuerySchedule;
        }
    }

    public static class RequestLocationSchedule extends HandyEvent.RequestEvent
    {
    }
}
