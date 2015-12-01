package com.handy.portal.model.logs;

import android.os.Build;

import com.handy.portal.BuildConfig;
import com.handy.portal.core.BaseApplication;
import com.handy.portal.manager.ProviderManager;
import com.handy.portal.model.Provider;

public class EventLogFactory
{
    private static final String EVENT_CONTEXT_APP = "app";
    private static final String EVENT_TYPE_APP_OPEN = "app_open";

    private ProviderManager mProviderManager;

    public EventLogFactory(ProviderManager providerManager)
    {
        mProviderManager = providerManager;
    }

    public EventLog createAppOpenLog()
    {
        Provider provider = mProviderManager.getCachedActiveProvider();
        if (provider != null)
        {
            return new EventLog(Build.VERSION.RELEASE, BuildConfig.VERSION_NAME,
                    BaseApplication.getDeviceId(), System.currentTimeMillis(), provider.getId(),
                    provider.getVersionTrack(), EVENT_CONTEXT_APP, EVENT_TYPE_APP_OPEN);
        }
        else
        {
            return new EventLog(Build.VERSION.RELEASE, BuildConfig.VERSION_NAME,
                    BaseApplication.getDeviceId(), System.currentTimeMillis(), "", "",
                    EVENT_CONTEXT_APP, EVENT_TYPE_APP_OPEN);
        }

    }
}
