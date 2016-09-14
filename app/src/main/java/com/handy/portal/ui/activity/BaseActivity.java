package com.handy.portal.ui.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
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
import com.handy.portal.library.util.FragmentUtils;
import com.handy.portal.library.util.SystemUtils;
import com.handy.portal.library.util.Utils;
import com.handy.portal.location.LocationUtils;
import com.handy.portal.location.scheduler.LocationScheduleService;
import com.handy.portal.location.ui.LocationSettingsBlockerDialogFragment;
import com.handy.portal.logger.handylogger.LogEvent;
import com.handy.portal.logger.handylogger.model.AppLog;
import com.handy.portal.logger.handylogger.model.DeeplinkLog;
import com.handy.portal.logger.handylogger.model.GoogleApiLog;
import com.handy.portal.manager.ConfigManager;
import com.handy.portal.manager.PrefsManager;
import com.handy.portal.model.ConfigurationResponse;
import com.handy.portal.onboarding.ui.fragment.PermissionsBlockerDialogFragment;
import com.handy.portal.setup.SetupData;
import com.handy.portal.setup.SetupEvent;
import com.handy.portal.setup.step.AcceptTermsStep;
import com.handy.portal.setup.step.AppUpdateStep;
import com.handy.portal.setup.step.SetConfigurationStep;
import com.handy.portal.setup.step.SetProviderProfileStep;
import com.handy.portal.updater.AppUpdateEvent;
import com.handy.portal.updater.AppUpdateEventListener;
import com.handy.portal.updater.AppUpdateFlowLauncher;
import com.handy.portal.updater.ui.PleaseUpdateActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Stack;

