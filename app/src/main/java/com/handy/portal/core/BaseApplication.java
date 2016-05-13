package com.handy.portal.core;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.multidex.MultiDexApplication;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.handy.portal.BuildConfig;
import com.handy.portal.R;
import com.handy.portal.bookings.BookingManager;
import com.handy.portal.bookings.BookingModalsManager;
import com.handy.portal.data.DataManager;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.library.util.PropertiesReader;
import com.handy.portal.location.manager.LocationManager;
import com.handy.portal.logger.handylogger.EventLogManager;
import com.handy.portal.logger.mixpanel.Mixpanel;
import com.handy.portal.manager.ConfigManager;
import com.handy.portal.manager.GoogleManager;
import com.handy.portal.manager.LoginManager;
import com.handy.portal.manager.MainActivityFragmentNavigationHelper;
import com.handy.portal.manager.PrefsManager;
import com.handy.portal.manager.ProviderManager;
import com.handy.portal.manager.RegionDefinitionsManager;
import com.handy.portal.manager.StripeManager;
import com.handy.portal.manager.SystemManager;
import com.handy.portal.manager.TabNavigationManager;
import com.handy.portal.manager.TermsManager;
import com.handy.portal.manager.UrbanAirshipManager;
import com.handy.portal.manager.UserInterfaceUpdateManager;
import com.handy.portal.manager.WebUrlManager;
import com.handy.portal.manager.ZipClusterManager;
import com.handy.portal.notification.NotificationMessageManager;
import com.handy.portal.payments.PaymentsManager;
import com.handy.portal.retrofit.HandyRetrofitEndpoint;
import com.handy.portal.updater.VersionManager;
import com.handy.portal.library.util.FontUtils;
import com.newrelic.agent.android.NewRelic;
import com.squareup.otto.Bus;

import javax.inject.Inject;

import dagger.ObjectGraph;
import io.fabric.sdk.android.Fabric;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class BaseApplication extends MultiDexApplication
{
    private static String sDeviceId = "";
    protected ObjectGraph mGraph;
    private int mStarted;
    private boolean mSavedInstance;

    @Inject
    Mixpanel mixpanel;

    //We are injecting all of our event bus listening managers in BaseApplication to start them up for event listening
    @Inject
    DataManager dataManager;
    @Inject
    HandyRetrofitEndpoint handyRetrofitEndpoint;
    @Inject
    GoogleManager googleManager;
    @Inject
    BookingManager bookingManager;
    @Inject
    BookingModalsManager bookingModalsManager;
    @Inject
    LoginManager loginManager;
    @Inject
    ProviderManager providerManager;
    @Inject
    VersionManager versionManager;
    @Inject
    TermsManager termsManager;
    @Inject
    ConfigManager configManager;
    @Inject
    PrefsManager prefsManager;
    @Inject
    PaymentsManager paymentsManager;
    @Inject
    ZipClusterManager zipClusterManager;
    @Inject
    StripeManager stripeManager;
    @Inject
    EventLogManager logEventsManager;
    @Inject
    RegionDefinitionsManager regionDefinitionsManager;
    @Inject
    UrbanAirshipManager urbanAirshipManager;
    @Inject
    MainActivityFragmentNavigationHelper mainActivityFragmentNavigationHelper;
    @Inject
    TabNavigationManager tabNavigationManager;
    @Inject
    WebUrlManager webUrlManager;
    @Inject
    NotificationMessageManager notificationMessageManager;
    @Inject
    LocationManager locationManager;
    @Inject
    SystemManager systemManager;
    @Inject
    UserInterfaceUpdateManager userInterfaceUpdateManager;

    @Inject
    Bus bus;

    @Override
    public final void onCreate()
    {
        super.onCreate();
        createObjectGraph();
        inject(this);

        startNewRelic();
        startCrashlytics();
        sDeviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        //Start UA
        bus.post(new HandyEvent.StartUrbanAirship());

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath(FontUtils.CIRCULAR_BOOK)
                .setFontAttrId(R.attr.fontPath)
                .build());

//        if (BuildConfig.FLAVOR.equals(BaseApplication.FLAVOR_PROD)) {
//            NewRelic.withApplicationToken("AA7a37dccf925fd1e474142399691d1b6b3f84648b").start(this);
//        }
//        else {
//            NewRelic.withApplicationToken("AAbaf8c55fb9788d1664e82661d94bc18ea7c39aa6").start(this);
//        }

        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks()
        {
            @Override
            public void onActivityCreated(final Activity activity,
                                          final Bundle savedInstanceState)
            {
                mSavedInstance = savedInstanceState != null;
            }

            @Override
            public void onActivityStarted(final Activity activity)
            {
                ++mStarted;

                if (mStarted == 1)
                {
                    if (!mSavedInstance) { mixpanel.trackEventAppOpened(true); }
                    else { mixpanel.trackEventAppOpened(false); }
                }
            }

            @Override
            public void onActivityResumed(final Activity activity) { }

            @Override
            public void onActivityPaused(final Activity activity) { }

            @Override
            public void onActivityStopped(final Activity activity) { }

            @Override
            public void onActivitySaveInstanceState(final Activity activity, final Bundle outState) { }

            @Override
            public void onActivityDestroyed(final Activity activity) { }
        });
    }

    protected void startNewRelic()
    {
        String newRelicApiKey = PropertiesReader.getConfigProperties(this).getProperty("newrelic_api_key");
        NewRelic.withApplicationToken(newRelicApiKey).start(this);
    }

    protected void startCrashlytics()
    {
        Crashlytics crashlytics = new Crashlytics.Builder().core(new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build()).build();
        Fabric.with(this, crashlytics);
    }

    protected void createObjectGraph()
    {
        mGraph = ObjectGraph.create(new ApplicationModule(this));
    }

    public final void inject(final Object object)
    {
        mGraph.inject(object);
    }

    public static String getDeviceId() { return sDeviceId; }

    public static String getDeviceModel()
    {
        final String manufacturer = Build.MANUFACTURER;
        final String model = Build.MODEL;

        if (model.startsWith(manufacturer))
        {
            return model;
        }
        else
        {
            return manufacturer + " " + model;
        }
    }
}
