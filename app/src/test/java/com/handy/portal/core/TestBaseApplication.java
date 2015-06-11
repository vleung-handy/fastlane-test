package com.handy.portal.core;

import dagger.ObjectGraph;

public class TestBaseApplication extends BaseApplication
{
    @Override
    protected void createObjectGraph()
    {
        this.graph = ObjectGraph.create(new TestApplicationModule());
    }

    @Override
    protected void startCrashlytics()
    {
    }
}
