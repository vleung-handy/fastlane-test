package com.handy.portal.location.scheduler.handler;

import android.content.Context;

import com.google.android.gms.common.api.GoogleApiClient;
import com.handy.portal.location.scheduler.geofences.handler.BookingGeofenceScheduleHandler;
import com.handy.portal.location.scheduler.model.LocationStrategies;
import com.handy.portal.location.scheduler.tracker.handler.LocationTrackerScheduleHandler;

import java.util.LinkedList;
import java.util.List;

public class LocationStrategiesHandler
{
    List<LocationScheduleHandler> mLocationScheduleHandlerList;
    public LocationStrategiesHandler(LocationStrategies locationStrategies,
                                     boolean locationTrackingEnabled,
                                     boolean bookingGeofencesEnabled,
                                     GoogleApiClient googleApiClient,
                                     Context context)
    {
        mLocationScheduleHandlerList = new LinkedList<>();
        if(bookingGeofencesEnabled)
        {
            mLocationScheduleHandlerList.add(new BookingGeofenceScheduleHandler(
                    locationStrategies.getBookingGeofenceStrategies(), googleApiClient, context));
        }
        if(locationTrackingEnabled)
        {
            mLocationScheduleHandlerList.add(new LocationTrackerScheduleHandler(
                    locationStrategies.getLocationQueryStrategies(), googleApiClient, context));
        }
    }

    public void start()
    {
        for(LocationScheduleHandler locationScheduleHandler : mLocationScheduleHandlerList)
        {
            locationScheduleHandler.start();
        }
    }

    public void destroy()
    {
        for(LocationScheduleHandler locationScheduleHandler : mLocationScheduleHandlerList)
        {
            locationScheduleHandler.destroy();
        }
    }

    public void onNetworkReconnected()
    {
        for(LocationScheduleHandler locationScheduleHandler : mLocationScheduleHandlerList)
        {
            locationScheduleHandler.onNetworkReconnected();
        }
    }

}
