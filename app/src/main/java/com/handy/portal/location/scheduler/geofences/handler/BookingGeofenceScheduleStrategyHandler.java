package com.handy.portal.location.scheduler.geofences.handler;

import android.app.PendingIntent;
import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.handy.portal.location.LocationConstants;
import com.handy.portal.location.scheduler.geofences.model.BookingGeofenceStrategy;
import com.handy.portal.location.scheduler.handler.ScheduleStrategyHandler;
import com.handy.portal.util.Utils;

import java.util.LinkedList;
import java.util.List;

public class BookingGeofenceScheduleStrategyHandler extends ScheduleStrategyHandler implements ResultCallback<Status>
{
    private BookingGeofenceStrategy mBookingGeofenceStrategy;
    private BookingGeofenceStrategyCallbacks mBookingGeofenceStrategyCallbacks;

    private Context mContext;
    private Handler mHandler;

    public BookingGeofenceScheduleStrategyHandler(BookingGeofenceStrategy bookingGeofenceStrategy,
                                                  BookingGeofenceStrategyCallbacks bookingGeofenceStrategyCallbacks,
                                                  Handler handler,
                                                  Context context)
    {
        mBookingGeofenceStrategy = bookingGeofenceStrategy;
        mBookingGeofenceStrategyCallbacks = bookingGeofenceStrategyCallbacks;
        mHandler = handler;
        mContext = context;
    }

    @Override
    protected boolean isStrategyExpired()
    {
        return System.currentTimeMillis() - mBookingGeofenceStrategy.getEndDate().getTime() <= 0;
    }

    @Override
    protected void buildStrategyBatchUpdatesAndNotifyReady()
    {
        //do nothing
        //TODO can we structure this better
    }

    @SuppressWarnings({"ResourceType", "MissingPermission"})
    @Override
    protected void startStrategy()
    {
        try
        {
            if(!Utils.areAnyPermissionsGranted(mContext, LocationConstants.LOCATION_PERMISSIONS))
            {
                return;
            }
            GoogleApiClient googleApiClient = mBookingGeofenceStrategyCallbacks.getGoogleApiClient();

            Geofence geofence = buildGeofenceFromStrategy();
            GeofencingRequest geofencingRequest = buildGeofencingRequestFromGeofence(geofence);

            //add the geofence request
            LocationServices.GeofencingApi.addGeofences(googleApiClient,
                    geofencingRequest,
                    mBookingGeofenceStrategyCallbacks.getPendingIntent(mBookingGeofenceStrategy)).setResultCallback(this);
            Log.d(getClass().getName(), "Building geofence for geofence strategy: " + mBookingGeofenceStrategy.toString());

            //todo postdelayed or postattime?
            mHandler.postDelayed(new Runnable() //callback for when strategy expires
            {
                @Override
                public void run()
                {
                    mBookingGeofenceStrategyCallbacks.onStrategyExpired(BookingGeofenceScheduleStrategyHandler.this);
                }
            }, getGeofenceExpirationDurationMs());
            //let the callback know so that we can remove this strategy from the active strategies list and post all pending updates
        }
        catch (Exception e)
        {
            Log.e(getClass().getName(), e.getMessage());
            Crashlytics.logException(e);
        }
    }

    /**
     * gets the duration in ms that this geofence has to be active, starting from the current time
     * @return
     */
    private long getGeofenceExpirationDurationMs()
    {
        return mBookingGeofenceStrategy.getEndDate().getTime() - System.currentTimeMillis();
    }

    private GeofencingRequest buildGeofencingRequestFromGeofence(@NonNull Geofence geofence)
    {
        GeofencingRequest.Builder geofencingRequestBuilder = new GeofencingRequest.Builder();
        geofencingRequestBuilder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        geofencingRequestBuilder.addGeofence(geofence);
        return geofencingRequestBuilder.build();
    }


    private Geofence buildGeofenceFromStrategy()
    {

        return new Geofence.Builder()
                .setRequestId(getGeofenceRequestId())
                .setCircularRegion(mBookingGeofenceStrategy.getLatitude(),
                        mBookingGeofenceStrategy.getLongitude(),
                        mBookingGeofenceStrategy.getRadius())
                .setExpirationDuration(getGeofenceExpirationDurationMs())
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                .build();
    }

    /**
     * the geofence unique request id should be the strategy's associated booking id
     * @return
     */
    private String getGeofenceRequestId()
    {
        return mBookingGeofenceStrategy.getBookingId();
    }

    @Override
    protected void stopStrategy()
    {
        try
        {
            GoogleApiClient googleApiClient = mBookingGeofenceStrategyCallbacks.getGoogleApiClient();
            Log.d(getClass().getName(), "stopping strategy: " + toString());
            List<String> requestIdsList = new LinkedList<>();
            requestIdsList.add(getGeofenceRequestId());
            LocationServices.GeofencingApi.removeGeofences(googleApiClient, requestIdsList);
        }
        catch (Exception e)
        {
            Log.e(getClass().getName(), e.getMessage());
            Crashlytics.logException(e);
        }
    }

    @Override
    public void onResult(final Status status)
    {
        Log.d(getClass().getName(), "got result callback. status: " + status.getStatusCode() + ":" + status.getStatusMessage());
    }

    public interface BookingGeofenceStrategyCallbacks extends ScheduleStrategyHandler.StrategyCallbacks<BookingGeofenceScheduleStrategyHandler>{
        GoogleApiClient getGoogleApiClient();
        PendingIntent getPendingIntent(BookingGeofenceStrategy bookingGeofenceStrategy);
    }
}
