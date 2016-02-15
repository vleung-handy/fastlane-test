package com.handy.portal.location;

import android.support.annotation.NonNull;
import android.util.Log;

import com.handy.portal.location.model.LocationBatchUpdate;
import com.handy.portal.location.model.LocationQueryStrategy;
import com.handy.portal.location.model.LocationUpdate;
import com.handy.portal.util.DateTimeUtils;

import java.util.LinkedList;
import java.util.Queue;

/**
 * this should be created for each location query strategy
 * <p/>
 * manages when to post updates to the server
 */
public class LocationRequestStrategy //TODO: rename this so it is more distinct from the location query strategy model
{
    Queue<LocationUpdate> mLocationUpdateQueue = new LinkedList<>();
    long mTimestampLastUpdatePostedMs = 0;
    LocationQueryStrategy mLocationQueryStrategy;

    public LocationRequestStrategy(@NonNull LocationQueryStrategy locationQueryStrategy)
    {
        mLocationQueryStrategy = locationQueryStrategy;
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
    private LocationBatchUpdate buildLocationBatchUpdate()
    {
        LocationUpdate locationUpdates[] = mLocationUpdateQueue
                .toArray(new LocationUpdate[mLocationUpdateQueue.size()]);
        //.size() is constant time for linked lists

        LocationBatchUpdate locationBatchUpdate = new LocationBatchUpdate(locationUpdates);
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
     * @param locationBatchUpdateReadyListener
     */
    public void onNewLocationUpdate(@NonNull LocationUpdate locationUpdate,
                                    OnLocationBatchUpdateReadyListener locationBatchUpdateReadyListener)
    {
        Log.i(getClass().getName(), "new location update: " + locationUpdate.toString());
        mLocationUpdateQueue.add(locationUpdate);
        if (shouldPostUpdate())
        {
            buildUpdateAndNotifyReady(locationBatchUpdateReadyListener);
        }
    }

    /**
     * TODO: call this when the query strategy expires
     *
     * @param locationBatchUpdateReadyListener
     */
    public void buildUpdateAndNotifyReady(OnLocationBatchUpdateReadyListener locationBatchUpdateReadyListener)
    {
        LocationBatchUpdate locationBatchUpdate = buildLocationBatchUpdate();
        mLocationUpdateQueue.clear();
        mTimestampLastUpdatePostedMs = System.currentTimeMillis();
        locationBatchUpdateReadyListener.onLocationBatchUpdateReady(locationBatchUpdate);
    }

}
