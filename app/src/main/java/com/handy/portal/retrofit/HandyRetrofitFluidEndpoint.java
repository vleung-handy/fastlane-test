package com.handy.portal.retrofit;

import android.content.Context;

import com.handy.portal.core.EnvironmentSwitcher;

import java.text.MessageFormat;

public class HandyRetrofitFluidEndpoint extends HandyRetrofitEndpoint
{
    private final EnvironmentSwitcher environmentSwitcher;

    public HandyRetrofitFluidEndpoint(Context context, EnvironmentSwitcher environmentSwitcher)
    {
        super(context);
        this.environmentSwitcher = environmentSwitcher;
    }

    @Override
    public String getUrl()
    {
        return formatUrl(super.getUrl());
    }

    @Override
    public String getName()
    {
        return environmentSwitcher.getEnvironment().getName();
    }

    @Override
    public String getBaseUrl()
    {
        return formatUrl(super.getBaseUrl());
    }

    private String formatUrl(String url)
    {
        return MessageFormat.format(url, environmentSwitcher.getEnvironment().getPrefix());
    }
}
