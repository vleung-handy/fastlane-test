package com.handy.portal.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.handy.portal.analytics.Mixpanel;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.manager.ConfigManager;
import com.handy.portal.ui.widget.ProgressDialog;
import com.handy.portal.util.Utils;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.Stack;

import javax.inject.Inject;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public abstract class BaseActivity extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
{
    private Object busEventListener;
    protected boolean allowCallbacks;
    private Stack<OnBackPressedListener> onBackPressedListenerStack;
    protected ProgressDialog progressDialog;

    //According to android docs this is the preferred way of accessing location instead of using LocationManager
    //will also let us do geofencing and reverse address lookup which is nice
        //This is a clear instance where a service would be great but it is too tightly coupled to an activity to break out
    protected static GoogleApiClient googleApiClient;
    protected static Location lastLocation;

    //Public Properties
    public boolean getAllowCallbacks()
    {
        return this.allowCallbacks;
    }

    @Inject
    Mixpanel mixpanel;
    @Inject
    Bus bus;
    @Inject
    ConfigManager configManager;

    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Utils.inject(this, this);

        final Intent intent = getIntent();
        final Uri data = intent.getData();

        busEventListener = new Object()
        {
            @Subscribe
            public void onReceiveUpdateAvailableSuccess(HandyEvent.ReceiveUpdateAvailableSuccess event)
            {
                BaseActivity.this.onReceiveUpdateAvailableSuccess(event);
            }

            @Subscribe
            public void onReceiveUpdateAvailableError(HandyEvent.ReceiveUpdateAvailableError event)
            {
                //TODO: Handle receive update available errors
            }
        };

        onBackPressedListenerStack = new Stack<>();

        buildGoogleApiClient();
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        allowCallbacks = true;

        if(this.googleApiClient != null)
        {
            this.googleApiClient.connect();
        }
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        allowCallbacks = false;

        if(this.googleApiClient != null)
        {
            this.googleApiClient.connect();
        }
    }

    @Override
    protected void onPostResume()
    {
        super.onPostResume();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        this.bus.register(busEventListener);
        checkForUpdates();
        postActivityResumeEvent(); //do not disable this
    }

    @Override
    protected void attachBaseContext(Context newBase)
    {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onBackPressed()
    {
        if (!onBackPressedListenerStack.isEmpty())
        {
            System.out.println("Using the custom back listener");
            onBackPressedListenerStack.pop().onBackPressed();
        }
        else
        {
            System.out.println("Using basic back listener");
            super.onBackPressed();
        }
    }

    @Override
    public void onPause()
    {
        postActivityPauseEvent();
        bus.unregister(busEventListener);
        super.onPause();
    }

    @Override
    protected void onDestroy()
    {
        mixpanel.flush();
        super.onDestroy();
    }

    public void addOnBackPressedListener(final OnBackPressedListener onBackPressedListener)
    {
        this.onBackPressedListenerStack.push(onBackPressedListener);
    }

    public void clearOnBackPressedListenerStack()
    {
        onBackPressedListenerStack.clear();
    }

    public interface OnBackPressedListener
    {
        void onBackPressed();
    }

    public void checkForUpdates()
    {
        bus.post(new HandyEvent.RequestUpdateCheck(this));
    }

    public void postActivityResumeEvent()
    {
        bus.post(new HandyEvent.ActivityResumed(this));
    }

    public void postActivityPauseEvent()
    {
        bus.post(new HandyEvent.ActivityPaused(this));
    }

    public void onReceiveUpdateAvailableSuccess(HandyEvent.ReceiveUpdateAvailableSuccess event)
    {
        if(event.updateDetails.getSuccess() && event.updateDetails.getShouldUpdate())
        {
            startActivity(new Intent(this, PleaseUpdateActivity.class));
        }
        //otherwise ignore
    }

    public void onReceiveUpdateAvailableError(HandyEvent.ReceiveUpdateAvailableError event)
    {
        //TODO: Handle receive update available error, do we need to block?
    }

    //Setup Google API client to be able to access location through play services
    protected synchronized void buildGoogleApiClient()
    {
        //client is static across activities
        if(googleApiClient == null)
        {
            int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
            if (resultCode == ConnectionResult.SUCCESS)
            {
                googleApiClient = new GoogleApiClient.Builder(this)
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .addApi(LocationServices.API)
                        .build();
            }
            else
            {
                Crashlytics.log("No Google Play Services, can not get locational data");
            }
        }
    }

    public void onConnected(Bundle connectionHint)
    {
        Location newLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        //Keeping old value in the event we have a failed location update
        if (newLocation != null)
        {
            lastLocation = newLocation;
        }
    }

    public Location getLastLocation()
    {
        return lastLocation;
    }

    //For google apli client
    public void onConnectionSuspended(int i)
    {
        //TODO: Handle?
    }

    //For google apli client
    public void onConnectionFailed(ConnectionResult var1)
    {
        //TODO: Handle?
    }
}
