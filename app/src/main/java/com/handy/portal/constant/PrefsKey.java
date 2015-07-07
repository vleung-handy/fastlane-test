package com.handy.portal.constant;

/**
 * Created by cdavis on 7/7/15.
 */
public enum PrefsKey
{
    USER_CREDENTIALS_ID_KEY("user_credentials_id");

    private String key;

    PrefsKey(String key)
    {
        this.key = key;
    }

    public String getKey()
    {
        return this.key;
    }
}
