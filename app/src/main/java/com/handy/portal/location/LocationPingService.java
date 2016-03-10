package com.handy.portal.location;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.location.model.LocationBatchUpdate;
import com.handy.portal.location.model.LocationUpdate;
import com.handy.portal.util.SystemUtils;
import com.handy.portal.util.Utils;
import com.squareup.otto.Bus;

import javax.inject.Inject;

public class LocationPingService extends Service implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
{
    @Inject
    Bus mBus;

    private GoogleApiClient mGoogleApiClient;
    private String mEventName;

    @Override
    public void onCreate()
    {
        super.onCreate();
        Utils.inject(this, this);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId)
    {
        mEventName = intent.getStringExtra(BundleKeys.EVENT_NAME);
        mGoogleApiClient.connect();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onConnected(@Nullable final Bundle bundle)
    {
        final Location lastLocation =
                LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (lastLocation != null)
        {
            sendLocationUpdate(lastLocation);
        }
    }

    private void sendLocationUpdate(final Location lastLocation)
    {
        final LocationUpdate locationUpdate = LocationUpdate.from(lastLocation);
        locationUpdate.setEventName(mEventName);
        locationUpdate.setBatteryLevelPercent(SystemUtils.getBatteryLevelPercent(this));
        locationUpdate.setActiveNetworkType(SystemUtils.getActiveNetworkType(this));
        final LocationBatchUpdate locationBatchUpdate = new LocationBatchUpdate(locationUpdate);
        mBus.post(new LocationEvent.SendGeolocationRequest(locationBatchUpdate));
    }

    @Override
    public void onDestroy()
    {
        if (mGoogleApiClient != null)
        {
            mGoogleApiClient.disconnect();
        }
        super.onDestroy();
    }

    @Override
    public void onConnectionSuspended(final int i)
    {
    }

    @Nullable
    @Override
    public IBinder onBind(final Intent intent)
    {
        return null;
    }

    @Override
    public void onConnectionFailed(@NonNull final ConnectionResult connectionResult)
    {
    }
}
