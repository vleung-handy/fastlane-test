package com.handy.portal.location;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

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
 * <p/>
 * responsible for handling google api client
 */
public class LocationService extends Service
        implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        Thread.UncaughtExceptionHandler
{
    @Inject
    Bus mBus;

    GoogleApiClient mGoogleApiClient;
    LocationScheduleHandler mLocationScheduleHandler;

    @Override
    public void onCreate()
    {
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
    public int onStartCommand(final Intent intent, final int flags, final int startId)
    {
        Log.i(getClass().getName(), "started with flags: " + flags + ", startId: " + startId);
        Toast.makeText(getBaseContext(), "started location service", Toast.LENGTH_SHORT).show(); //TODO: remove, test only

        super.onStartCommand(intent, flags, startId);
        try
        {
            mBus.register(this);
            mGoogleApiClient.connect();
//            requestLocationQuerySchedule();//uncomment this back

            //TODO: remove, for toggle testing only
            if (intent != null && intent.getExtras() != null && intent.getExtras().getParcelable(LocationQuerySchedule.EXTRA_LOCATION_SCHEDULE) != null)
            {
                mBus.post(new LocationEvent.ReceiveLocationSchedule((LocationQuerySchedule)
                        intent.getExtras().getParcelable(LocationQuerySchedule.EXTRA_LOCATION_SCHEDULE)));

            }
            else
            {
                requestLocationQuerySchedule();
            }
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
     *
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

    @Override
    public void uncaughtException(final Thread thread, final Throwable ex)
    {
        //overriding this only prevents the error message dialog that shows
        Log.e(getClass().getName(), "got uncaught exception");

        stopSelf(); //TODO: test this. does it make it not restart even if sticky?
    }

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

        //the api client automatically reconnects itself. no need to call connect()

    }

    @Override
    public void onConnectionFailed(final ConnectionResult connectionResult)
    {
        Log.i(LocationService.class.getName(), "GoogleApiClient connection has failed");

    }
}
