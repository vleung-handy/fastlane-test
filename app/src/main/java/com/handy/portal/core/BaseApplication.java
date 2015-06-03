package com.handy.portal.core;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.crashlytics.android.Crashlytics;
import com.handy.portal.R;
import com.handy.portal.data.DataManager;
import com.handy.portal.data.Mixpanel;
import com.handy.portal.util.TextUtils;

import javax.inject.Inject;

import dagger.ObjectGraph;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class BaseApplication extends Application
{
    protected ObjectGraph graph;
    private int started;
    private boolean savedInstance;

    @Inject
    DataManager dataManager;
    @Inject
    Mixpanel mixpanel;

    @Override
    public final void onCreate()
    {
        super.onCreate();
        startCrashlytics();
        createObjectGraph();
        inject(this);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath(TextUtils.Fonts.CIRCULAR_BOOK)
                .setFontAttrId(R.attr.fontPath)
                .build());

//        final AirshipConfigOptions options = AirshipConfigOptions.loadDefaultOptions(this);
//        options.inProduction = BuildConfig.FLAVOR.equals(BaseApplication.FLAVOR_PROD);
//
//        UAirship.takeOff(this, options, new UAirship.OnReadyCallback() {
//            @Override
//            public void onAirshipReady(final UAirship airship) {
//                final DefaultNotificationFactory defaultNotificationFactory =
//                        new DefaultNotificationFactory(getApplicationContext());
//
//                defaultNotificationFactory.setColor(getResources().getColor(R.color.handy_blue));
//                defaultNotificationFactory.setSmallIconId(R.drawable.ic_notification);
//
//                airship.getPushManager().setNotificationFactory(defaultNotificationFactory);
//                airship.getPushManager().setPushEnabled(false);
//                airship.getPushManager().setUserNotificationsEnabled(false);
//            }
//        });
//
//        CalligraphyConfig.initDefault("fonts/CircularStd-Book.otf", R.attr.fontPath);
//
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

    protected void startCrashlytics()
    {
        Crashlytics.start(this);
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
