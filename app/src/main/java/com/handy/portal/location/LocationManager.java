package com.handy.portal.location;

import android.location.Location;
import android.util.Log;

import com.handy.portal.constant.PrefsKey;
import com.handy.portal.data.DataManager;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.location.model.LocationBatchUpdate;
import com.handy.portal.location.model.LocationQuerySchedule;
import com.handy.portal.manager.PrefsManager;
import com.handy.portal.manager.ProviderManager;
import com.handy.portal.model.SuccessWrapper;
import com.handy.portal.util.DateTimeUtils;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.inject.Inject;

//TODO: clean this up
/**
 * listens to location schedule updated event
 *
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
    private Location mLastLocationSent;

    //TODO: adjust these params
    private final long LAST_UPDATE_TIME_INTERVAL_MILLISEC = 2 * DateTimeUtils.MILLISECONDS_IN_SECOND;

    private static final int MAX_LOCATION_UPDATE_BATCHES_TO_RETRY_AT_ONCE = 5;
    //TODO: send location updates in batches

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

    //not used for now
    public Location getLastLocationSent()
    {
        return mLastLocationSent;
    }

    Set<LocationBatchUpdate> mFailedLocationBatchUpdates = new HashSet<>();

    //TODO: if this fails due to no network connection, then on network reconnect, need to determine what requests in the queue need to be sent
    @Subscribe
    public void onReceiveLocationBatchUpdate(final LocationEvent.SendGeolocationRequest event)
    {
        final LocationBatchUpdate locationBatchUpdate = event.getLocationBatchUpdate();
        sendLocationBatchUpdate(locationBatchUpdate, true);
    }

    //TODO: call this on network reconnected. clean up, super crude
    public void resendFailedLocationBatchUpdates() //need to test this
    {
        //TODO: can send this at every LENGTH/SAMPLE LENGTH intervals to get a sampling
        Iterator<LocationBatchUpdate> setIterator = mFailedLocationBatchUpdates.iterator();
        int maxBatchesToRetryAtOnce = MAX_LOCATION_UPDATE_BATCHES_TO_RETRY_AT_ONCE;
        while (setIterator.hasNext() && maxBatchesToRetryAtOnce > 0)
        {
            LocationBatchUpdate failedLocationBatchUpdate = setIterator.next();
            sendLocationBatchUpdate(failedLocationBatchUpdate, false);
            Log.i(getClass().getName(), "resending failed location update: " + failedLocationBatchUpdate.toString());
            setIterator.remove();
            maxBatchesToRetryAtOnce--;
        }
    }

    @Subscribe
    public void onNetworkReconnected(final LocationEvent.OnNetworkReconnected event) //TODO: will move this event to right class
    {
        Log.i(getClass().getName(), "on network reconnected");
        resendFailedLocationBatchUpdates();
    }

    private void sendLocationBatchUpdate(final LocationBatchUpdate locationBatchUpdate, final boolean retryUpdateIfFailed)
    {
        Log.i(getClass().getName(), "sending location batch update: " + locationBatchUpdate.toString());
        int providerId = 0;
        if (mProviderManager.getCachedActiveProvider() != null)
        {
            try
            {
                providerId = Integer.parseInt(mProviderManager.getCachedActiveProvider().getId());
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        //TODO: put this update in the request queue

        mDataManager.sendGeolocation(providerId, locationBatchUpdate, new DataManager.Callback<SuccessWrapper>()
        {
            @Override
            public void onSuccess(final SuccessWrapper response)
            {
                if (response.getSuccess())
                {
                    Log.i(getClass().getName(), "Successfully sent location to server");
//                    mBus.post(new LocationEvent.SendGeolocationSuccess());
                    resendFailedLocationBatchUpdates(); //now is probably a good time to retry
                }
                else
                {
                    Log.i(getClass().getName(), "Failed to send location to server but got Retrofit success callback");
                }
            }

            @Override
            public void onError(final DataManager.DataManagerError error)
            {
                Log.i(getClass().getName(), "Failed to send location to server");
                if(retryUpdateIfFailed && error.getType().equals(DataManager.DataManagerError.Type.NETWORK))
                {
                    addToLocationBatchUpdateFailedList(locationBatchUpdate);
                }
            }
        });
    }

    private void addToLocationBatchUpdateFailedList(LocationBatchUpdate locationBatchUpdate)
    {
        mFailedLocationBatchUpdates.add(locationBatchUpdate);
    }

    @Subscribe
    public void onRequestLocationSchedule(LocationEvent.RequestLocationSchedule event)
    {
        //TODO: do something, like get the cached scheduled bookings or request them, then store in prefs

        //TODO: this is temporary. remove; for testing only
        LocationQuerySchedule locationQuerySchedule = new LocationQuerySchedule();
        mBus.post(new LocationEvent.ReceiveLocationSchedule(locationQuerySchedule));

//        mPrefsManager.setString(PrefsKey.LOCATION_QUERY_SCHEDULE, locationQuerySchedule.toJson());


//        //TODO: call this on the condition that the above fails
//        //if request fails, use cached. we can still get location data even if we don't have connection to server
//        LocationQuerySchedule locationQuerySchedule = LocationQuerySchedule.fromJson(mPrefsManager.getString(PrefsKey.LOCATION_QUERY_SCHEDULE));
//        if(locationQuerySchedule != null)
//        {
//            mBus.post(new LocationEvent.ReceiveLocationSchedule(locationQuerySchedule));
//        }

    }


    /**
     * TODO: TEMPORARY; EVENTUALLY JUST LISTEN TO SERVER'S SCHEDULE UPDATED EVENT
     * @param event
     */
    @Subscribe
    public void onReceiveUpdatedScheduledBookings(HandyEvent.ReceiveScheduledBookingsSuccess event)
    {
        //TODO: build the location schedule from these bookings and notify the background service
        LocationQuerySchedule locationQuerySchedule = LocationScheduleFactory.getLocationScheduleFromBookings(event.bookings);

        //TODO: remove the below, test only!
        locationQuerySchedule = new LocationQuerySchedule();

        //TODO: store this in shared prefs
        mPrefsManager.setString(PrefsKey.LOCATION_QUERY_SCHEDULE, locationQuerySchedule.toJson());

        mBus.post(new LocationEvent.ReceiveLocationSchedule(locationQuerySchedule));
    }
}
