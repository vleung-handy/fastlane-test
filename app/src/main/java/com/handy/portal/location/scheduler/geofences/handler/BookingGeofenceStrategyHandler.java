package com.handy.portal.location.scheduler.geofences.handler;

import android.app.PendingIntent;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.handy.portal.location.LocationConstants;
import com.handy.portal.location.scheduler.geofences.model.BookingGeofenceStrategy;
import com.handy.portal.location.scheduler.handler.StrategyHandler;
import com.handy.portal.util.Utils;

import java.util.LinkedList;
import java.util.List;

public class BookingGeofenceStrategyHandler extends StrategyHandler implements ResultCallback<Status>
{
    private BookingGeofenceStrategy mBookingGeofenceStrategy;
    private BookingGeofenceStrategyCallbacks mBookingGeofenceStrategyCallbacks;

    private Context mContext;
    private Handler mHandler;

    public BookingGeofenceStrategyHandler(BookingGeofenceStrategy bookingGeofenceStrategy,
                                          BookingGeofenceStrategyCallbacks bookingGeofenceStrategyCallbacks,
                                          Handler handler,
                                          Context context)
    {
        mBookingGeofenceStrategy = bookingGeofenceStrategy;
        mBookingGeofenceStrategyCallbacks = bookingGeofenceStrategyCallbacks;
        mHandler = handler;
        mContext = context;
    }

    public BookingGeofenceStrategy getBookingGeofenceStrategy()
    {
        return mBookingGeofenceStrategy;
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
        if(!Utils.areAnyPermissionsGranted(mContext, LocationConstants.LOCATION_PERMISSIONS))
        {
            return;
        }
        GoogleApiClient googleApiClient = mBookingGeofenceStrategyCallbacks.getGoogleApiClient();
        long expirationDurationMs = mBookingGeofenceStrategy.getEndDate().getTime() - System.currentTimeMillis();
        Geofence geofence = new Geofence.Builder()
                .setRequestId(mBookingGeofenceStrategy.getBookingId())
                .setCircularRegion(mBookingGeofenceStrategy.getLatitude(),
                        mBookingGeofenceStrategy.getLongitude(),
                        mBookingGeofenceStrategy.getRadius())
//                .setCircularRegion(40.740402, -73.993034, 10000) //TODO test only, remove
                .setExpirationDuration(expirationDurationMs)
                .setNotificationResponsiveness(1) //TODO make this much higher. test only
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                .build();
        GeofencingRequest.Builder geofencingRequestBuilder = new GeofencingRequest.Builder();
        geofencingRequestBuilder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER); //todo TEST ONLY
        geofencingRequestBuilder.addGeofence(geofence);

        GeofencingRequest geofencingRequest = geofencingRequestBuilder.build();
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
                mBookingGeofenceStrategyCallbacks.onStrategyExpired(BookingGeofenceStrategyHandler.this);
            }
        }, expirationDurationMs);
    }

    @Override
    protected void stopStrategy()
    {
        GoogleApiClient googleApiClient = mBookingGeofenceStrategyCallbacks.getGoogleApiClient();
        Log.d(getClass().getName(), "stopping strategy: " + toString());
        LocationServices.GeofencingApi.removeGeofences(googleApiClient, mBookingGeofenceStrategyCallbacks.getPendingIntent(mBookingGeofenceStrategy));
        List<String> requestIdsList = new LinkedList<>();
        requestIdsList.add(mBookingGeofenceStrategy.getBookingId());
        LocationServices.GeofencingApi.removeGeofences(googleApiClient, requestIdsList);
    }

    @Override
    public void onResult(final Status status)
    {
        Log.d(getClass().getName(), "got result callback. status: " + status.getStatusCode() + ":" + status.getStatusMessage());
    }

    public interface BookingGeofenceStrategyCallbacks extends StrategyHandler.StrategyCallbacks<BookingGeofenceStrategyHandler>{
        GoogleApiClient getGoogleApiClient();
        PendingIntent getPendingIntent(BookingGeofenceStrategy bookingGeofenceStrategy);
    }


}