import javax.inject.Inject;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public abstract class BaseActivity extends AppCompatActivity
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        AppUpdateFlowLauncher
{
    @Inject
    PrefsManager mPrefsManager;

    @Inject
    ConfigManager mConfigManager;

    private AppUpdateEventListener mAppUpdateEventListener;
    protected boolean allowCallbacks;
    private Stack<OnBackPressedListener> onBackPressedListenerStack;

    //According to android docs this is the preferred way of accessing location instead of using LocationManager
    //will also let us do geofencing and reverse address lookup which is nice
    //This is a clear instance where a service would be great but it is too tightly coupled to an activity to break out
    protected static GoogleApiClient googleApiClient;
    protected static Location lastLocation;
    private SetupHandler mSetupHandler;
    private boolean mWasOpenBefore;
    private boolean mHasRequestedPermissionsBefore;

    private static final int ACCESS_FINE_LOCATION_AND_PHONE_STATE_PERMISSIONS_REQUEST_CODE = 42;


    // this is meant to be optionally overridden
    protected boolean shouldTriggerSetup()
    {
        return false;
    }

    // this is meant to be optionally overridden
    protected void onSetupComplete(final SetupData setupData)
    {
    }

    // this is meant to be optionally overridden
    protected void onSetupFailure()
    {
    }

    //Public Properties
    public boolean getAllowCallbacks()
    {
        return this.allowCallbacks;
    }

    @Inject
    public EventBus bus;
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

        mAppUpdateEventListener = new AppUpdateEventListener(this);
        onBackPressedListenerStack = new Stack<>();

        buildGoogleApiClient();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        showRequiredPermissionBlockersOrStartServiceIfNecessary();
        bus.post(new LogEvent.SendLogsEvent());
        if (mWasOpenBefore)
        {
            bus.post(new LogEvent.AddLogEvent(new AppLog.AppOpenLog(false, false)));
        }
    }

    @Override
    protected void onPostResume()
    {
        super.onPostResume();
        if (!isFinishing() && shouldTriggerSetup())
        {
            triggerSetup();
        }
    }

    protected void triggerSetup()
    {
        if (mSetupHandler == null || !mSetupHandler.isOngoing())
        {
            mSetupHandler = new SetupHandler(this);
            mSetupHandler.start();
        }
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
        bus.unregister(mAppUpdateEventListener);
        mWasOpenBefore = true;
        super.onPause();
    }

    @Override
    protected void onDestroy()
    {
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
        EventBus bus;

        private BaseActivity mBaseActivity;
        private Flow mSetupFlow;

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

        public boolean isOngoing()
        {
            return mSetupFlow != null && !mSetupFlow.isComplete();
        }

        @Subscribe
        public void onReceiveSetupDataSuccess(final SetupEvent.ReceiveSetupDataSuccess event)
        {
            final SetupData setupData = event.getSetupData();
            mSetupFlow = new Flow()
                    .addStep(new AppUpdateStep()) // this does NOTHING for now
                    .addStep(new AcceptTermsStep(mBaseActivity,
                            setupData.getTermsDetails()))
                    .addStep(new SetConfigurationStep(mBaseActivity,
                            setupData.getConfigurationResponse()))
                    .addStep(new SetProviderProfileStep(mBaseActivity,
                            setupData.getProviderProfile()))
                    .setOnFlowCompleteListener(new Flow.OnFlowCompleteListener()
                    {
                        @Override
                        public void onFlowComplete()
                        {
                            bus.unregister(SetupHandler.this);
                            mBaseActivity.onSetupComplete(setupData);
                        }
                    })
                    .start();
        }

        @Subscribe
        public void onReceiveSetupDataError(final SetupEvent.ReceiveSetupDataError event)
        {
            bus.unregister(this);
            mBaseActivity.onSetupFailure();
        }
    }

    private boolean isLocationServiceEnabled()
    {
        ConfigurationResponse configurationResponse = mConfigManager.getConfigurationResponse();
        return configurationResponse != null
                && configurationResponse.isLocationServiceEnabled();
    }

    /**
     * TODO: this is temporary to handle case in which config response comes back later
     * ideally, we should probably block the app until the config response is received
     *
     * @param event
     */
    @Subscribe
    public void onReceiveConfigurationResponse(HandyEvent.ReceiveConfigurationSuccess event)
    {
        showRequiredPermissionBlockersOrStartServiceIfNecessary();
    }

    //region Private methods
    private synchronized void showRequiredPermissionBlockersOrStartServiceIfNecessary()
    {
        if (!isEverythingGood())
        {
            if (!mHasRequestedPermissionsBefore)
            {
                mHasRequestedPermissionsBefore = true;
                ActivityCompat.requestPermissions(this, new String[]{
                                Manifest.permission.READ_PHONE_STATE,
                                Manifest.permission.ACCESS_FINE_LOCATION},
                        ACCESS_FINE_LOCATION_AND_PHONE_STATE_PERMISSIONS_REQUEST_CODE);
            }
            else if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                    == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED)
            {
                // Both permissions granted, check for location settings or show blocker for that
                showLocationSettingsBlockerIfNecessaryOrStartLocationService();
            }
            else
            {
                // Atleast one of them still not granted, show blocker
                showPermissionsBlocker();
            }
        }
    }

    private void showLocationSettingsBlockerIfNecessaryOrStartLocationService()
    {
        //check whether location services setting is on
        if (!LocationUtils.hasRequiredLocationSettings(this) &&
                getSupportFragmentManager().findFragmentByTag(LocationSettingsBlockerDialogFragment.FRAGMENT_TAG) == null)
        //don't want to show this dialog if it's already showing
        {
            LocationSettingsBlockerDialogFragment locationSettingsBlockerDialogFragment
                    = new LocationSettingsBlockerDialogFragment();
            FragmentUtils.safeLaunchDialogFragment(locationSettingsBlockerDialogFragment, this,
                    LocationSettingsBlockerDialogFragment.FRAGMENT_TAG);
        }
        else
        {
            removeAllBlockersIfNeeded();
            startLocationServiceIfNecessary();
        }
    }

    private void startLocationServiceIfNecessary()
    {
        if (LocationUtils.hasRequiredLocationPermissions(this)
                && LocationUtils.hasRequiredLocationSettings(this))
        {
            try
            {
                Intent locationServiceIntent = new Intent(this, LocationScheduleService.class);
                if (isLocationServiceEnabled())
                {
                    //nothing will happen if it's already running
                    if (!SystemUtils.isServiceRunning(this, LocationScheduleService.class))
                    {
                        startService(locationServiceIntent);
                    }
                }
                else
                {
                    //nothing will happen if it's not running
                    stopService(locationServiceIntent);
                }
            }
            catch (Exception e)
            {
                Crashlytics.logException(e);
            }
            //at most one service instance will be running
        }
    }

    private void showPermissionsBlocker()
    {
        if (getSupportFragmentManager().findFragmentByTag(
                PermissionsBlockerDialogFragment.FRAGMENT_TAG) == null)
        {
            FragmentUtils.safeLaunchDialogFragment(
                    new PermissionsBlockerDialogFragment(), this,
                    PermissionsBlockerDialogFragment.FRAGMENT_TAG);
        }
    }

    private void removeAllBlockersIfNeeded()
    {
        // Remove permission blocker if necessary
        Fragment fragmentByTag = getSupportFragmentManager().findFragmentByTag(
                PermissionsBlockerDialogFragment.FRAGMENT_TAG);
        if (fragmentByTag != null &&
                fragmentByTag instanceof PermissionsBlockerDialogFragment)
        {
            ((PermissionsBlockerDialogFragment) fragmentByTag).dismiss();
        }

        // Remove location settings blocker if necessary
        fragmentByTag = getSupportFragmentManager().findFragmentByTag(
                LocationSettingsBlockerDialogFragment.FRAGMENT_TAG);
        if (fragmentByTag != null &&
                fragmentByTag instanceof LocationSettingsBlockerDialogFragment)
        {
            ((LocationSettingsBlockerDialogFragment) fragmentByTag).dismiss();
        }
    }

    private boolean isEverythingGood()
    {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED &&
                LocationUtils.hasRequiredLocationSettings(this);
    }
    //endregion
}
