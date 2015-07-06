package com.handy.portal.core;

import com.handy.portal.data.DataManager;

import javax.inject.Inject;

public class ConfigManager
{
    public static final String KEY_HOURS_SPANNING_AVAILABLE_BOOKINGS = "Hours to Start Sending Messages";
    private static final String[] CONFIG_PARAM_KEYS = {KEY_HOURS_SPANNING_AVAILABLE_BOOKINGS};

    private final DataManager dataManager;
    private ConfigParams configParams;

    @Inject
    ConfigManager(final DataManager dataManager)
    {
        this.dataManager = dataManager;
        this.configParams = new ConfigParams();
    }

    public void init()
    {
        dataManager.getConfigParams(CONFIG_PARAM_KEYS, new DataManager.Callback<ConfigParams>()
        {
            @Override
            public void onSuccess(ConfigParams configParamMap)
            {
                ConfigManager.this.configParams = configParamMap;
            }

            @Override
            public void onError(DataManager.DataManagerError error)
            {
            }
        });
    }

    public int getConfigParamValue(String key, int defaultValue)
    {
        Integer value = this.configParams.get(key);
        if (value != null)
        {
            return value;
        }
        return defaultValue;
    }
}
