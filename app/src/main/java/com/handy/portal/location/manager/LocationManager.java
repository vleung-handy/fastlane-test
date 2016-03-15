package com.handy.portal.location.manager;

import android.location.Location;
import android.util.Log;

import com.handy.portal.data.DataManager;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.event.SystemEvent;
import com.handy.portal.location.LocationEvent;
import com.handy.portal.location.model.LocationBatchUpdate;
import com.handy.portal.location.scheduler.model.LocationStrategies;
import com.handy.portal.manager.PrefsManager;
import com.handy.portal.manager.ProviderManager;
import com.handy.portal.model.SuccessWrapper;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.inject.Inject;

//TODO: clean this up


/**
 * listens to location schedule updated event
 * <p/>
 * listens to location update events from the location service
 * <p/>
 * posts location updates to the server
 * <p/>
 * keeps track of the last location
 */
public class LocationManager
{
    private final Bus mBus;
    private final DataManager mDataManager;
    private final ProviderManager mProviderManager;
    private final PrefsManager mPrefsManager;
    private Location mLastKnownLocation; //for backwards compatibility with check-in flow

    //TODO: adjust these params
//    private final long LAST_UPDATE_TIME_INTERVAL_MILLISEC = 2 * DateTimeUtils.MILLISECONDS_IN_SECOND;
    private static final int MAX_LOCATION_UPDATE_BATCHES_TO_RETRY_AT_ONCE = 5;

    //don't care about order of batch update
    private Set<LocationBatchUpdate> mFailedLocationBatchUpdates = new HashSet<>();
    private final static int MAX_FAILED_LOCATION_BATCH_UPDATES_SIZE = 100;

    @Inject
    public LocationManager(final Bus bus,
                           final DataManager dataManager,
                           final ProviderManager providerManager,
                           final PrefsManager prefsManager)
    {
        mBus = bus;
        mBus.register(this);
        mDataManager = dataManager;
        mProviderManager = providerManager;
        mPrefsManager = prefsManager;
    }

    /**
     * for backwards compatibility with check-in flow which requires this
     * TODO can remove this when everyone switches over to location service
     *
     * @return
     */
    public Location getLastKnownLocation()
    {
        return mLastKnownLocation;
    }

    @Subscribe
    public void onRequestLocationSchedule(LocationEvent.RequestLocationSchedule event)
    {
        String providerId = mProviderManager.getLastProviderId();
        if (providerId == null) { return; }
        mDataManager.getLocationStrategies(providerId, new DataManager.Callback<LocationStrategies>()
        {
            @Override
            public void onSuccess(final LocationStrategies response)
            {
                mBus.post(new LocationEvent.ReceiveLocationScheduleSuccess(response));
            }

            @Override
            public void onError(final DataManager.DataManagerError error)
            {
                mBus.post(new LocationEvent.ReceiveLocationScheduleError(error));
            }
        });
    }

    @Subscribe
    public void onReceiveLocationUpdate(final LocationEvent.LocationUpdated event)
    {
        /*
        basic way to keep track of last known location for backwards compatibility with
        the check-in flow which requires it
         */
        mLastKnownLocation = event.getLocationUpdate();
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

    @Subscribe
    public void onBookingChangedOrCreated(HandyEvent.BookingChangedOrCreated event)
    {
        //when this happens, we should rebuild the schedule
        //TODO: see if building schedule is costly. if so, note which bookings were invalidated and rebuild the schedule only for those bookings
        mBus.post(new LocationEvent.RequestLocationSchedule());
    }

}
