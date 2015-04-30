package com.handy.portal.core;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.handy.portal.data.DataManager;
import com.handy.portal.data.Mixpanel;

import javax.inject.Inject;

import dagger.ObjectGraph;

public final class BaseApplication extends Application {
    public static final String FLAVOR_PROD = "prod";
    public static final String FLAVOR_STAGE = "stage";

    private ObjectGraph graph;
    private int started;
    private boolean savedInstance;

    @Inject UserManager userManager;
    @Inject DataManager dataManager;
    @Inject Mixpanel mixpanel;

    @Override
    public final void onCreate() {
        super.onCreate();
        graph = ObjectGraph.create(new ApplicationModule(this));
        inject(this);

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

        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(final Activity activity,
                                          final Bundle savedInstanceState) {
                savedInstance = savedInstanceState != null;
            }

            @Override
            public void onActivityStarted(final Activity activity) {
                ++started;

                if (started == 1) {
                    if (!savedInstance) mixpanel.trackEventAppOpened(true);
                    else mixpanel.trackEventAppOpened(false);
                    updateUser();
                }
            }

            @Override
            public void onActivityResumed(final Activity activity) {}

            @Override
            public void onActivityPaused(final Activity activity) {}

            @Override
            public void onActivityStopped(final Activity activity) {}

            @Override
            public void onActivitySaveInstanceState(final Activity activity,
                                                    final Bundle outState) {}

            @Override
            public void onActivityDestroyed(final Activity activity) {}
        });
    }

    public final void inject(final Object object) {
        graph.inject(object);
    }

    private void updateUser() {
        final User user = userManager.getCurrentUser();

        if (user != null) {
            dataManager.getUser(user.getId(), user.getAuthToken(), new DataManager.Callback<User>() {
                @Override
                public void onSuccess(final User updatedUser) {
                    userManager.setCurrentUser(updatedUser);
                }

                @Override
                public void onError(final DataManager.DataManagerError error) {}
            });
        }
    }
}
