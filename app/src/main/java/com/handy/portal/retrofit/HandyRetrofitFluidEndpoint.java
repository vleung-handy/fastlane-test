package com.handy.portal.retrofit;

import android.content.Context;

import com.handy.portal.core.EnvironmentModifier;

import java.text.MessageFormat;

public class HandyRetrofitFluidEndpoint extends HandyRetrofitEndpoint
{
    private final EnvironmentModifier environmentModifier;

    public HandyRetrofitFluidEndpoint(Context context, EnvironmentModifier environmentModifier)
    {
        super(context);
        this.environmentModifier = environmentModifier;
    }

    @Override
    public String getUrl()
    {
        return formatUrl(super.getUrl());
    }

    @Override
    public String getName()
    {
        return environmentModifier.getEnvironment().getName();
    }

    @Override
    public String getBaseUrl()
    {
        return formatUrl(super.getBaseUrl());
    }

    private String formatUrl(String url)
    {
        return MessageFormat.format(url, environmentModifier.getEnvironment().getPrefix());
    }
}
