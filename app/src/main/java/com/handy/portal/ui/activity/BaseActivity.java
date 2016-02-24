package com.handy.portal.ui.activity;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.handy.portal.analytics.Mixpanel;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.location.LocationEvent;
import com.handy.portal.location.manager.LocationManager;
import com.handy.portal.manager.ConfigManager;
import com.handy.portal.ui.widget.ProgressDialog;
import com.handy.portal.util.Utils;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.Stack;

import javax.inject.Inject;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public abstract class BaseActivity extends AppCompatActivity
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener
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
    @Inject
    LocationManager mLocationManager;

    @Override
    public void startActivity(final Intent intent)
    {
        final Bundle currentExtras = getIntent().getExtras();
        if (currentExtras != null)
        {
            final Bundle deeplinkData = currentExtras.getBundle(BundleKeys.DEEPLINK_DATA);
            // Pass deeplink data along if it exists
            if (deeplinkData != null)
            {
                intent.putExtra(BundleKeys.DEEPLINK_DATA, deeplinkData);
            }
        }
        super.startActivity(intent);
    }

    public void setDeeplinkHandled()
    {
        getIntent().removeExtra(BundleKeys.DEEPLINK_DATA);
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Utils.inject(this, this);

        final Intent intent = getIntent();
        final Uri data = intent.getData();

        busEventListener = new Object()//TODO: put these methods into a service
        {
            @Subscribe
            public void onReceiveUpdateAvailableSuccess(HandyEvent.ReceiveUpdateAvailableSuccess event)
            {
                BaseActivity.this.onReceiveUpdateAvailableSuccess(event);
            }

            @Subscribe
            public void onReceiveUpdateAvailableError(HandyEvent.ReceiveUpdateAvailableError event)
            {
                String message = event.error.getMessage();
                if (message != null)
                {
                    Toast.makeText(BaseActivity.this, event.error.getMessage(), Toast.LENGTH_LONG).show();
                }
            }

            @Subscribe
            public void onReceiveEnableApplication(HandyEvent.RequestEnableApplication event)
            {
                String packageName = event.packageName;
                String promptMessage = event.infoMessage;
                Context context = BaseActivity.this;
                Toast.makeText(context, promptMessage, Toast.LENGTH_LONG).show();
                Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                try
                {
                    intent.setData(Uri.parse("package:" + packageName));
                    context.startActivity(intent);//activity may not be found, may throw exception
                } catch (ActivityNotFoundException e)
                {
                    intent = new Intent(android.provider.Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
                    Utils.safeLaunchIntent(intent, context);
                }
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

        if (googleApiClient != null)
        {
            googleApiClient.connect();
        }
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        allowCallbacks = false;

        if (googleApiClient != null)
        {
            googleApiClient.connect();
        }
    }

    @Override
    protected void onPostResume()
    {
        super.onPostResume();
    }

    @Override
    public void onResumeFragments()
    {
        super.onResumeFragments();
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
            onBackPressedListenerStack.pop().onBackPressed();
        }
        else
        {
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
        if (event.updateDetails.getSuccess() && event.updateDetails.getShouldUpdate()) //TODO: there seems to be a lot of redundant updateDetails.getShouldUpdate() calls. clean this up
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
        if (googleApiClient == null)
        {
            int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
            if (resultCode == ConnectionResult.SUCCESS)
            {
                googleApiClient = new GoogleApiClient.Builder(this)
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .addApi(LocationServices.API)
                        .build();
                bus.post(new HandyEvent.GooglePlayServicesAvailabilityCheck(true));
            }
            else
            {
                bus.post(new HandyEvent.GooglePlayServicesAvailabilityCheck(false));
            }
        }
    }

    @Override
    public void onConnected(Bundle connectionHint)

        //TODO: handle
//        try
//        {
//            /*
//                this is a replacement for getLastLocation() because we think it is contaminating
//                our location data by sending old location updates with newer timestamps
//                http://stackoverflow.com/questions/17603527/android-locationclient-getlastlocation-returns-old-and-inaccurate-location-wit
//
//                this will only passively listen to location updates triggered by someone else
//                which is equivalent to how getLastLocation() works.
//                TODO remove this when we fully switch over to location services
//            */
//            LocationRequest locationRequest = new LocationRequest()
//                    .setPriority(LocationRequest.PRIORITY_LOW_POWER);
//            LocationServices.FusedLocationApi.requestLocationUpdates(
//                    googleApiClient, locationRequest, this);
//        }
//        catch (SecurityException e)
//        {
//            Crashlytics.logException(e);
//        }

        //TODO: this seems to be contaminating location service data, need to test
//        Location newLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        //Keeping old value in the event we have a failed location update
//        if (newLocation != null)
//        {
//            lastLocation = newLocation;
//        }
    }

    @Override
    public void onLocationChanged(final Location location)
    {
        //the location manager will listen to this and update the last known location
        bus.post(new LocationEvent.LocationUpdated(location));
//        lastLocation = location;
    }

    public Location getLastLocation()
    {
        return mLocationManager.getLastKnownLocation();
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
