package com.handy.portal.location;

import android.location.Location;
import android.util.Log;

import com.handy.portal.constant.PrefsKey;
import com.handy.portal.data.DataManager;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.location.model.LocationQuerySchedule;
import com.handy.portal.location.model.LocationUpdate;
import com.handy.portal.manager.PrefsManager;
import com.handy.portal.manager.ProviderManager;
import com.handy.portal.model.SuccessWrapper;
import com.handy.portal.util.DateTimeUtils;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.Date;

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

    //TODO: adjust this param
    private final long LAST_UPDATE_TIME_INTERVAL_MILLISEC = 2 * DateTimeUtils.MILLISECONDS_IN_SECOND;

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

    @Subscribe
    public void onReceiveLocationUpdate(final LocationEvent.LocationChanged event)
    {
        Location location = event.getLocation();

//        if (mLastLocationSent != null && location.getTime() - mLastLocationSent.getTime() < LAST_UPDATE_TIME_INTERVAL_MILLISEC)
//        {
//            //don't do anything
//            return;
//        }
        mLastLocationSent = location;
        String eventName = "test";
        LocationUpdate locationUpdate = new LocationUpdate(
                location.getLatitude(),
                location.getLongitude(),
                location.getAccuracy(),
                location.getAltitude(),
                location.getSpeed(),
                location.getBearing(),
                eventName,//event name
                new Date(location.getTime()),
                0, //no battery level yet
                0 //no booking id yet
        );
        Log.i(getClass().getName(), locationUpdate.toString());
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
        mDataManager.sendGeolocation(providerId, locationUpdate, new DataManager.Callback<SuccessWrapper>()
        {
            @Override
            public void onSuccess(final SuccessWrapper response)
            {
                if(response.getSuccess())
                {
                    Log.i(getClass().getName(), "Successfully sent location to server");
                }
                else{
                    Log.i(getClass().getName(), "Failed to send location to server but got Retrofit success callback");
                }
            }

            @Override
            public void onError(final DataManager.DataManagerError error)
            {
                Log.i(getClass().getName(), "Failed to send location to server");
            }
        });
        //TODO
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
