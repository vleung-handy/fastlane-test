package com.handy.portal.constant;

public enum PrefsKey
{
    PROVIDER_ID("user_credentials_id"),
    AUTH_TOKEN("user_credentials");

    private String key;

    PrefsKey(String key)
    {
        this.key = key;
    }

    public String getKey()
    {
        return this.key;
    }

    @Override
    public String toString()
    {
        return this.getKey();
    }
}
