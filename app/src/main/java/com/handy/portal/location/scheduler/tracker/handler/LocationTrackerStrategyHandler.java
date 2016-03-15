package com.handy.portal.location.scheduler.tracker.handler;

import android.content.Context;
import android.location.Location;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.handy.portal.location.LocationConstants;
import com.handy.portal.location.model.LocationBatchUpdate;
import com.handy.portal.location.model.LocationUpdate;
import com.handy.portal.location.scheduler.handler.LocationStrategyHandler;
import com.handy.portal.location.scheduler.tracker.model.LocationTrackerStrategy;
import com.handy.portal.util.DateTimeUtils;
import com.handy.portal.util.SystemUtils;
import com.handy.portal.util.Utils;

import java.util.LinkedList;
import java.util.Queue;

/**
 * does whatever needs to be done given a location strategy
 * <p/>
 * wrapper for objects that are tied to a location query strategy
 * like the location updated listener
 * (but eventually we'll just have one for whole schedule when no overlapping strategies)
 * and the location updates queue
 * <p/>
 * <p/>
 * manages when to post updates to the server
 * <p/>
 * TODO needs major cleanup
 */
public class LocationTrackerStrategyHandler extends LocationStrategyHandler //TODO: rename this so it is more distinct from the location query strategy model
{
    /**
     * any strategy that wants accuracy equal to or below this amount
     * will use the high accuracy mode in LocationApi
     *
     * otherwise it will use balanced power mode
     */
    public static final int HIGH_ACCURACY_THRESHOLD_METERS = 100;

    Queue<LocationUpdate> mLocationUpdateQueue = new LinkedList<>();
    long mTimestampLastUpdatePostedMs;
    LocationTrackerStrategy mLocationTrackerStrategy;
    LocationStrategyCallbacks mLocationStrategyCallbacks;
    Context mContext;

    /**
     * this listener is tied to the lifecycle of the strategy and gets location update callbacks
     */
    LocationListener mLocationListener;
    Handler mHandler;

    public LocationListener getLocationListener()
    {
        return mLocationListener;
    }


    public LocationTrackerStrategyHandler(@NonNull LocationTrackerStrategy locationTrackerStrategy,
                                          @NonNull final LocationStrategyCallbacks locationStrategyCallbacks,
                                          @NonNull Handler handler,
                                          @NonNull Context context)
    {
        mLocationTrackerStrategy = locationTrackerStrategy;
        mLocationStrategyCallbacks = locationStrategyCallbacks;

        mContext = context;
        mHandler = handler;
        mTimestampLastUpdatePostedMs = System.currentTimeMillis(); //don't want to post immediately
        mLocationListener = new LocationListener()
        {
            @Override
            public void onLocationChanged(final Location location)
            {
                //TODO: use a util instead
                if (location.getTime() > mLocationTrackerStrategy.getEndDate().getTime())
                {
                    Log.d(getClass().getName(), "location request expired but got location changed callback, not doing anything");
                    //would be messy if i unregistered this here, because no reference to required arguments

                    return; //expired
                }
                mLocationStrategyCallbacks.onLocationUpdate(location);
                LocationUpdate locationUpdate = LocationUpdate.from(location);
                locationUpdate.setBatteryLevelPercent(SystemUtils.getBatteryLevelPercent(mContext));
                locationUpdate.setActiveNetworkType(SystemUtils.getActiveNetworkType(mContext));
                onNewLocationUpdate(locationUpdate);
            }
        };
    }

    /**
     * convenience method for determining if strategy is expired
     *
     * @return
     */
    public boolean isStrategyExpired()
    {
        //TODO use a util instead
        return System.currentTimeMillis() > mLocationTrackerStrategy.getEndDate().getTime();
    }

