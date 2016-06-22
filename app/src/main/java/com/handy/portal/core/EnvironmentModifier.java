package com.handy.portal.core;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

import com.handy.portal.R;
import com.handy.portal.constant.PrefsKey;
import com.handy.portal.library.util.PropertiesReader;
import com.handy.portal.manager.PrefsManager;

import java.util.Properties;

public class EnvironmentModifier
{
    public enum Environment
    {
        Q(R.string.q),
        L(R.string.local),
        MS(R.string.mobile_staging),
        S(R.string.staging),;
        // TODO: Support pointing to production
        // P(R.string.production),;

        private int mDisplayNameResId;

        Environment(@StringRes final int displayNameResId)
        {
            mDisplayNameResId = displayNameResId;
        }

        public int getDisplayNameResId()
        {
            return mDisplayNameResId;
        }
    }


    private static final String DEFAULT_ENVIRONMENT = Environment.MS.name();
    private final PrefsManager mPrefsManager;
    private boolean mIsPinRequestEnabled = true;

    public EnvironmentModifier(Context context, PrefsManager prefsManager)
    {
        mPrefsManager = prefsManager;
        try
        {
            Properties properties = PropertiesReader.getProperties(context, "override.properties");
            boolean disablePinRequest = Boolean.parseBoolean(properties.getProperty("disable_pin_request", "false"));
            mIsPinRequestEnabled = !disablePinRequest;
            String environmentPrefix = properties.getProperty("environment", null);
            environmentPrefix = prefsManager.getString(PrefsKey.ENVIRONMENT_PREFIX, environmentPrefix); // whatever is stored in prefs is higher priority

            if (environmentPrefix != null && environmentPrefix.startsWith("q")) // this means it's an override to point to a Q environment
            {
                prefsManager.setString(PrefsKey.ENVIRONMENT, Environment.Q.name());
                prefsManager.setString(PrefsKey.ENVIRONMENT_PREFIX, environmentPrefix);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public String getEnvironmentPrefix()
    {
        return mPrefsManager.getString(PrefsKey.ENVIRONMENT_PREFIX, null);
    }

    public Environment getEnvironment()
    {
        final String environmentName =
                mPrefsManager.getString(PrefsKey.ENVIRONMENT, DEFAULT_ENVIRONMENT);
        return Environment.valueOf(environmentName);
    }

    public boolean isPinRequestEnabled()
    {
        return mIsPinRequestEnabled;
    }

    public void setEnvironment(final Environment environment,
                               @Nullable final String environmentPrefix,
                               @Nullable final OnEnvironmentChangedListener callback)
    {
        mPrefsManager.setString(PrefsKey.ENVIRONMENT, environment.name());
        mPrefsManager.setString(PrefsKey.ENVIRONMENT_PREFIX, environmentPrefix);
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
