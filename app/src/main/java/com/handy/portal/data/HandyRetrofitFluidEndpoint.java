package com.handy.portal.data;

import android.content.Context;

import java.text.MessageFormat;

public class HandyRetrofitFluidEndpoint extends HandyRetrofitEndpoint
{
    private final EnvironmentManager environmentManager;

    public HandyRetrofitFluidEndpoint(Context context, EnvironmentManager environmentManager)
    {
        super(context);
        this.environmentManager = environmentManager;
    }

    @Override
    public String getUrl()
    {
        return formatUrl(super.getUrl());
    }

    @Override
    public String getName()
    {
        return environmentManager.getEnvironment().getName();
    }

    @Override
    public String getBaseUrl()
    {
        return formatUrl(super.getBaseUrl());
    }

    private String formatUrl(String url)
    {
        return MessageFormat.format(url, environmentManager.getEnvironment().getPrefix());
    }
}