    /**
     * builds a location request for the location api call
     *
     * @return
     */
    private LocationRequest createLocationRequest()
    {
        int priority;
        if(mLocationTrackerStrategy.getAccuracy() <= HIGH_ACCURACY_THRESHOLD_METERS)
        {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY;
        }
        else
        {
            priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY;
        }

        long pollingIntervalMs = mLocationTrackerStrategy.getLocationPollingIntervalSeconds() * DateTimeUtils.MILLISECONDS_IN_SECOND;
        long expirationDurationMs = mLocationTrackerStrategy.getEndDate().getTime() - System.currentTimeMillis();

        LocationRequest locationRequest = new LocationRequest()
                .setSmallestDisplacement(mLocationTrackerStrategy.getDistanceFilterMeters())
                .setPriority(priority)
                .setExpirationDuration(expirationDurationMs)
                .setInterval(pollingIntervalMs)
                .setFastestInterval(pollingIntervalMs) //TODO: consider changing this
                ;

        return locationRequest;
    }

    /**
     * asks the location api for location updates
     *
     * @param googleApiClient
     * @throws SecurityException
     */
    @SuppressWarnings({"ResourceType", "MissingPermission"})
    public void requestLocationUpdates(GoogleApiClient googleApiClient)
    {
        //this handles the permission system in Android 6.0
        if (!Utils.areAnyPermissionsGranted(mContext, LocationConstants.LOCATION_PERMISSIONS))
        {
            return;
        }

        Log.d(getClass().getName(), "requesting location updates for " + mLocationTrackerStrategy.toString());
        LocationRequest locationRequest = createLocationRequest();
        long expirationTimeMs = locationRequest.getExpirationTime();

        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient,
                createLocationRequest(),
                mLocationListener);

        mHandler.postAtTime(new Runnable() //callback for when strategy expires
        {
            @Override
            public void run()
            {
                mLocationStrategyCallbacks.onLocationStrategyExpired(LocationTrackerStrategyHandler.this);
            }
        }, expirationTimeMs);
    }

    public LocationTrackerStrategy getLocationTrackerStrategy()
    {
        return mLocationTrackerStrategy;
    }

    /**
     * builds the batch update request model from the queue of updates
     * <p/>
     * this is what will be posted to the server
     *
     * @return
     */
    private LocationBatchUpdate buildLocationBatchUpdateAndClearQueue()
    {
        LocationUpdate locationUpdates[] = mLocationUpdateQueue
                .toArray(new LocationUpdate[mLocationUpdateQueue.size()]);
        //.size() is constant time for linked lists

        LocationBatchUpdate locationBatchUpdate = new LocationBatchUpdate(locationUpdates);
        mLocationUpdateQueue.clear();
        return locationBatchUpdate;
    }

    /**
     * check if it's time to post an update to the server
     *
     * @return
     */
    private boolean shouldPostUpdate()
    {
        //TODO use a util instead
        long serverPollingIntervalMs = mLocationTrackerStrategy.getServerPollingIntervalSeconds() * DateTimeUtils.MILLISECONDS_IN_SECOND;
        return (System.currentTimeMillis() - mTimestampLastUpdatePostedMs >= serverPollingIntervalMs);
    }

    /**
     * notifies the ready listener if a batch update should be made
     *
     * @param locationUpdate
     */
    private void onNewLocationUpdate(@NonNull LocationUpdate locationUpdate)
    {
        Log.d(getClass().getName(), "new location update: " + locationUpdate.toString());
        mLocationUpdateQueue.add(locationUpdate);
        if (shouldPostUpdate())
        {
            buildBatchUpdateAndNotifyReady();
        }
    }

    /**
     * called when it's time to post the location updates to the server
     */
    public void buildBatchUpdateAndNotifyReady()
    {
        LocationBatchUpdate locationBatchUpdate = buildLocationBatchUpdateAndClearQueue();
        mTimestampLastUpdatePostedMs = System.currentTimeMillis();
        if (!locationBatchUpdate.isEmpty())
        {
            mLocationStrategyCallbacks.onLocationBatchUpdateReady(locationBatchUpdate);
        }
    }

    @Override
    protected void requestUpdates(final GoogleApiClient googleApiClient)
    {
        requestLocationUpdates(googleApiClient);
    }

    public interface LocationStrategyCallbacks
    {
        void onLocationStrategyExpired(LocationTrackerStrategyHandler locationTrackerStrategyHandler);

        void onLocationBatchUpdateReady(LocationBatchUpdate locationBatchUpdate);

        void onLocationUpdate(Location locationUpdate);
        //want to notify the manager of updates so it can keep track of last location, for backwards compatibility with current check-in flow.
        //not sure if i want to keep this
    }
}
