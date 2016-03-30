package com.handy.portal.location.scheduler.handler;

import android.content.Context;

import com.google.android.gms.common.api.GoogleApiClient;
import com.handy.portal.location.scheduler.geofences.handler.BookingGeofenceScheduleHandler;
import com.handy.portal.location.scheduler.model.LocationScheduleStrategies;
import com.handy.portal.location.scheduler.tracking.handler.LocationTrackingScheduleHandler;

import java.util.LinkedList;
import java.util.List;

/**
 * handles LocationScheduleStrategies
 */
public class LocationScheduleStrategiesHandler
{
    private List<ScheduleHandler> mScheduleHandlerList;
    public LocationScheduleStrategiesHandler(LocationScheduleStrategies locationScheduleStrategies,
                                             boolean locationTrackingEnabled,
                                             boolean bookingGeofencesEnabled,
                                             GoogleApiClient googleApiClient,
                                             Context context)
    {
        mScheduleHandlerList = new LinkedList<>();
        if(bookingGeofencesEnabled)
        {
            mScheduleHandlerList.add(new BookingGeofenceScheduleHandler(
                    locationScheduleStrategies.getBookingGeofenceStrategies(), googleApiClient, context));
        }
        if(locationTrackingEnabled)
        {
            mScheduleHandlerList.add(new LocationTrackingScheduleHandler(
                    locationScheduleStrategies.getLocationTrackingStrategies(), googleApiClient, context));
        }
    }

    public void startIfNotStarted()
    {
        for(ScheduleHandler scheduleHandler : mScheduleHandlerList)
        {
            scheduleHandler.startIfNotStarted();
        }
    }

    public void destroy()
    {
        for(ScheduleHandler scheduleHandler : mScheduleHandlerList)
        {
            scheduleHandler.destroy();
        }
    }

    public void onNetworkReconnected()
    {
        for(ScheduleHandler scheduleHandler : mScheduleHandlerList)
        {
            scheduleHandler.onNetworkReconnected();
        }
    }

}
