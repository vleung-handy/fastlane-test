package com.handy.portal.core;

import dagger.ObjectGraph;

public class CoreTestApplication extends BaseApplication {

    @Override
    protected void createObjectGraph() {
        mGraph = ObjectGraph.create(new CoreTestApplicationModule(this));
    }
}
