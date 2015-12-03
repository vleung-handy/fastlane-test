package com.handy.portal.manager;

import android.support.annotation.Nullable;

import com.crashlytics.android.Crashlytics;
import com.handy.portal.data.DataManager;
import com.handy.portal.model.ConfigurationResponse;

import javax.inject.Inject;


//We currently point to both the config params and configuration end points
//We are going to deprecate the direct config params endpoint and access everything through the configuration response layer
public class ConfigManager
{
    private final DataManager mDataManager;
    private ConfigurationResponse mConfigurationResponse;

    @Inject
    public ConfigManager(final DataManager dataManager)
    {
        mDataManager = dataManager;
        mConfigurationResponse = null;
    }

    public void prefetch()
    {
        mDataManager.getConfiguration(new DataManager.Callback<ConfigurationResponse>()
        {
            @Override
            public void onSuccess(ConfigurationResponse configurationResponse)
            {
                ConfigManager.this.mConfigurationResponse = configurationResponse;
            }

            @Override
            public void onError(DataManager.DataManagerError error)
            {
            }
        });

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
