package com.handy.portal.manager;

import com.handy.portal.constant.PrefsKey;
import com.securepreferences.SecurePreferences;

import javax.inject.Inject;

/**
 * Created by cdavis on 7/7/15.
 */
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
        String value = prefs.getString(prefsKey.getKey(), "");
        return value;
    }

    public void setString(PrefsKey prefsKey, String value)
    {
        prefs.edit().putString(prefsKey.getKey(), value).apply();
    }
}
