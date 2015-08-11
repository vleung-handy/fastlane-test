package com.handy.portal.core;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.multidex.MultiDex;

import com.crashlytics.android.Crashlytics;
import com.handy.portal.BuildConfig;
import com.handy.portal.R;
import com.handy.portal.analytics.Mixpanel;
import com.handy.portal.data.DataManager;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.manager.BookingManager;
import com.handy.portal.manager.ConfigManager;
import com.handy.portal.manager.GoogleManager;
import com.handy.portal.manager.HelpContactManager;
import com.handy.portal.manager.HelpManager;
import com.handy.portal.manager.LoginManager;
import com.handy.portal.manager.PrefsManager;
import com.handy.portal.manager.ProviderManager;
import com.handy.portal.manager.TermsManager;
import com.handy.portal.manager.UrbanAirshipManager;
import com.handy.portal.manager.VersionManager;
import com.handy.portal.util.TextUtils;
import com.newrelic.agent.android.NewRelic;
import com.squareup.otto.Bus;
import com.urbanairship.util.UriUtils;

import javax.inject.Inject;

import dagger.ObjectGraph;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class BaseApplication extends Application
{
    protected ObjectGraph graph;
    private int started;
    private boolean savedInstance;

    @Inject
    Mixpanel mixpanel;

    //We are injecting all of our event bus listening managers in BaseApplication to start them up for event listening
    @Inject
    DataManager dataManager;
    @Inject
    GoogleManager googleManager;
    @Inject
    BookingManager bookingManager;
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
    HelpManager helpManager;
    @Inject
    HelpContactManager helpContactManager;
    @Inject
    PrefsManager prefsManager;
    @Inject
    UrbanAirshipManager urbanAirshipManager;
    @Inject
    ApplicationOnResumeWatcher applicationOnResumeWatcher;

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

        //Start UA
        bus.post(new HandyEvent.StartUrbanAirship());

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath(TextUtils.Fonts.CIRCULAR_BOOK)
                .setFontAttrId(R.attr.fontPath)
                .build());

//        if (BuildConfig.FLAVOR.equals(BaseApplication.FLAVOR_PROD)) {
//            NewRelic.withApplicationToken("AA7a37dccf925fd1e474142399691d1b6b3f84648b").start(this);
//        }
//        else {
//            NewRelic.withApplicationToken("AAbaf8c55fb9788d1664e82661d94bc18ea7c39aa6").start(this);
//        }

        System.out.println("Checking uri validation");


        String a1 = "http://deeplink/booking_details?{Booking Id}";
        Uri uri = UriUtils.parse(a1);
        if(a1 != null)
        {
            System.out.println(" a 1 is good");
        }



        a1 = "deeplink/available_jobs";
        uri = UriUtils.parse(a1);
        if(a1 != null)
        {
            System.out.println(" a 2 is good");
        }


        a1 = "//deeplink/available_jobs";
        uri = UriUtils.parse(a1);
        if(a1 != null)
        {
            System.out.println(" a 3 is good");
        }

        a1 = "http://deeplink/booking_details?";
        uri = UriUtils.parse(a1);
        if(a1 != null)
        {
            System.out.println(" a 4 is good");
        }

        a1 = "http://deeplink/booking_details?2745";
        uri = UriUtils.parse(a1);
        if(a1 != null)
        {
            System.out.println(" a 5 is good");
        }




        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks()
        {
            @Override
            public void onActivityCreated(final Activity activity,
                                          final Bundle savedInstanceState)
            {
                savedInstance = savedInstanceState != null;
            }

            @Override
            public void onActivityStarted(final Activity activity)
            {
                ++started;

                if (started == 1)
                {
                    if (!savedInstance) mixpanel.trackEventAppOpened(true);
                    else mixpanel.trackEventAppOpened(false);
                }
            }

            @Override
            public void onActivityResumed(final Activity activity)
            {
            }

            @Override
            public void onActivityPaused(final Activity activity)
            {
            }

            @Override
            public void onActivityStopped(final Activity activity)
            {
            }

            @Override
            public void onActivitySaveInstanceState(final Activity activity,
                                                    final Bundle outState)
            {
            }

            @Override
            public void onActivityDestroyed(final Activity activity)
            {
            }
        });
    }

    @Override
    protected void attachBaseContext(Context base)
    {
        super.attachBaseContext(base);
        installMultiDex();
    }

    protected void installMultiDex()
    {
        MultiDex.install(this);
    }

    protected void startNewRelic()
    {
        String newRelicApiKey = PropertiesReader.getConfigProperties(this).getProperty("newrelic_api_key");
        NewRelic.withApplicationToken(newRelicApiKey).start(this);
    }

    protected void startCrashlytics()
    {
        if(!BuildConfig.DEBUG)
        {
            Crashlytics.start(this);
        }
    }

    protected void createObjectGraph()
    {
        graph = ObjectGraph.create(new ApplicationModule(this));
    }

    public final void inject(final Object object)
    {
        graph.inject(object);
    }

}
