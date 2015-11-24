package com.handy.portal.manager;

import com.handy.portal.model.ConfigParams;
import com.handy.portal.data.DataManager;

import javax.inject.Inject;

public class ConfigManager
{
    public static final String KEY_HOURS_SPANNING_AVAILABLE_BOOKINGS = "Hours to Start Sending Messages";
    public static final String KEY_SHOW_BLOCK_JOB_SCHEDULES = "Show Block Job Schedules";

//TODO: This is not the real key, just a placeholder

    public static final String KEY_PRO_CUSTOMER_FEEDBACK_ENABLED = "Pro Customer Feedback Enabled";

    private static final String[] CONFIG_PARAM_KEYS = {KEY_HOURS_SPANNING_AVAILABLE_BOOKINGS, KEY_PRO_CUSTOMER_FEEDBACK_ENABLED};

    private final DataManager dataManager;
    private ConfigParams configParams;

    @Inject
    public ConfigManager(final DataManager dataManager)
    {
        this.dataManager = dataManager;
        this.configParams = new ConfigParams();
    }

    public void prefetch()
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
