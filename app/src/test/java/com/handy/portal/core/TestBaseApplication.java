package com.handy.portal.core;

import android.content.Context;

import dagger.ObjectGraph;

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
    }

    @Override
    protected void startNewRelic()
    {
    }

}
