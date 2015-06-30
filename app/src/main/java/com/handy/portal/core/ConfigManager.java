package com.handy.portal.core;

import com.handy.portal.data.DataManager;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

public class ConfigManager
{
    public static final String KEY_HOURS_SPANNING_AVAILABLE_BOOKINGS = "Hours to Start Sending Messages";
    private static final String[] CONFIG_PARAM_KEYS = {KEY_HOURS_SPANNING_AVAILABLE_BOOKINGS};

    private final DataManager dataManager;
    private Map<String, Integer> configParamMap;

    @Inject
    ConfigManager(final DataManager dataManager)
    {
        this.dataManager = dataManager;
        this.configParamMap = new HashMap<>();
    }

    public void init()
    {
        dataManager.getConfigParams(CONFIG_PARAM_KEYS, new DataManager.Callback<Map<String, Integer>>()
        {
            @Override
            public void onSuccess(Map<String, Integer> configParamMap)
            {
                ConfigManager.this.configParamMap = configParamMap;
            }

            @Override
            public void onError(DataManager.DataManagerError error)
            {
            }
        });
    }

    public int getConfigParamValue(String key, int defaultValue)
    {
        Integer value = this.configParamMap.get(key);
        if (value != null)
        {
            return value;
        }
        return defaultValue;
    }
}
