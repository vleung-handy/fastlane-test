package com.handy.portal.location.manager;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.handy.portal.core.event.HandyEvent;
import com.handy.portal.core.event.SystemEvent;
import com.handy.portal.core.manager.ProviderManager;
import com.handy.portal.core.model.LocationData;
import com.handy.portal.core.model.SuccessWrapper;
import com.handy.portal.data.DataManager;
import com.handy.portal.location.LocationEvent;
import com.handy.portal.location.LocationUtils;
import com.handy.portal.location.model.LocationBatchUpdate;
import com.handy.portal.location.scheduler.model.LocationScheduleStrategies;

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
 * <p>
 * and does the following:
 * - posts batch updates to the server
 * - remembers failed posts and retries posting on network reconnection
 * - keeps track of last location (for legacy code)
 */
public class LocationManager
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private final EventBus mBus;
    private final DataManager mDataManager;
    private final ProviderManager mProviderManager;
    private final Context mContext;
    private GoogleApiClient mGoogleApiClient;

    //TODO: adjust these params
//    private final long LAST_UPDATE_TIME_INTERVAL_MILLISEC = 2 * DateTimeUtils.MILLISECONDS_IN_SECOND;
    private static final int MAX_LOCATION_UPDATE_BATCHES_TO_RETRY_AT_ONCE = 5;

    //don't care about order of batch update
    private Set<LocationBatchUpdate> mFailedLocationBatchUpdates = new HashSet<>();
    private final static int MAX_FAILED_LOCATION_BATCH_UPDATES_SIZE = 100;

    @Inject
    public LocationManager(
            final Context context,
            final EventBus bus,
            final DataManager dataManager,
            final ProviderManager providerManager) {
        mContext = context;
        mBus = bus;
        mBus.register(this);
        mDataManager = dataManager;
        mProviderManager = providerManager;

        GoogleApiAvailability gApi = GoogleApiAvailability.getInstance();
        int resultCode = gApi.isGooglePlayServicesAvailable(context);
        if (resultCode == ConnectionResult.SUCCESS) {
            mGoogleApiClient = new GoogleApiClient.Builder(context)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            mGoogleApiClient.connect();
        }
    }

    @SuppressWarnings({"ResourceType", "MissingPermission"})
    @Nullable
    public Location getLastLocation() {
        if (!LocationUtils.hasRequiredLocationPermissions(mContext) || mGoogleApiClient == null) {
            return null;
        }
        else {
            return LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        }
    }

    @NonNull
    public LocationData getLastKnownLocationData() {
        LocationData locationData;
        Location location = getLastLocation();
        if (location != null) {
            locationData = new LocationData(location);
        }
        else {
            Crashlytics.log("Unable to get user location.");
            locationData = new LocationData();
        }
        return locationData;
    }

    @Override
    public void onConnected(@Nullable final Bundle bundle) {}

    @Override
    public void onConnectionSuspended(final int i) {}

    @Override
    public void onConnectionFailed(@NonNull final ConnectionResult connectionResult) {}

    @Subscribe
    public void onRequestLocationSchedule(LocationEvent.RequestLocationSchedule event) {
        String providerId = mProviderManager.getLastProviderId();
        if (providerId == null) { return; }
        mDataManager.getLocationStrategies(providerId, new DataManager.Callback<LocationScheduleStrategies>() {
            @Override
            public void onSuccess(final LocationScheduleStrategies response) {
                Crashlytics.log("Received location query schedule from server: " + response.toString());
                mBus.post(new LocationEvent.ReceiveLocationScheduleSuccess(response));
            }

            @Override
            public void onError(final DataManager.DataManagerError error) {
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
    public void onReceiveLocationBatchUpdate(final LocationEvent.SendGeolocationRequest event) {
        final LocationBatchUpdate locationBatchUpdate = event.getLocationBatchUpdate();
        sendLocationBatchUpdate(locationBatchUpdate, true);
    }

    //TODO: call this on network reconnected. clean up, super crude

    /**
     * only retrying once, but this is called when network is reconnected or got a success response from server
     */
    public void resendFailedLocationBatchUpdates() {
        //TODO: if ordered, can send this at every LENGTH/SAMPLE LENGTH intervals to get a sampling
        Iterator<LocationBatchUpdate> setIterator = mFailedLocationBatchUpdates.iterator();
        int maxBatchesToRetryAtOnce = MAX_LOCATION_UPDATE_BATCHES_TO_RETRY_AT_ONCE;
        while (setIterator.hasNext() && maxBatchesToRetryAtOnce > 0) {
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
    public void onNetworkReconnected(final SystemEvent.NetworkReconnected event) {
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
    private void sendLocationBatchUpdate(final LocationBatchUpdate locationBatchUpdate, final boolean retryUpdateIfFailed) {
        Log.d(getClass().getName(), "sending location batch update: " + locationBatchUpdate.toString());
        String providerId = mProviderManager.getLastProviderId();
        if (providerId == null) { return; }
        mDataManager.sendGeolocation(providerId, locationBatchUpdate, new DataManager.Callback<SuccessWrapper>() {
            @Override
            public void onSuccess(final SuccessWrapper response) {
                if (response.getSuccess()) {
                    Log.d(getClass().getName(), "Successfully sent location to server");
                    resendFailedLocationBatchUpdates(); //now is probably a good time to retry
                    //calling here in addition to on network reconnected because we're retrying a limited number of batches at once
                }
                else {
                    Log.d(getClass().getName(), "Failed to send location to server but got Retrofit success callback");
                }
            }

            @Override
            public void onError(final DataManager.DataManagerError error) {
                Log.d(getClass().getName(), "Failed to send location to server");
                if (retryUpdateIfFailed && error.getType().equals(DataManager.DataManagerError.Type.NETWORK)) {
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
    private void addToLocationBatchUpdateFailedList(LocationBatchUpdate locationBatchUpdate) {
        if (mFailedLocationBatchUpdates.size() >= MAX_FAILED_LOCATION_BATCH_UPDATES_SIZE) {
            /**
             * if the size of the failed list is greater than max, remove the first one before adding another
             */
            //TODO: what is the price of this? should we remove more than one if costly? should we use a structure that doesn't require iterator?
            Iterator<LocationBatchUpdate> iterator = mFailedLocationBatchUpdates.iterator();
            if (iterator.hasNext()) {
                iterator.next();
                iterator.remove();
            }
        }
        mFailedLocationBatchUpdates.add(locationBatchUpdate);
    }

    /**
     * after the user logs out, we don't need the location service,
     * so request to stop it
     *
     * @param event
     */
    @Subscribe
    public void onUserLoggedOut(HandyEvent.UserLoggedOut event) {
        mBus.post(new LocationEvent.RequestStopLocationService());
    }
}
