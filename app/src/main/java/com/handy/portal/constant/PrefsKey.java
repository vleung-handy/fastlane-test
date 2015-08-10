package com.handy.portal.constant;

public enum PrefsKey
{
    LAST_PROVIDER_ID("user_credentials_id"),
    AUTH_TOKEN("user_credentials"),
    ONBOARDING_COMPLETED("onboarding_completed");

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
