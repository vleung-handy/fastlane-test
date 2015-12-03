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
        return new EventLog(Build.VERSION.RELEASE, BuildConfig.VERSION_NAME,
                BaseApplication.getDeviceId(), System.currentTimeMillis(), getProviderId(),
                getVersionTrack(), EVENT_CONTEXT_APP, EVENT_TYPE_APP_OPEN);
    }

    public EventLog createReferralSelectedLog()
    {
        return new ProfileLog.ReferralSelectedLog(Build.VERSION.RELEASE, BuildConfig.VERSION_NAME,
                BaseApplication.getDeviceId(), System.currentTimeMillis(), getProviderId(),
                getVersionTrack());
    }

    public EventLog createResupplyKitSelectedLog()
    {
        return new ProfileLog.ResupplyKitSelectedLog(Build.VERSION.RELEASE, BuildConfig.VERSION_NAME,
                BaseApplication.getDeviceId(), System.currentTimeMillis(), getProviderId(),
                getVersionTrack());
    }

    public EventLog createResupplyKitConfirmedLog()
    {
        return new ProfileLog.ResupplyKitConfirmedLog(Build.VERSION.RELEASE, BuildConfig.VERSION_NAME,
                BaseApplication.getDeviceId(), System.currentTimeMillis(), getProviderId(),
                getVersionTrack());
    }

    public EventLog createEditProfileSelectedLog()
    {
        return new ProfileLog.EditProfileSelectedLog(Build.VERSION.RELEASE, BuildConfig.VERSION_NAME,
                BaseApplication.getDeviceId(), System.currentTimeMillis(), getProviderId(),
                getVersionTrack());
    }

    public EventLog createEditProfileConfirmedLog()
    {
        return new ProfileLog.EditProfileConfirmedLog(Build.VERSION.RELEASE, BuildConfig.VERSION_NAME,
                BaseApplication.getDeviceId(), System.currentTimeMillis(), getProviderId(),
                getVersionTrack());
    }

    public EventLog createHelpContactFormSubmittedLog(String path, int helpNodeId, String helpNodeTitle)
    {
        return new HelpContactFormSubmittedLog(Build.VERSION.RELEASE, BuildConfig.VERSION_NAME,
                BaseApplication.getDeviceId(), System.currentTimeMillis(), getProviderId(),
                getVersionTrack(), path, helpNodeId, helpNodeTitle);
    }

    private String getProviderId()
    {
        Provider provider = mProviderManager.getCachedActiveProvider();
        if (provider != null && provider.getId() != null)
        {
            return provider.getId();
        }
        else
        {
            return "";
        }
    }

    private String getVersionTrack()
    {
        Provider provider = mProviderManager.getCachedActiveProvider();
        if (provider != null && provider.getVersionTrack() != null)
        {
            return provider.getVersionTrack();
        }
        else
        {
            return "";
        }
    }
}
