package com.handy.portal.constant;

public enum PrefsKey
{
    USER_CREDENTIALS_ID("user_credentials_id");

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
