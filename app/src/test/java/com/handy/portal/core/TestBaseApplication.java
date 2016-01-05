package com.handy.portal.core;

import dagger.ObjectGraph;

public class TestBaseApplication extends BaseApplication
{
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
