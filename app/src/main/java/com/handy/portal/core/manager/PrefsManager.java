package com.handy.portal.core.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.handy.portal.core.constant.PrefsKey;
import com.handy.portal.library.util.PropertiesReader;
import com.securepreferences.SecurePreferences;

import java.util.Properties;
import java.util.UUID;

import javax.inject.Inject;

public class PrefsManager
{
    private static final String SECURE_PREFS_KEY = "secure_prefs_key";
    private static final String DEFAULT_PREFS = "prefs.xml";
    public static final String BOOKING_INSTRUCTIONS_PREFS = "booking_instructions_preferences";

    private final SharedPreferences mDefaultPrefs;
    private final SharedPreferences mBookingInstructionsPrefs;
    private final SharedPreferences mSecureDefaultPrefs;

    @Inject
    public PrefsManager(final Context context)
    {
        Properties configs = PropertiesReader.getConfigProperties(context);
        mDefaultPrefs = PreferenceManager.getDefaultSharedPreferences(context);

        mBookingInstructionsPrefs =
                context.getSharedPreferences(BOOKING_INSTRUCTIONS_PREFS, Context.MODE_PRIVATE);

        mSecureDefaultPrefs = new SecurePreferences(
                context, configs.getProperty(SECURE_PREFS_KEY), DEFAULT_PREFS);
    }

    public String getString(@PrefsKey.Key String prefsKey)
    {
        return getString(prefsKey, "");
    }

    public String getString(@PrefsKey.Key String prefsKey, String defaultValue)
    {
        return mDefaultPrefs.getString(prefsKey, defaultValue);
    }

    public void setString(@PrefsKey.Key String prefsKey, String value)
    {
        mDefaultPrefs.edit().putString(prefsKey, value).apply();
    }

    public int getInt(@PrefsKey.Key String prefsKey, int defaultValue)
    {
        return mDefaultPrefs.getInt(prefsKey, defaultValue);
    }

    public boolean contains(String prefsKey)
    {
        return mDefaultPrefs.contains(prefsKey);
    }

    public boolean getBoolean(String prefsKey, boolean defaultValue)
    {
        return mDefaultPrefs.getBoolean(prefsKey, defaultValue);
    }

    public void setBoolean(String prefsKey, boolean value)
    {
        mDefaultPrefs.edit().putBoolean(prefsKey, value).apply();
    }

    public String getSecureString(@PrefsKey.Key String prefsKey)
    {
        return getSecureString(prefsKey, "");
    }

    public String getSecureString(@PrefsKey.Key String prefsKey, String defaultValue)
    {
        return mSecureDefaultPrefs.getString(prefsKey, defaultValue);
    }

    public void setSecureString(@PrefsKey.Key String prefsKey, String value)
    {
        mSecureDefaultPrefs.edit().putString(prefsKey, value).apply();
    }

    public void removeValue(String prefsKey)
    {
        mDefaultPrefs.edit().remove(prefsKey).apply();
    }

    public boolean getSecureBoolean(String prefsKey, boolean defaultValue)
    {
        return mSecureDefaultPrefs.getBoolean(prefsKey, defaultValue);
    }

    public void setSecureBoolean(String prefsKey, boolean value)
    {
        mSecureDefaultPrefs.edit().putBoolean(prefsKey, value).apply();
    }

    public void clear()
    {
        String eventLogs = mDefaultPrefs.getString(PrefsKey.EVENT_LOG_BUNDLES, null);
        String installationId = mDefaultPrefs.getString(PrefsKey.INSTALLATION_ID, null);
        mDefaultPrefs.edit().clear().apply();
        mSecureDefaultPrefs.edit().clear().apply();
        mBookingInstructionsPrefs.edit().clear().apply();
        mDefaultPrefs.edit().putString(PrefsKey.EVENT_LOG_BUNDLES, eventLogs).apply();
        mDefaultPrefs.edit().putString(PrefsKey.INSTALLATION_ID, installationId).apply();
    }

    public void setBookingInstructions(String bookingId, String value)
    {
        mBookingInstructionsPrefs.edit().putString(bookingId, value).apply();
    }

    public String getBookingInstructions(String bookingId)
    {
        return mBookingInstructionsPrefs.getString(bookingId, "");
    }

    public String getInstallationId()
    {
        String installationId = mDefaultPrefs.getString(PrefsKey.INSTALLATION_ID, null);
        if (TextUtils.isEmpty(installationId))
        {
            installationId = System.currentTimeMillis() + "+" + UUID.randomUUID().toString();
            mDefaultPrefs.edit().putString(PrefsKey.INSTALLATION_ID, installationId).apply();
        }
        return installationId;
    }
}
