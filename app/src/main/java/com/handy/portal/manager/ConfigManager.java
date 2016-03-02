package com.handy.portal.manager;

import android.support.annotation.Nullable;

import com.crashlytics.android.Crashlytics;
import com.handy.portal.data.DataManager;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.model.ConfigurationResponse;
import com.squareup.otto.Bus;

import javax.inject.Inject;


//We currently point to both the config params and configuration end points
//We are going to deprecate the direct config params endpoint and access everything through the configuration response layer
public class ConfigManager
{
    private final Bus mBus;
    private final DataManager mDataManager;
    private ConfigurationResponse mConfigurationResponse;

    @Inject
    public ConfigManager(final Bus bus, final DataManager dataManager)
    {
        mDataManager = dataManager;
        mConfigurationResponse = null;
        mBus = bus;
        mBus.register(this);
    }

    public void prefetch()
    {
        mDataManager.getConfiguration(new DataManager.Callback<ConfigurationResponse>()
        {
            @Override
            public void onSuccess(ConfigurationResponse configurationResponse)
            {
                ConfigManager.this.mConfigurationResponse = configurationResponse;
                mBus.post(new HandyEvent.ReceiveConfigurationSuccess(configurationResponse));
                //TODO: we should make this request blocking
            }

            @Override
            public void onError(DataManager.DataManagerError error)
            {
                Crashlytics.log("Unable to get configuration response");
            }
        });

    }

    //TODO: should we break this down into methods to get specific fields or just give them the object?
    @Nullable
    public ConfigurationResponse getConfigurationResponse()
    {
        return mConfigurationResponse;
    }
}
