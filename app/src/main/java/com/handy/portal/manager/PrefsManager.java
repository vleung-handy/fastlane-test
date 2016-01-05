package com.handy.portal.manager;

import android.content.SharedPreferences;

import com.handy.portal.constant.PrefsKey;

import javax.inject.Inject;

public class PrefsManager
{
    private final SharedPreferences mPrefs;

    @Inject
    public PrefsManager(final SharedPreferences prefs)
    {
        mPrefs = prefs;
    }

    public String getString(@PrefsKey.Key String prefsKey)
    {
        return getString(prefsKey, "");
    }

    public String getString(@PrefsKey.Key String prefsKey, String defaultValue)
    {
        return mPrefs.getString(prefsKey, defaultValue);
    }

    public void setString(@PrefsKey.Key String prefsKey, String value)
    {
        mPrefs.edit().putString(prefsKey, value).apply();
    }

    public boolean getBoolean(@PrefsKey.Key String prefsKey, boolean defaultValue)
    {
        return mPrefs.getBoolean(prefsKey, defaultValue);
    }

    public void setBoolean(@PrefsKey.Key String prefsKey, boolean value)
    {
        mPrefs.edit().putBoolean(prefsKey, value).apply();
    }

    public int getInt(@PrefsKey.Key String prefsKey, int defaultValue)
    {
        return mPrefs.getInt(prefsKey, defaultValue);
    }

    public void setInt(@PrefsKey.Key String prefsKey, int value)
    {
        mPrefs.edit().putInt(prefsKey, value).apply();
    }

    public long getLong(@PrefsKey.Key String prefsKey, long defaultValue)
    {
        return mPrefs.getLong(prefsKey, defaultValue);
    }

    public void setLong(@PrefsKey.Key String prefsKey, long value)
    {
        mPrefs.edit().putLong(prefsKey, value).apply();
    }
}
