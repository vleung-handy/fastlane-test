package com.handy.portal.location.manager;

import android.location.Location;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.handy.portal.data.DataManager;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.event.SystemEvent;
import com.handy.portal.location.LocationEvent;
import com.handy.portal.location.model.LocationBatchUpdate;
import com.handy.portal.location.scheduler.model.LocationScheduleStrategies;
import com.handy.portal.manager.ProviderManager;
import com.handy.portal.model.SuccessWrapper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.inject.Inject;

/**
 * listens to the following events:
 * - request location schedule event
 * - location updates
 * - network reconnected
 *
 * and does the following:
 * - posts batch updates to the server
 * - remembers failed posts and retries posting on network reconnection
 * - keeps track of last location (for legacy code)
 */
public class LocationManager
{
    private static Location sLastLocation;

    private final EventBus mBus;
    private final DataManager mDataManager;
    private final ProviderManager mProviderManager;

    //TODO: adjust these params
//    private final long LAST_UPDATE_TIME_INTERVAL_MILLISEC = 2 * DateTimeUtils.MILLISECONDS_IN_SECOND;
    private static final int MAX_LOCATION_UPDATE_BATCHES_TO_RETRY_AT_ONCE = 5;

    //don't care about order of batch update
    private Set<LocationBatchUpdate> mFailedLocationBatchUpdates = new HashSet<>();
    private final static int MAX_FAILED_LOCATION_BATCH_UPDATES_SIZE = 100;

    public static void setLastLocation(Location lastLocation) { sLastLocation = lastLocation; }

    public static Location getLastLocation() { return sLastLocation; }

    @Inject
    public LocationManager(final EventBus bus,
                           final DataManager dataManager,
                           final ProviderManager providerManager)
    {
        mBus = bus;
        mBus.register(this);
        mDataManager = dataManager;
        mProviderManager = providerManager;
    }

    @Subscribe
    public void onRequestLocationSchedule(LocationEvent.RequestLocationSchedule event)
    {
        String providerId = mProviderManager.getLastProviderId();
        if (providerId == null) { return; }
        mDataManager.getLocationStrategies(providerId, new DataManager.Callback<LocationScheduleStrategies>()
        {
            @Override
            public void onSuccess(final LocationScheduleStrategies response)
            {
                Crashlytics.log("Received location query schedule from server: " + response.toString());
                mBus.post(new LocationEvent.ReceiveLocationScheduleSuccess(response));
            }

            @Override
            public void onError(final DataManager.DataManagerError error)
            {
                mBus.post(new LocationEvent.ReceiveLocationScheduleError(error));
            }
        });
    }

    /**
     * will send location batch updates to the server
     *
     * @param event
     */
    @Subscribe
    public void onReceiveLocationBatchUpdate(final LocationEvent.SendGeolocationRequest event)
    {
        final LocationBatchUpdate locationBatchUpdate = event.getLocationBatchUpdate();
        sendLocationBatchUpdate(locationBatchUpdate, true);
    }

    //TODO: call this on network reconnected. clean up, super crude

    /**
     * only retrying once, but this is called when network is reconnected or got a success response from server
     */
    public void resendFailedLocationBatchUpdates()
    {
        //TODO: if ordered, can send this at every LENGTH/SAMPLE LENGTH intervals to get a sampling
        Iterator<LocationBatchUpdate> setIterator = mFailedLocationBatchUpdates.iterator();
        int maxBatchesToRetryAtOnce = MAX_LOCATION_UPDATE_BATCHES_TO_RETRY_AT_ONCE;
        while (setIterator.hasNext() && maxBatchesToRetryAtOnce > 0)
        {
            LocationBatchUpdate failedLocationBatchUpdate = setIterator.next();
            sendLocationBatchUpdate(failedLocationBatchUpdate, false);
            Log.d(getClass().getName(), "resending failed location update: " + failedLocationBatchUpdate.toString());
            setIterator.remove();
            maxBatchesToRetryAtOnce--;
        }
    }

    /**
     * network got re-established. resend the failed updates
     *
     * @param event
     */
    @Subscribe
    public void onNetworkReconnected(final SystemEvent.NetworkReconnected event)
    {
        Log.d(getClass().getName(), "on network reconnected");
        resendFailedLocationBatchUpdates();
        //request immediate location updates?
    }

    /**
     * sends location batch updates to the server
     *
     * @param locationBatchUpdate
     * @param retryUpdateIfFailed true if the request should be retried on network reconnect
     */
    private void sendLocationBatchUpdate(final LocationBatchUpdate locationBatchUpdate, final boolean retryUpdateIfFailed)
    {
        Log.d(getClass().getName(), "sending location batch update: " + locationBatchUpdate.toString());
        String providerId = mProviderManager.getLastProviderId();
        if (providerId == null) { return; }
        mDataManager.sendGeolocation(providerId, locationBatchUpdate, new DataManager.Callback<SuccessWrapper>()
        {
            @Override
            public void onSuccess(final SuccessWrapper response)
            {
                if (response.getSuccess())
                {
                    Log.d(getClass().getName(), "Successfully sent location to server");
                    resendFailedLocationBatchUpdates(); //now is probably a good time to retry
                    //calling here in addition to on network reconnected because we're retrying a limited number of batches at once
                }
                else
                {
                    Log.d(getClass().getName(), "Failed to send location to server but got Retrofit success callback");
                }
            }

            @Override
            public void onError(final DataManager.DataManagerError error)
            {
                Log.d(getClass().getName(), "Failed to send location to server");
                if (retryUpdateIfFailed && error.getType().equals(DataManager.DataManagerError.Type.NETWORK))
                {
                    //only retry when network issue. don't want to retry if it's a server problem or our problem
                    addToLocationBatchUpdateFailedList(locationBatchUpdate);
                }
            }
        });
    }

    /**
     * adds the failed location batch update request to the failed set
     *
     * @param locationBatchUpdate
     */
    private void addToLocationBatchUpdateFailedList(LocationBatchUpdate locationBatchUpdate)
    {
        if (mFailedLocationBatchUpdates.size() >= MAX_FAILED_LOCATION_BATCH_UPDATES_SIZE)
        {
            /**
             * if the size of the failed list is greater than max, remove the first one before adding another
             */
            //TODO: what is the price of this? should we remove more than one if costly? should we use a structure that doesn't require iterator?
            Iterator<LocationBatchUpdate> iterator = mFailedLocationBatchUpdates.iterator();
            if (iterator.hasNext())
            {
                iterator.next();
                iterator.remove();
            }
        }
        mFailedLocationBatchUpdates.add(locationBatchUpdate);
    }

    /**
     * after the user logs out, we don't need the location service,
     * so request to stop it
     * @param event
     */
    @Subscribe
    public void onUserLoggedOut(HandyEvent.UserLoggedOut event)
    {
        mBus.post(new LocationEvent.RequestStopLocationService());
    }
}
