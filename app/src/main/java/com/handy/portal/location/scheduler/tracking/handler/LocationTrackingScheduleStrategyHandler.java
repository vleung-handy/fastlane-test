package com.handy.portal.location.scheduler.tracking.handler;

import android.content.Context;
import android.location.Location;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.text.format.DateUtils;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.handy.portal.location.LocationConstants;
import com.handy.portal.location.model.LocationBatchUpdate;
import com.handy.portal.location.model.LocationUpdate;
import com.handy.portal.location.scheduler.handler.ScheduleStrategyHandler;
import com.handy.portal.location.scheduler.tracking.model.LocationTrackingScheduleStrategy;
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
public class LocationTrackingScheduleStrategyHandler extends ScheduleStrategyHandler
{
    /**
     * any strategy that wants accuracy equal to or below this amount
     * will use the high accuracy mode in LocationApi
     *
     * otherwise it will use balanced power mode
     */
    public static final int HIGH_ACCURACY_THRESHOLD_METERS = 100;

    private Queue<LocationUpdate> mLocationUpdateQueue = new LinkedList<>();
    private long mTimestampLastUpdatePostedMs;
    private LocationTrackingScheduleStrategy mLocationTrackingStrategy;
    private LocationStrategyCallbacks mLocationStrategyCallbacks;
    private Context mContext;

    /**
     * this listener is tied to the lifecycle of the strategy and gets location update callbacks
     */
    private LocationListener mLocationListener;
    private Handler mHandler;

    public LocationListener getLocationListener()
    {
        return mLocationListener;
    }


    public LocationTrackingScheduleStrategyHandler(@NonNull LocationTrackingScheduleStrategy locationTrackingStrategy,
                                                   @NonNull final LocationStrategyCallbacks locationStrategyCallbacks,
                                                   @NonNull Handler handler,
                                                   @NonNull Context context)
    {
        mLocationTrackingStrategy = locationTrackingStrategy;
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
                if (location.getTime() > mLocationTrackingStrategy.getEndDate().getTime())
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
        return System.currentTimeMillis() > mLocationTrackingStrategy.getEndDate().getTime();
    }

    /**
     * builds a location request for the location api call
     *
     * @return
     */
    private LocationRequest createLocationRequest()
    {
        int priority;
        if(mLocationTrackingStrategy.getAccuracy() <= HIGH_ACCURACY_THRESHOLD_METERS)
        {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY;
        }
        else
        {
            priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY;
        }

        long pollingIntervalMs = mLocationTrackingStrategy.getLocationPollingIntervalSeconds() * DateUtils.SECOND_IN_MILLIS;
        long expirationDurationMs = mLocationTrackingStrategy.getEndDate().getTime() - System.currentTimeMillis();

        LocationRequest locationRequest = new LocationRequest()
                .setSmallestDisplacement(mLocationTrackingStrategy.getDistanceFilterMeters())
                .setPriority(priority)
                .setExpirationDuration(expirationDurationMs)
                .setInterval(pollingIntervalMs)
                .setFastestInterval(pollingIntervalMs) //TODO: consider changing this
                ;

        return locationRequest;
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
        long serverPollingIntervalMs = mLocationTrackingStrategy.getServerPollingIntervalSeconds() * DateUtils.SECOND_IN_MILLIS;
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
            buildStrategyBatchUpdatesAndNotifyReady();
        }
    }

    /**
     * called when it's time to post the location updates to the server
     */
    public void buildStrategyBatchUpdatesAndNotifyReady()
    {
        LocationBatchUpdate locationBatchUpdate = buildLocationBatchUpdateAndClearQueue();
        mTimestampLastUpdatePostedMs = System.currentTimeMillis();
        if (!locationBatchUpdate.isEmpty())
        {
            mLocationStrategyCallbacks.onLocationBatchUpdateReady(locationBatchUpdate);
        }
    }

    /**
     * asks the location api for location updates
     **/
    @SuppressWarnings({"ResourceType", "MissingPermission"})
    @Override
    protected void startStrategy()
    {
        try
        {
            GoogleApiClient googleApiClient = mLocationStrategyCallbacks.getGoogleApiClient();
            //this handles the permission system in Android 6.0
            if (!Utils.areAllPermissionsGranted(mContext, LocationConstants.LOCATION_PERMISSIONS))
            {
                return;
            }

            Log.d(getClass().getName(), "requesting location updates for " + mLocationTrackingStrategy.toString());
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
                    mLocationStrategyCallbacks.onStrategyExpired(LocationTrackingScheduleStrategyHandler.this);
                }
            }, expirationTimeMs);
            //let the callback know so that we can remove this strategy from the active strategies list and post all pending updates
        }
        catch (Exception e)
        {
            Log.e(getClass().getName(), e.getMessage());
            Crashlytics.logException(e);
        }

    }

    @Override
    protected void stopStrategy()
    {
        try
        {
            GoogleApiClient googleApiClient = mLocationStrategyCallbacks.getGoogleApiClient();
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, getLocationListener());
        }
        catch (Exception e)
        {
            Log.e(getClass().getName(), e.getMessage());
            Crashlytics.logException(e);
        }
    }

    public interface LocationStrategyCallbacks extends ScheduleStrategyHandler.StrategyCallbacks<LocationTrackingScheduleStrategyHandler>
    {
        GoogleApiClient getGoogleApiClient();

        void onLocationBatchUpdateReady(LocationBatchUpdate locationBatchUpdate);

        void onLocationUpdate(Location locationUpdate);
        //want to notify the manager of updates so it can keep track of last location, for backwards compatibility with current check-in flow.
        //not sure if i want to keep this
    }
}
