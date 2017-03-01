package com.handy.portal.core;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

import com.handy.portal.R;
import com.handy.portal.core.constant.PrefsKey;
import com.handy.portal.core.manager.PrefsManager;
import com.handy.portal.library.util.PropertiesReader;
import com.handy.portal.library.util.TextUtils;

import java.util.Properties;

public class EnvironmentModifier {
    public static final String IP_ADDRESS_REGEX = "(\\d+\\.){3}\\d+";


    public enum Environment {
        Q(R.string.q),
        L(R.string.local),
        S(R.string.staging),;
        // TODO: Support pointing to production
        // P(R.string.production),;

        private int mDisplayNameResId;

        Environment(@StringRes final int displayNameResId) {
            mDisplayNameResId = displayNameResId;
        }

        public int getDisplayNameResId() {
            return mDisplayNameResId;
        }
    }


    private static final String DEFAULT_ENVIRONMENT = Environment.S.name();
    private final PrefsManager mPrefsManager;
    private boolean mIsPinRequestEnabled = true;

    public EnvironmentModifier(Context context, PrefsManager prefsManager) {
        mPrefsManager = prefsManager;
        try {
            Properties properties = PropertiesReader.getProperties(context, "override.properties");
            boolean disablePinRequest = Boolean.parseBoolean(properties.getProperty("disable_pin_request", "false"));
            mIsPinRequestEnabled = !disablePinRequest;
            String environmentPrefix = properties.getProperty("environment", null);
            environmentPrefix = prefsManager.getSecureString(PrefsKey.ENVIRONMENT_PREFIX, environmentPrefix); // whatever is stored in prefs is higher priority

            if (!TextUtils.isNullOrEmpty(environmentPrefix)) // this means it's an override to point to a Q/L environment
            {
                if (environmentPrefix.matches(IP_ADDRESS_REGEX)) {
                    prefsManager.setSecureString(PrefsKey.ENVIRONMENT, Environment.L.name());
                    prefsManager.setSecureString(PrefsKey.ENVIRONMENT_PREFIX, environmentPrefix);
                }
                else {
                    prefsManager.setSecureString(PrefsKey.ENVIRONMENT, Environment.Q.name());
                    prefsManager.setSecureString(PrefsKey.ENVIRONMENT_PREFIX, environmentPrefix);
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getEnvironmentPrefix() {
        return mPrefsManager.getSecureString(PrefsKey.ENVIRONMENT_PREFIX, null);
    }

    public Environment getEnvironment() {
        final String environmentName =
                mPrefsManager.getSecureString(PrefsKey.ENVIRONMENT, DEFAULT_ENVIRONMENT);
        return Environment.valueOf(environmentName);
    }

    public boolean isPinRequestEnabled() {
        return mIsPinRequestEnabled;
    }

    public void setEnvironment(final Environment environment,
                               @Nullable final String environmentPrefix,
                               @Nullable final OnEnvironmentChangedListener callback) {
        mPrefsManager.setSecureString(PrefsKey.ENVIRONMENT, environment.name());
        mPrefsManager.setSecureString(PrefsKey.ENVIRONMENT_PREFIX, environmentPrefix);
        if (callback != null) {
            callback.onEnvironmentChanged(environmentPrefix);
        }
    }

    public interface OnEnvironmentChangedListener {
        void onEnvironmentChanged(String newEnvironmentPrefix);
    }
}
