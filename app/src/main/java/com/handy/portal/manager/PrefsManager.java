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

    public String getString(@PrefsKey.Key String prefsKey)
    {
        return getString(prefsKey, "");
    }

    public String getString(@PrefsKey.Key String prefsKey, String defaultValue)
    {
        return prefs.getString(prefsKey, defaultValue);
    }

    public void setString(@PrefsKey.Key String prefsKey, String value)
    {
        prefs.edit().putString(prefsKey, value).apply();
    }

    public boolean getBoolean(@PrefsKey.Key String prefsKey, boolean defaultValue)
    {
        return prefs.getBoolean(prefsKey, defaultValue);
    }

    public void setBoolean(@PrefsKey.Key String prefsKey, boolean value)
    {
        prefs.edit().putBoolean(prefsKey, value).apply();
    }

    public int getInt(@PrefsKey.Key String prefsKey, int defaultValue)
    {
        return prefs.getInt(prefsKey, defaultValue);
    }

    public void setInt(@PrefsKey.Key String prefsKey, int value)
    {
        prefs.edit().putInt(prefsKey, value).apply();
    }

    public long getLong(@PrefsKey.Key String prefsKey, long defaultValue)
    {
        return prefs.getLong(prefsKey, defaultValue);
    }

    public void setLong(@PrefsKey.Key String prefsKey, long value)
    {
        prefs.edit().putLong(prefsKey, value).apply();
    }
}
