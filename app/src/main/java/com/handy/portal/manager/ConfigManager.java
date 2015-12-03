package com.handy.portal.manager;

import android.support.annotation.Nullable;

import com.crashlytics.android.Crashlytics;
import com.handy.portal.data.DataManager;
import com.handy.portal.model.ConfigParams;
import com.handy.portal.model.ConfigurationResponse;

import javax.inject.Inject;


//We currently point to both the config params and configuration end points
//We are going to deprecate the direct config params endpoint and access everything through the configuration response layer
public class ConfigManager
{
    public static final String KEY_HOURS_SPANNING_AVAILABLE_BOOKINGS = "Hours to Start Sending Messages";
    public static final String KEY_SHOW_BLOCK_JOB_SCHEDULES = "Show Block Job Schedules";
    public static final String KEY_PRO_CUSTOMER_FEEDBACK_ENABLED = "NATIVE_CHECKOUT_RATING_FLOW_ENABLED";

    private static final String[] CONFIG_PARAM_KEYS =
            {
                    KEY_HOURS_SPANNING_AVAILABLE_BOOKINGS,
                    KEY_PRO_CUSTOMER_FEEDBACK_ENABLED,
            };

    private final DataManager mDataManager;
    private ConfigParams mConfigParams;
    private ConfigurationResponse mConfigurationResponse;

    @Inject
    public ConfigManager(final DataManager dataManager)
    {
        this.mDataManager = dataManager;
        this.mConfigParams = new ConfigParams();
        this.mConfigurationResponse = null;
    }

    public void prefetch()
    {
        mDataManager.getConfigParams(CONFIG_PARAM_KEYS, new DataManager.Callback<ConfigParams>()
        {
            @Override
            public void onSuccess(ConfigParams configParamMap)
            {
                ConfigManager.this.mConfigParams = configParamMap;
            }

            @Override
            public void onError(DataManager.DataManagerError error)
            {
            }
        });

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

    @Deprecated
    public int getConfigParamValue(String key, int defaultValue)
    {
        Integer value = this.mConfigParams.get(key);
        if (value != null)
        {
            return value;
        }
        return defaultValue;
    }

    @Nullable
    public ConfigurationResponse getConfigurationResponse()
    {
        if (mConfigurationResponse == null)
        {
            Crashlytics.logException(new Exception("Tried to access configuration data before it was available"));
        }
        return mConfigurationResponse; //should we break this down into methods to get specific fields or just give them the object?
    }
}
