package com.handy.portal.location;

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
import com.handy.portal.event.SystemEvent;
import com.handy.portal.location.model.LocationQuerySchedule;
import com.handy.portal.util.Utils;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;


/**
 * listens to the location schedule updated event and starts the schedule handler accordingly
 * <p/>
 * responsible for handling google api client
 * *
 * TODO: what exactly is wakelock and do i need it?
 * <p/>
 * TODO: consider running this, or parts of this, in separate thread from main application to prevent lags
 * <p/>
 * TODO: make sure this isn't started if config manager says no
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
    Thread.UncaughtExceptionHandler mDefaultUncaughtExceptionHandler;

    @Override
    public void onCreate()
    {
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
    public int onStartCommand(final Intent intent, final int flags, final int startId)
    {
        Log.d(getClass().getName(), "started with flags: " + flags + ", startId: " + startId);

        super.onStartCommand(intent, flags, startId);
        if (mGoogleApiClient == null)
        {
            return START_NOT_STICKY;
        }
        mBus.register(this);
        mGoogleApiClient.connect();

        requestLocationQuerySchedule();

        return START_STICKY;
    }

    @Override
    public void onDestroy()
    {
        try
        {
            mBus.unregister(this);
        }
        catch (Exception e)
        {
            //this happens when this isn't registered to the bus. one case is when stopService() is called twice
        }

        if (mLocationScheduleHandler != null)
        {
            mLocationScheduleHandler.destroy();
        }

        if (mGoogleApiClient != null)
        {
            mGoogleApiClient.disconnect();
        }
        Log.d(getClass().getName(), "service destroyed");
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
    public void onLocationQueryScheduleReceived(LocationEvent.ReceiveLocationScheduleSuccess event)
    {
        Log.d(getClass().getName(), "got new location schedule event");
        LocationQuerySchedule locationQuerySchedule = event.getLocationQuerySchedule();
        if(!locationQuerySchedule.isEmpty())
        {
            handleNewLocationQuerySchedule(locationQuerySchedule);
        }
        //TODO: optimize if the schedule did NOT change!!!!!
    }

    /**
     * delegating bus event here because don't want the handler to subscribe to bus
     * because it does not have a strict lifecycle
     * @param event
     */
    @Subscribe
    public void onNetworkReconnected(SystemEvent.NetworkReconnected event)
    {
        if(mLocationScheduleHandler != null)
        {
            mLocationScheduleHandler.onNetworkReconnected();
        }
    }
    /**
     * got a new schedule, create a handler for it
     *
     * @param locationQuerySchedule
     */
    private void handleNewLocationQuerySchedule(@NonNull LocationQuerySchedule locationQuerySchedule)
    {
        Log.d(getClass().getName(), "handling new schedule: " + locationQuerySchedule.toString());
        if (mLocationScheduleHandler != null)
        {
            mLocationScheduleHandler.destroy(); //TODO: don't want to do this if the schedule didn't change!
        }
        mLocationScheduleHandler = new LocationScheduleHandler(locationQuerySchedule, mGoogleApiClient, this);
        //TODO: don't want to do this if the schedule didn't change!

        startLocationQueryingIfReady();
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

    /**
     * doing this because i want to prevent this sticky service from restarting if a crash occurred
     * <p/>
     * however this is not elegant. would prefer a better way
     *
     * @param thread
     * @param ex
     */
    @Override
    public void uncaughtException(final Thread thread, final Throwable ex)
    {
        //overriding this only prevents the error message dialog that shows
        Log.e(getClass().getName(), "got uncaught exception: " + ex.getMessage());
        stopSelf(); //looks like this makes the service not restart even if sticky!
        if (mDefaultUncaughtExceptionHandler != null)
        {
            mDefaultUncaughtExceptionHandler.uncaughtException(thread, ex);
            //default handler should log the exception to crashlytics
        }
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
        Log.d(LocationService.class.getName(), "GoogleApiClient connection has been suspended");

        //the api client automatically reconnects itself. no need to call connect()

    }

    @Override
    public void onConnectionFailed(final ConnectionResult connectionResult)
    {
        Log.d(LocationService.class.getName(), "GoogleApiClient connection has failed");

    }

}
