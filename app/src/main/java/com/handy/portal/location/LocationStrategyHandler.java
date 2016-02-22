package com.handy.portal.location;

import android.content.Context;
import android.location.Location;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.handy.portal.location.model.LocationBatchUpdate;
import com.handy.portal.location.model.LocationQueryStrategy;
import com.handy.portal.location.model.LocationUpdate;
import com.handy.portal.util.DateTimeUtils;
import com.handy.portal.util.SystemUtils;

import java.util.LinkedList;
import java.util.Queue;

/**
 * does whatever needs to be done given a location strategy
 *
 * wrapper for objects that are tied to a location query strategy
 * like the location updated listener
 * (but eventually we'll just have one for whole schedule when no overlapping strategies)
 * and the location updates queue
 *
 * <p/>
 * manages when to post updates to the server
 *
 * TODO needs major cleanup
 */
public class LocationStrategyHandler //TODO: rename this so it is more distinct from the location query strategy model
{
    Queue<LocationUpdate> mLocationUpdateQueue = new LinkedList<>();
    long mTimestampLastUpdatePostedMs;
    LocationQueryStrategy mLocationQueryStrategy;
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


    public LocationStrategyHandler(@NonNull LocationQueryStrategy locationQueryStrategy,
                                   @NonNull final LocationStrategyCallbacks locationStrategyCallbacks,
                                   @NonNull Handler handler,
                                   @NonNull Context context)
    {
        mLocationQueryStrategy = locationQueryStrategy;
        mLocationStrategyCallbacks = locationStrategyCallbacks;

        mContext = context;
        mHandler = handler;
        mTimestampLastUpdatePostedMs = System.currentTimeMillis(); //don't want to post immediately
        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(final Location location)
            {
                if(location.getTime() > mLocationQueryStrategy.getEndDate().getTime())
                {
                    Log.i(getClass().getName(), "location request expired but got location changed callback, not doing anything");
                    //would be messy if i unregistered this here, because no reference to required arguments

                    return; //expired
                }
                LocationUpdate locationUpdate = LocationUpdate.from(location, mLocationQueryStrategy);
                locationUpdate.setBatteryLevelPercent(SystemUtils.getBatteryLevelPercent(mContext));
                onNewLocationUpdate(locationUpdate);
            }
        };
    }

    /**
     * convenience method for determining if strategy is expired
     * @return
     */
    public boolean isStrategyExpired()
    {
        return System.currentTimeMillis() > mLocationQueryStrategy.getEndDate().getTime();
    }
    private LocationRequest createLocationRequest()
    {
        int priority = 0;
        switch(mLocationQueryStrategy.getLocationAccuracyPriority())
        {
            case 0:
                priority = LocationRequest.PRIORITY_LOW_POWER;
                break;
            case LocationQueryStrategy.ACCURACY_BALANCED_POWER_PRIORITIY:
                priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY;
                break;
            case LocationQueryStrategy.ACCURACY_HIGH_PRIORITY:
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY;
                break;
            default:
                priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY;
                break;
        }

        long pollingIntervalMs = mLocationQueryStrategy.getLocationPollingIntervalSeconds() * DateTimeUtils.MILLISECONDS_IN_SECOND;
        long expirationDurationMs = mLocationQueryStrategy.getEndDate().getTime() - System.currentTimeMillis();


        LocationRequest locationRequest = new LocationRequest()
                .setSmallestDisplacement(mLocationQueryStrategy.getDistanceFilterMeters())
                .setPriority(priority)
                .setExpirationDuration(expirationDurationMs) //we need to do things like flush the locations once the request expires
                .setInterval(pollingIntervalMs)
                .setFastestInterval(pollingIntervalMs) //TODO: change this, test only
                ;

        return locationRequest;
    }

    public void requestLocationUpdates(GoogleApiClient googleApiClient) throws SecurityException
    {
        Log.i(getClass().getName(), "requesting location updates for " + mLocationQueryStrategy.toString());
        LocationRequest locationRequest = createLocationRequest();
        long expirationTimeMs = locationRequest.getExpirationTime();

        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient,
                createLocationRequest(),
                getLocationListener());

        mHandler.postAtTime(new Runnable() //callback for when strategy expires
        {
            @Override
            public void run()
            {
                mLocationStrategyCallbacks.onLocationStrategyExpired(LocationStrategyHandler.this);
            }
        }, expirationTimeMs);
    }

    public LocationQueryStrategy getLocationQueryStrategy()
    {
        return mLocationQueryStrategy;
    }

    /**
     * builds the batch update request model from the queue
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

    private boolean shouldPostUpdate()
    {
        long serverPollingIntervalMs = mLocationQueryStrategy.getServerPollingIntervalSeconds() * DateTimeUtils.MILLISECONDS_IN_SECOND;
        return (System.currentTimeMillis() - mTimestampLastUpdatePostedMs >= serverPollingIntervalMs);
    }

    /**
     * notifies the ready listener if a batch update should be made
     *
     * @param locationUpdate
     */
    private void onNewLocationUpdate(@NonNull LocationUpdate locationUpdate)
    {
        Log.i(getClass().getName(), "new location update: " + locationUpdate.toString());
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
        if(!locationBatchUpdate.isEmpty())
        {
            mLocationStrategyCallbacks.onLocationBatchUpdateReady(locationBatchUpdate);
        }
    }

    //TODO: test only, will possibly remove
    public interface LocationStrategyCallbacks
    {
        void onLocationStrategyExpired(LocationStrategyHandler locationStrategyHandler);
        void onLocationBatchUpdateReady(LocationBatchUpdate locationBatchUpdate);
    }
}
