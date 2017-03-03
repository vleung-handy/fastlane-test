package com.handy.portal.core;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;

import dagger.ObjectGraph;
import io.fabric.sdk.android.Fabric;

public class TestBaseApplication extends BaseApplication {
    @SuppressWarnings("deprecation")
    @Override
    public void onCreate() {
        super.onCreate();
        // Enable location service
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Settings.Secure.putInt(getContentResolver(), Settings.Secure.LOCATION_MODE, Settings.Secure.LOCATION_MODE_HIGH_ACCURACY);
        }
        else {
            Settings.Secure.putString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED, "some string");
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        //This is needed to make the unit tests work with multidexing
        try {
            super.attachBaseContext(base);
        }
        catch (RuntimeException ignored) {
            //Multidex support doesn't play well with Robolectric yet
        }
    }

    @Override
    protected ObjectGraph createObjectGraph() {
        return ObjectGraph.create(new TestApplicationModule(this));
    }

    @Override
    protected void startCrashlytics() {
        /*
        initializing a disabled Crashlytics instance
        to prevent "java.lang.IllegalStateException: Must Initialize Fabric before using singleton()"
        when a unit test triggers a Crashlytics call
         */
        Crashlytics crashlytics = new Crashlytics.Builder().core(new CrashlyticsCore.Builder().disabled(true).build()).build();
        Fabric.with(this, crashlytics);
    }
}
