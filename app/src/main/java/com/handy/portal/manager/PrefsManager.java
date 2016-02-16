package com.handy.portal.manager;

import android.content.Context;
import android.content.SharedPreferences;

import com.handy.portal.constant.PrefsKey;
import com.handy.portal.core.PropertiesReader;
import com.securepreferences.SecurePreferences;

import java.util.Properties;

import javax.inject.Inject;

public class PrefsManager
{
    private static final String SECURE_PREFS_KEY = "secure_prefs_key";
    private static final String DEFAULT_PREFS = "prefs.xml";
    private static final String BOOKING_INSTRUCTIONS_PREFS = "booking_instructions_preferences";

    private final SharedPreferences mDefaultPrefs;
    private final SharedPreferences mBookingInstructionsPrefs;

    @Inject
    public PrefsManager(final Context context)
    {
        Properties configs = PropertiesReader.getConfigProperties(context);
        mDefaultPrefs = new SecurePreferences(
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

    public boolean getBoolean(@PrefsKey.Key String prefsKey, boolean defaultValue)
    {
        return mDefaultPrefs.getBoolean(prefsKey, defaultValue);
    }

    public void setBoolean(@PrefsKey.Key String prefsKey, boolean value)
    {
        mDefaultPrefs.edit().putBoolean(prefsKey, value).apply();
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
