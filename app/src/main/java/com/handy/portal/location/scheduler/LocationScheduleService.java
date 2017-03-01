package com.handy.portal.location.scheduler;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.handy.portal.core.event.SystemEvent;
import com.handy.portal.core.manager.ConfigManager;
import com.handy.portal.core.model.ConfigurationResponse;
import com.handy.portal.library.util.Utils;
import com.handy.portal.location.LocationEvent;
import com.handy.portal.location.scheduler.handler.LocationScheduleStrategiesHandler;
import com.handy.portal.location.scheduler.model.LocationScheduleStrategies;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import javax.inject.Inject;


/**
 * listens to the location schedule updated event and
 * starts the schedule handler accordingly
 * <p>
 * note that it makes no direct requests for the location schedule,
 * because LocationScheduleUpdateManager is entirely responsible for that
 * <p>
 * <p/>
 * responsible for handling google api client
 */
public class LocationScheduleService extends Service
        implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        Thread.UncaughtExceptionHandler {
    @Inject
    EventBus mBus;
    @Inject
    ConfigManager mConfigManager;

    GoogleApiClient mGoogleApiClient;
    LocationScheduleStrategiesHandler mLocationScheduleStrategiesHandler;
    Thread.UncaughtExceptionHandler mDefaultUncaughtExceptionHandler;

    @Override
    public void onCreate() {
        mDefaultUncaughtExceptionHandler =
                Thread.getDefaultUncaughtExceptionHandler();
        Thread.currentThread().setUncaughtExceptionHandler(this);
        super.onCreate();
        Utils.inject(getApplicationContext(), this);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        Log.d(getClass().getName(), "started with flags: " + flags + ", startId: " + startId);

        super.onStartCommand(intent, flags, startId);
        if (mGoogleApiClient == null) {
            Log.e(getClass().getName(), "Google api client is null in service start!");
            return START_NOT_STICKY;
        }
        mBus.register(this);
        mGoogleApiClient.connect();

        mBus.post(new LocationEvent.LocationServiceStarted());
        /*
            a manager listens to this event and will
            subsequently request location schedules if necessary
         */

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        mBus.unregister(this);

        if (mLocationScheduleStrategiesHandler != null) {
            mLocationScheduleStrategiesHandler.destroy();
        }

        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        Log.d(getClass().getName(), "service destroyed");
        super.onDestroy();
    }

    @Subscribe
    public void onLocationQueryScheduleReceived(LocationEvent.ReceiveLocationScheduleSuccess event) {
        Log.d(getClass().getName(), "got new location schedule event");
        LocationScheduleStrategies locationScheduleStrategies = event.getLocationScheduleStrategies();
        if (!locationScheduleStrategies.isEmpty()) {
            handleNewLocationStrategies(locationScheduleStrategies);
        }
        //TODO: optimize if the schedule did NOT change!!!!!
    }

    @Subscribe
    public void OnRequestStopLocationService(LocationEvent.RequestStopLocationService event) {
        stopSelf();
    }

    /**
     * delegating bus event here because don't want the handler to subscribe to bus
     * because it does not have a strict lifecycle
     *
     * @param event
     */
    @Subscribe
    public void onNetworkReconnected(SystemEvent.NetworkReconnected event) {
        if (mLocationScheduleStrategiesHandler != null) {
            mLocationScheduleStrategiesHandler.onNetworkReconnected();
        }
    }

    /**
     * got a new schedule, create a handler for it
     *
     * @param locationScheduleStrategies
     */
    private void handleNewLocationStrategies(@NonNull LocationScheduleStrategies locationScheduleStrategies) {
        Log.d(getClass().getName(), "handling new schedule: " + locationScheduleStrategies.toString());
        if (mLocationScheduleStrategiesHandler != null) {
            mLocationScheduleStrategiesHandler.destroy(); //TODO: don't want to do this if the schedule didn't change!
        }
        ConfigurationResponse configurationResponse = mConfigManager.getConfigurationResponse();
        boolean locationTrackingEnabled = configurationResponse == null || configurationResponse.isLocationScheduleServiceEnabled();
        boolean bookingGeofencesEnabled = configurationResponse == null || configurationResponse.isBookingGeofenceServiceEnabled();
        mLocationScheduleStrategiesHandler = new LocationScheduleStrategiesHandler(
                locationScheduleStrategies,
                locationTrackingEnabled,
                bookingGeofencesEnabled,
                mGoogleApiClient,
                this.getBaseContext());
        //TODO: don't want to do this if the schedule didn't change!

        startLocationQueryingIfReady();
    }

    private void startLocationQueryingIfReady() {
        if (isGoogleApiClientConnected() && mLocationScheduleStrategiesHandler != null) {
            //the schedule handler will not start again if already started
            mLocationScheduleStrategiesHandler.startIfNotStarted();
        }
    }

    private boolean isGoogleApiClientConnected() {
        return mGoogleApiClient != null && mGoogleApiClient.isConnected();
    }

    //TODO: would i need this?
    private final IBinder mBinder = new LocalBinder();

    /**
     * doing this because i want to prevent this sticky service from restarting if a crash occurred
     * <p/>
     * however this is not elegant. would prefer a better way
     *
     * @param thread
     * @param ex
     */
    @Override
    public void uncaughtException(final Thread thread, final Throwable ex) {
        //overriding this only prevents the error message dialog that shows
        Log.e(getClass().getName(), "got uncaught exception: " + ex.getMessage());
        stopSelf(); //looks like this makes the service not restart even if sticky!
        if (mDefaultUncaughtExceptionHandler != null) {
            mDefaultUncaughtExceptionHandler.uncaughtException(thread, ex);
            //default handler should log the exception to crashlytics
        }
    }

    public class LocalBinder extends Binder {
        public LocationScheduleService getService() {
            return LocationScheduleService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(final Intent intent) {
        return mBinder;
    }

    @Override
    public void onConnected(final Bundle bundle) {
        startLocationQueryingIfReady();
    }

    @Override
    public void onConnectionSuspended(final int i) {
        Log.d(LocationScheduleService.class.getName(), "GoogleApiClient connection has been suspended");

        //the api client automatically reconnects itself. no need to call connect()

    }

    @Override
    public void onConnectionFailed(final ConnectionResult connectionResult) {
        Log.d(LocationScheduleService.class.getName(), "GoogleApiClient connection has failed");

    }

}
