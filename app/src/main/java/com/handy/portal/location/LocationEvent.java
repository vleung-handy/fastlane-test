package com.handy.portal.location;

import android.location.Location;
import android.support.annotation.NonNull;

import com.handy.portal.event.HandyEvent;
import com.handy.portal.location.model.LocationBatchUpdate;
import com.handy.portal.location.model.LocationQuerySchedule;

/**
 * events used by the bus
 */
public abstract class LocationEvent
{
    public static class SendGeolocationRequest extends HandyEvent.RequestEvent
    {
        private final LocationBatchUpdate mLocationBatchUpdate;

        public SendGeolocationRequest(LocationBatchUpdate locationBatchUpdate)
        {
            mLocationBatchUpdate = locationBatchUpdate;
        }

        public LocationBatchUpdate getLocationBatchUpdate()
        {
            return mLocationBatchUpdate;
        }
    }


    public static class LocationUpdated extends HandyEvent
    {
        private final Location mLocationUpdate;

        public LocationUpdated(final Location locationUpdate)
        {
            mLocationUpdate = locationUpdate;
        }

        public Location getLocationUpdate()
        {
            return mLocationUpdate;
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
