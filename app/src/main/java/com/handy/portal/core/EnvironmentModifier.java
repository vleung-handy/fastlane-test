package com.handy.portal.core;

import android.content.Context;
import android.support.annotation.Nullable;

import com.handy.portal.constant.PrefsKey;
import com.handy.portal.manager.PrefsManager;

import java.util.Properties;

public class EnvironmentModifier
{
    private static final String DEFAULT_ENVIRONMENT_PREFIX = "s";
    private final PrefsManager prefsManager;
    private boolean pinRequestEnabled = true;

    public EnvironmentModifier(Context context, PrefsManager prefsManager)
    {
        this.prefsManager = prefsManager;

        try
        {
            Properties properties = PropertiesReader.getProperties(context, "override.properties");
            boolean disablePinRequest = Boolean.parseBoolean(properties.getProperty("disable_pin_request", "false"));
            String environment = properties.getProperty("environment", DEFAULT_ENVIRONMENT_PREFIX);
            environment = prefsManager.getString(PrefsKey.ENVIRONMENT_PREFIX, environment); // whatever is stored in prefs is higher priority

            this.pinRequestEnabled = !disablePinRequest;
            prefsManager.setString(PrefsKey.ENVIRONMENT_PREFIX, environment);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public String getEnvironmentPrefix()
    {
        return prefsManager.getString(PrefsKey.ENVIRONMENT_PREFIX, DEFAULT_ENVIRONMENT_PREFIX);
    }

    public boolean pinRequestEnabled()
    {
        return pinRequestEnabled;
    }

    public void setEnvironmentPrefix(String environmentPrefix, @Nullable OnEnvironmentChangedListener callback)
    {
        prefsManager.setString(PrefsKey.ENVIRONMENT_PREFIX, environmentPrefix);
        if (callback != null)
        {
            callback.onEnvironmentChanged(environmentPrefix);
        }
    }

    public interface OnEnvironmentChangedListener
    {
        void onEnvironmentChanged(String newEnvironmentPrefix);
    }
}
