package com.handy.portal.location.scheduler.geofences.handler;

import android.app.PendingIntent;
import android.content.Context;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.handy.portal.location.LocationConstants;
import com.handy.portal.location.model.LocationBatchUpdate;
import com.handy.portal.location.scheduler.geofences.model.BookingGeofenceStrategy;
import com.handy.portal.location.scheduler.handler.LocationStrategyHandler;
import com.handy.portal.util.Utils;

/**
 * Created by vleung on 3/16/16.
 */
public class BookingGeofenceStrategyHandler extends LocationStrategyHandler implements ResultCallback<Status>
{
    private BookingGeofenceStrategy mBookingGeofenceStrategy;
    private BookingGeofenceStrategyCallbacks mBookingGeofenceStrategyCallbacks;
    private Context mContext;
    public BookingGeofenceStrategyHandler(BookingGeofenceStrategy bookingGeofenceStrategy,
                                          BookingGeofenceStrategyCallbacks bookingGeofenceStrategyCallbacks,
                                          Context context)
    {
        mBookingGeofenceStrategy = bookingGeofenceStrategy;
        mBookingGeofenceStrategyCallbacks = bookingGeofenceStrategyCallbacks;
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
    protected void buildBatchUpdateAndNotifyReady()
    {
        //build the batch update
        LocationBatchUpdate locationBatchUpdate = buildLocationBatchUpdate();
        mBookingGeofenceStrategyCallbacks.onLocationBatchUpdateReady(locationBatchUpdate);
    }

    private LocationBatchUpdate buildLocationBatchUpdate()
    {
        return null; //TODO
    }

    @SuppressWarnings({"ResourceType", "MissingPermission"})
    @Override
    protected void requestUpdates(final GoogleApiClient googleApiClient)
    {
        if(!Utils.areAnyPermissionsGranted(mContext, LocationConstants.LOCATION_PERMISSIONS))
        {
            return;
        }
        long expirationDurationMs = mBookingGeofenceStrategy.getEndDate().getTime() - System.currentTimeMillis();
        Geofence geofence = new Geofence.Builder()
                .setRequestId(mBookingGeofenceStrategy.getBookingId())
//                .setCircularRegion(mBookingGeofenceStrategy.getLatitude(),
//                        mBookingGeofenceStrategy.getLongitude(),
//                        mBookingGeofenceStrategy.getRadius())
                .setCircularRegion(40.740402, -73.993034, 10000)
                .setExpirationDuration(expirationDurationMs)
                .setNotificationResponsiveness(1) //TODO make this much higher. test only
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT | Geofence.GEOFENCE_TRANSITION_DWELL)
                .setLoiteringDelay(1000)
                .build();
        GeofencingRequest.Builder geofencingRequestBuilder = new GeofencingRequest.Builder();
        geofencingRequestBuilder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER
        | GeofencingRequest.INITIAL_TRIGGER_DWELL | GeofencingRequest.INITIAL_TRIGGER_EXIT); //todo TEST ONLY
        geofencingRequestBuilder.addGeofence(geofence);
        LocationServices.GeofencingApi.addGeofences(googleApiClient,
                geofencingRequestBuilder.build(),
                mBookingGeofenceStrategyCallbacks.getPendingIntent(mBookingGeofenceStrategy)).setResultCallback(this);
        Log.d(getClass().getName(), "Building geofence for geofence strategy: " + mBookingGeofenceStrategy.toString());
    }

    @Override
    public void onResult(final Status status)
    {
        Log.d(getClass().getName(), "got result callback. status: " + status.getStatusCode() + ":" + status.getStatusMessage());
    }

    public interface BookingGeofenceStrategyCallbacks {
        void onLocationBatchUpdateReady(LocationBatchUpdate locationBatchUpdate);
        PendingIntent getPendingIntent(BookingGeofenceStrategy bookingGeofenceStrategy);
    }


}
