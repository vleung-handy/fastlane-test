package com.handy.portal.manager;

import com.handy.portal.constant.PrefsKey;
import com.securepreferences.SecurePreferences;

import javax.inject.Inject;

public class PrefsManager
{
    private final SecurePreferences prefs;

    @Inject
    public PrefsManager(final SecurePreferences prefs)
    {
        this.prefs = prefs;
    }

    public String getString(PrefsKey prefsKey)
    {
        return getString(prefsKey, "");
    }

    public String getString(PrefsKey prefsKey, String defaultValue)
    {
        return(prefs.getString(prefsKey.getKey(), defaultValue));
    }

    public boolean getBoolean(PrefsKey prefsKey, boolean defaultValue)
    {
        return(prefs.getBoolean(prefsKey.getKey(), defaultValue));
    }

    public void setBoolean(PrefsKey prefsKey, boolean value)
    {
        prefs.edit().putBoolean(prefsKey.getKey(), value).apply();
    }

    public void setString(PrefsKey prefsKey, String value)
    {
        prefs.edit().putString(prefsKey.getKey(), value).apply();
    }

    public int getInt(PrefsKey prefsKey, int defaultValue)
    {
        return (prefs.getInt(prefsKey.getKey(), defaultValue));
    }

    public void setInt(PrefsKey prefsKey, int value)
    {
        prefs.edit().putInt(prefsKey.getKey(), value).apply();
    }

    public long getLong(PrefsKey prefsKey, long defaultValue)
    {
        return (prefs.getLong(prefsKey.getKey(), defaultValue));
    }

    public void setLong(PrefsKey prefsKey, long value)
    {
        prefs.edit().putLong(prefsKey.getKey(), value).apply();
    }
}
