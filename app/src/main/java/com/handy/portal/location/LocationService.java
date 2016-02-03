package com.handy.portal.location;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.handy.portal.location.model.LocationQuerySchedule;
import com.handy.portal.util.Utils;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

//TODO: test only


/**
 * listens to the location schedule updated event and starts the schedule handler accordingly
 *
 * responsible for handling google api client
 */
public class LocationService extends Service
        implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener
{
    @Inject
    Bus mBus;

    GoogleApiClient mGoogleApiClient;
    LocationScheduleHandler mLocationScheduleHandler;

    @Override
    public void onCreate()
    {
        super.onCreate();
        Utils.inject(getApplicationContext(), this);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId)
    {
        super.onStartCommand(intent, flags, startId);
        try
        {
            mBus.register(this);
            mGoogleApiClient.connect();
            requestLocationQuerySchedule();
        }
        catch (Exception e)
        {
            //TODO: shouldn't happen but if it does, don't restart the service
            //TODO: investigate which parts of this service need to be exception handled
            e.printStackTrace();
            Crashlytics.logException(e);
            return START_NOT_STICKY;
        }

        //START_STICKY will keep this service running even if app is closed
        return START_STICKY;
    }

    @Override
    public void onDestroy()
    {
        mBus.unregister(this);
        unregisterReceiver(mLocationScheduleHandler);
        mLocationScheduleHandler.destroy();
        mGoogleApiClient.disconnect();
        super.onDestroy();
    }

    /**
     * ask for the location schedule (location manager will respond to this)
     */
    private void requestLocationQuerySchedule()
    {
        mBus.post(new LocationEvent.RequestLocationSchedule());
    }

    @Subscribe
    public void onNewLocationQueryScheduleReceived(LocationEvent.ReceiveLocationSchedule event)
    {
        Log.i(getClass().getName(), "got new location schedule event");
        LocationQuerySchedule locationQuerySchedule = event.getLocationQuerySchedule();
        handleNewLocationQuerySchedule(locationQuerySchedule);
    }

    /**
     * got a new schedule, create a handler for it
     * @param locationQuerySchedule
     */
    private void handleNewLocationQuerySchedule(@NonNull LocationQuerySchedule locationQuerySchedule)
    {
        Log.i(getClass().getName(), "handling new schedule: " + locationQuerySchedule.toString());
        if (mLocationScheduleHandler != null)
        {
            mLocationScheduleHandler.destroy();
        }
        mLocationScheduleHandler = new LocationScheduleHandler(locationQuerySchedule, mGoogleApiClient, this);
        startLocationQueryingIfReady();
        //TODO: do something useful
    }

    private void startLocationQueryingIfReady()
    {
        if (isGoogleApiClientConnected() && mLocationScheduleHandler != null)
        {
            //should be safe if called twice because handled strategies are removed
            mLocationScheduleHandler.start();
        }
    }

    private boolean isGoogleApiClientConnected()
    {
        return mGoogleApiClient != null && mGoogleApiClient.isConnected();
    }

    //TODO: would i need this?
    private final IBinder mBinder = new LocalBinder();

    public class LocalBinder extends Binder
    {
        public LocationService getService()
        {
            return LocationService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(final Intent intent)
    {
        return mBinder;
    }

    @Override
    public void onConnected(final Bundle bundle)
    {
        startLocationQueryingIfReady();
    }

    @Override
    public void onConnectionSuspended(final int i)
    {
        Log.i(LocationService.class.getName(), "GoogleApiClient connection has been suspended");

        //TODO: do location updates continue or are they stopped? if stopped do they need to be restarted?

    }

    @Override
    public void onConnectionFailed(final ConnectionResult connectionResult)
    {
        Log.i(LocationService.class.getName(), "GoogleApiClient connection has failed");

    }
}
