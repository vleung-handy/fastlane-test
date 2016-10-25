package com.handy.portal.logger.handylogger.model;

import com.google.gson.annotations.SerializedName;

public class EventSuperProperties extends EventSuperPropertiesBase
{
    public static final String PROVIDER_ID = "provider_id";
    @SerializedName(PROVIDER_ID)
    private int mProviderId;

    public EventSuperProperties(final int providerId)
    {
        super();
        mProviderId = providerId;
    }
}
