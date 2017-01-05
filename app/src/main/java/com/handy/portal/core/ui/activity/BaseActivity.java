package com.handy.portal.core.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.handy.portal.core.constant.BundleKeys;
import com.handy.portal.core.event.HandyEvent;
import com.handy.portal.core.manager.AppseeManager;
import com.handy.portal.core.manager.ConfigManager;
import com.handy.portal.core.manager.PrefsManager;
import com.handy.portal.core.model.ConfigurationResponse;
import com.handy.portal.flow.Flow;
import com.handy.portal.library.util.Utils;
import com.handy.portal.location.LocationUtils;
import com.handy.portal.logger.handylogger.LogEvent;
import com.handy.portal.logger.handylogger.model.AppLog;
import com.handy.portal.logger.handylogger.model.DeeplinkLog;
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

public abstract class BaseActivity extends AppCompatActivity implements AppUpdateFlowLauncher
{
    @Inject
    PrefsManager mPrefsManager;
    @Inject
    ConfigManager mConfigManager;
    @Inject
    public EventBus bus;
    @Inject
    AppseeManager mAppseeManager;

    private AppUpdateEventListener mAppUpdateEventListener;
    protected boolean allowCallbacks;
    private Stack<OnBackPressedListener> onBackPressedListenerStack;

    //According to android docs this is the preferred way of accessing location instead of using LocationManager
    //will also let us do geofencing and reverse address lookup which is nice
    //This is a clear instance where a service would be great but it is too tightly coupled to an activity to break out
    private SetupHandler mSetupHandler;
    private boolean mWasOpenBefore;

    // this is meant to be optionally overridden
    protected boolean shouldTriggerSetup()
    {
        return false;
    }

    // this is meant to be optionally overridden
    protected void onSetupComplete(final SetupData setupData) {}

    // this is meant to be optionally overridden
    protected void onSetupFailure() {}

    //Public Properties
    public boolean getAllowCallbacks()
    {
        return this.allowCallbacks;
    }

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
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        LocationUtils.showLocationBlockersOrStartServiceIfNecessary(this, isLocationServiceEnabled());
        bus.post(new LogEvent.SendLogsEvent());
        if (mWasOpenBefore)
        {
            bus.post(new LogEvent.AddLogEvent(new AppLog.AppOpenLog(false, false)));
        }

        /**
         * start/stop appsee recording as necessary (ex. based on device space and configs)
         * when user resumes the activity. nothing happens if we start Appsee recording if it's already started
         *
         * NOTE: according to docs Appsee.start() can only be called in
         * Activity.onCreate() or Activity.onResume()
         *
         * putting in this method rather than Activity.onCreate()
         * because most of the app is in one activity and Activity.onCreate() isn't triggered often enough
         *
         * although it is only necessary to start Appsee in activities that are entry points to the app
         * putting this here because we may want to start/stop recording when configs change
         */
        mAppseeManager.startOrStopRecordingAsNecessary();
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
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        allowCallbacks = false;
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
                            mBaseActivity.onSetupComplete(setupData);
                        }
                    })
                    .start();
            bus.unregister(SetupHandler.this);
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
        LocationUtils.showLocationBlockersOrStartServiceIfNecessary(this, isLocationServiceEnabled());
    }
}
