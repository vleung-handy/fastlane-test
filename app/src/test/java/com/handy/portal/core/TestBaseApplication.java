package com.handy.portal.core;

import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;

import dagger.ObjectGraph;
import io.fabric.sdk.android.Fabric;

public class TestBaseApplication extends BaseApplication
{
    @Override
    protected void attachBaseContext(Context base)
    {
        //This is needed to make the unit tests work with multidexing
        try
        {
            super.attachBaseContext(base);
        }
        catch (RuntimeException ignored)
        {
            //Multidex support doesn't play well with Robolectric yet
        }
    }

    @Override
    protected void createObjectGraph()
    {
        mGraph = ObjectGraph.create(new TestApplicationModule(this));
    }

    @Override
    protected void startCrashlytics()
    {
        /*
        initializing a disabled Crashlytics instance
        to prevent "java.lang.IllegalStateException: Must Initialize Fabric before using singleton()"
        when a unit test triggers a Crashlytics call
         */
        Crashlytics crashlytics = new Crashlytics.Builder().core(new CrashlyticsCore.Builder().disabled(true).build()).build();
        Fabric.with(this, crashlytics);
    }

    @Override
    protected void startNewRelic()
    {
    }

}
