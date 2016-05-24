package com.handy.portal.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.flow.Flow;
import com.handy.portal.location.LocationUtils;
import com.handy.portal.logger.handylogger.LogEvent;
import com.handy.portal.logger.handylogger.model.DeeplinkLog;
import com.handy.portal.logger.handylogger.model.GoogleApiLog;
import com.handy.portal.logger.mixpanel.Mixpanel;
import com.handy.portal.manager.ConfigManager;
import com.handy.portal.setup.SetupData;
import com.handy.portal.setup.SetupEvent;
import com.handy.portal.setup.step.AcceptTermsStep;
import com.handy.portal.setup.step.AppUpdateStep;
import com.handy.portal.setup.step.OnboardingStep;
import com.handy.portal.setup.step.SetConfigurationStep;
import com.handy.portal.setup.step.SetProviderProfileStep;
import com.handy.portal.library.ui.widget.ProgressDialog;
import com.handy.portal.updater.AppUpdateEvent;
import com.handy.portal.updater.AppUpdateEventListener;
import com.handy.portal.updater.AppUpdateFlowLauncher;
import com.handy.portal.updater.ui.PleaseUpdateActivity;
import com.handy.portal.library.util.Utils;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.Stack;

import javax.inject.Inject;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public abstract class BaseActivity extends AppCompatActivity
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        AppUpdateFlowLauncher
{
    private AppUpdateEventListener mAppUpdateEventListener;
    protected boolean allowCallbacks;
    private Stack<OnBackPressedListener> onBackPressedListenerStack;
    protected ProgressDialog progressDialog;

    //According to android docs this is the preferred way of accessing location instead of using LocationManager
    //will also let us do geofencing and reverse address lookup which is nice
    //This is a clear instance where a service would be great but it is too tightly coupled to an activity to break out
    protected static GoogleApiClient googleApiClient;
    protected static Location lastLocation;

    abstract protected boolean shouldTriggerSetup();

    protected void onSetupComplete()
    {
        // this is meant to be optionally overridden
    }

    protected void onSetupFailure()
    {
        // this is meant to be optionally overridden
    }

    //Public Properties
    public boolean getAllowCallbacks()
    {
        return this.allowCallbacks;
    }

    @Inject
    public Mixpanel mixpanel;
    @Inject
    Bus bus;
    @Inject
    ConfigManager configManager;

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

                // Since deeplink data gets passed along through activity launches, we want to
                // avoid logging deeplink_opened event multiple times.
                boolean deeplinkOpenedLogged =
                        intent.getBooleanExtra(BundleKeys.DEEPLINK_OPENED_LOGGED, false);
                if (!deeplinkOpenedLogged)
                {
                    final String deeplinkSource = intent.getStringExtra(BundleKeys.DEEPLINK_SOURCE);
                    bus.post(new LogEvent.AddLogEvent(new DeeplinkLog.Opened(
                            deeplinkSource,
                            deeplinkData
                    )));
                    intent.putExtra(BundleKeys.DEEPLINK_OPENED_LOGGED, true);
                }
            }
        }
        super.startActivity(intent);
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Utils.inject(this, this);

        if (shouldTriggerSetup())
        {
            triggerSetup();
        }
        mAppUpdateEventListener = new AppUpdateEventListener(this);
        onBackPressedListenerStack = new Stack<>();

        buildGoogleApiClient();
    }

    protected void triggerSetup()
    {
        new SetupHandler(this).start();
    }

    @VisibleForTesting
    public AppUpdateEventListener getBusEventListener()
    {
        return mAppUpdateEventListener;
    }

    @Override
    public void launchAppUpdater()
    {
        startActivity(new Intent(this, PleaseUpdateActivity.class));
    }

    @Override
    public void launchEnableRequiredUpdateFlowApplicationIntent(@NonNull final String packageName, final String promptMessage)
    {
        Toast.makeText(this, promptMessage, Toast.LENGTH_LONG).show();
        Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.setData(Uri.parse("package:" + packageName));
        boolean successfullyLaunchedIntent = Utils.safeLaunchIntent(intent, this);
        if (!successfullyLaunchedIntent)
        {
            /*
            unable to launch the application detail settings intent,
            so try launching the manage application settings intent
             */
            intent = new Intent(android.provider.Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
            Utils.safeLaunchIntent(intent, this);
        }
    }

    @Override
    public void showAppUpdateFlowError(@NonNull final String message)
    {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
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
            googleApiClient.disconnect();
        }
    }

    @Override
    public void onResumeFragments()
    {
        super.onResumeFragments();
        this.bus.register(mAppUpdateEventListener);
        checkForUpdates();
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
        bus.post(new LogEvent.SaveLogsEvent());
        try
        {
             /*
                 on mostly Samsung Android 5.0 devices (responsible for ~97% of crashes here),
                 Activity.onPause() can be called without Activity.onResume()
                 so unregistering the bus here can cause an exception
              */
            bus.unregister(mAppUpdateEventListener);
        }
        catch (Exception e)
        {
            Crashlytics.logException(e); //want more info for now
        }
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
        bus.post(new AppUpdateEvent.RequestUpdateCheck(this));
    }

    public void onReceiveUpdateAvailableError(AppUpdateEvent.ReceiveUpdateAvailableError event)
    {
        //TODO: Handle receive update available error, do we need to block?
    }

    //Setup Google API client to be able to access location through play services
    protected synchronized void buildGoogleApiClient()
    {
        //client is static across activities
        if (googleApiClient == null)
        {
            GoogleApiAvailability gApi = GoogleApiAvailability.getInstance();
            int resultCode = gApi.isGooglePlayServicesAvailable(this);
            if (resultCode == ConnectionResult.SUCCESS)
            {
                googleApiClient = new GoogleApiClient.Builder(this)
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .addApi(LocationServices.API)
                        .build();
                bus.post(new HandyEvent.GooglePlayServicesAvailabilityCheck(true));
                bus.post(new LogEvent.AddLogEvent(new GoogleApiLog.GoogleApiAvailability(true)));
            }
            else
            {
                bus.post(new HandyEvent.GooglePlayServicesAvailabilityCheck(false));
                bus.post(new LogEvent.AddLogEvent(new GoogleApiLog.GoogleApiAvailability(false)));
            }
        }
    }

    @Override
    @SuppressWarnings({"ResourceType", "MissingPermission"})
    public void onConnected(Bundle connectionHint)
    {
        if (!LocationUtils.hasRequiredLocationPermissions(this))
        {
            return;
        }
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

    public static class SetupHandler
    {
        @Inject
        Bus bus;

        private BaseActivity mBaseActivity;

        public SetupHandler(final BaseActivity baseActivity)
        {
            Utils.inject(baseActivity, this);
            bus.register(this);
            mBaseActivity = baseActivity;
        }

        public void start()
        {
            bus.post(new SetupEvent.RequestSetupData());
        }

        @Subscribe
        public void onReceiveSetupDataSuccess(final SetupEvent.ReceiveSetupDataSuccess event)
        {
            final SetupData setupData = event.getSetupData();
            new Flow()
                    .addStep(new AppUpdateStep()) // this does NOTHING for now
                    .addStep(new AcceptTermsStep(mBaseActivity,
                            setupData.getTermsDetails()))
                    .addStep(new SetConfigurationStep(mBaseActivity,
                            setupData.getConfigurationResponse()))
                    .addStep(new SetProviderProfileStep(mBaseActivity,
                            setupData.getProviderProfile()))
                    .addStep(new OnboardingStep())
                    .setOnFlowCompleteListener(new Flow.OnFlowCompleteListener()
                    {
                        @Override
                        public void onFlowComplete()
                        {
                            bus.unregister(SetupHandler.this);
                            mBaseActivity.onSetupComplete();
                        }
                    })
                    .start();
        }

        @Subscribe
        public void onReceiveSetupDataError(final SetupEvent.ReceiveSetupDataError event)
        {
            mBaseActivity.onSetupFailure();
        }
    }
}
