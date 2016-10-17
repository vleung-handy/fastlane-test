package com.handy.portal.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.handy.portal.constant.PrefsKey;
import com.handy.portal.library.util.PropertiesReader;
import com.securepreferences.SecurePreferences;

import java.util.Properties;

import javax.inject.Inject;

public class PrefsManager
{
    private static final String SECURE_PREFS_KEY = "secure_prefs_key";
    private static final String DEFAULT_PREFS = "prefs.xml";
    public static final String BOOKING_INSTRUCTIONS_PREFS = "booking_instructions_preferences";

    private final SharedPreferences mDefaultPrefs;
    private final SharedPreferences mSecureDefaultPrefs;
    private final SharedPreferences mBookingInstructionsPrefs;

    @Inject
    public PrefsManager(final Context context)
    {
        Properties configs = PropertiesReader.getConfigProperties(context);
        mDefaultPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        mSecureDefaultPrefs = new SecurePreferences(
                context, configs.getProperty(SECURE_PREFS_KEY), DEFAULT_PREFS);
        mBookingInstructionsPrefs = new SecurePreferences(
                context, configs.getProperty(SECURE_PREFS_KEY), BOOKING_INSTRUCTIONS_PREFS);

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

    public int getInt(@PrefsKey.Key String prefsKey, int defaultValue) {
        return mDefaultPrefs.getInt(prefsKey, defaultValue);
    }

    public boolean contains(String prefsKey) {
        return mDefaultPrefs.contains(prefsKey);
    }

    public boolean getBoolean(String prefsKey, boolean defaultValue)
    {
        return mDefaultPrefs.getBoolean(prefsKey, defaultValue);
    }

    public void setBoolean(String prefsKey, boolean value)
    {
        mSecureDefaultPrefs.edit().putBoolean(prefsKey, value).apply();
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

    public boolean containsSecure(String prefsKey) {
        return mSecureDefaultPrefs.contains(prefsKey);
    }

    public void removeValue(String prefsKey) {
        mDefaultPrefs.edit().remove(prefsKey).apply();
    }

    public void removeSecureValue(String prefsKey) {
        mSecureDefaultPrefs.edit().remove(prefsKey).apply();
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
        mDefaultPrefs.edit().clear().apply();
        mSecureDefaultPrefs.edit().clear().apply();
        mBookingInstructionsPrefs.edit().clear().apply();
    }

    public void clearButSaveEventLogs()
    {
        String eventLogs = getString(PrefsKey.EVENT_LOG_BUNDLES);
        mDefaultPrefs.edit().clear().apply();
        mSecureDefaultPrefs.edit().clear().apply();
        mBookingInstructionsPrefs.edit().clear().apply();
        setString(PrefsKey.EVENT_LOG_BUNDLES, eventLogs);
    }

    public void setBookingInstructions(String bookingId, String value)
    {
        mBookingInstructionsPrefs.edit().putString(bookingId, value).apply();
    }

    public String getBookingInstructions(String bookingId)
    {
        return mBookingInstructionsPrefs.getString(bookingId, "");
    }
}
