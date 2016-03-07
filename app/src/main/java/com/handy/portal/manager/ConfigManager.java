package com.handy.portal.manager;

import android.support.annotation.Nullable;

import com.crashlytics.android.Crashlytics;
import com.handy.portal.data.DataManager;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.model.ConfigurationResponse;
import com.handy.portal.model.OnboardingParams;
import com.squareup.otto.Bus;

import javax.inject.Inject;


//We currently point to both the config params and configuration end points
//We are going to deprecate the direct config params endpoint and access everything through the configuration response layer
public class ConfigManager
{
    private final Bus mBus;
    private final DataManager mDataManager;
    private ConfigurationResponse mConfigurationResponse;
    private boolean mRequestIsPending = false; //this is getting requested more because of onboarding, prevent dupe requests

    @Inject
    public ConfigManager(final DataManager dataManager, final Bus bus)
    {
        mDataManager = dataManager;
        mConfigurationResponse = null;
        mBus = bus;
        mBus.register(this);
    }

    public void prefetch()
    {
        //stop dupe requests in short time frame
        if (!mRequestIsPending)
        {
            mRequestIsPending = true;
            mDataManager.getConfiguration(new DataManager.Callback<ConfigurationResponse>()
            {
                @Override
                public void onSuccess(ConfigurationResponse configurationResponse)
                {
                    OnboardingParams.QUICKHACK++; //REMOVEME
                    mRequestIsPending = false;
                    ConfigManager.this.mConfigurationResponse = configurationResponse;
                    mBus.post(new HandyEvent.ReceiveConfigurationSuccess(configurationResponse));
                }

                @Override
                public void onError(DataManager.DataManagerError error)
                {
                    mRequestIsPending = false;
                    Crashlytics.log("Unable to get configuration response");
                }
            });
        }
    }

    //TODO: should we break this down into methods to get specific fields or just give them the object?
    @Nullable
    public ConfigurationResponse getConfigurationResponse()
    {
        if (mConfigurationResponse == null)
        {
            Crashlytics.logException(new Exception("Tried to access configuration data before it was available"));
        }
        return mConfigurationResponse;
    }
}
