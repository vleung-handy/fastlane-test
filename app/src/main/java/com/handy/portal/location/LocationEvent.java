package com.handy.portal.location;

import android.location.Location;
import android.support.annotation.NonNull;

import com.handy.portal.data.DataManager;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.location.model.LocationBatchUpdate;
import com.handy.portal.location.scheduler.model.LocationStrategies;

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


    public static class ReceiveLocationScheduleSuccess extends HandyEvent.ReceiveSuccessEvent
    {
        private final LocationStrategies mLocationStrategies;

        public ReceiveLocationScheduleSuccess(@NonNull LocationStrategies locationStrategies)
        {
            mLocationStrategies = locationStrategies;
        }

        public LocationStrategies getLocationStrategies()
        {
            return mLocationStrategies;
        }
    }

    public static class ReceiveLocationScheduleError extends HandyEvent.ReceiveErrorEvent
    {
        public ReceiveLocationScheduleError(DataManager.DataManagerError error)
        {
            this.error = error;
        }
    }


    public static class RequestLocationSchedule extends HandyEvent.RequestEvent
    {
    }
}
