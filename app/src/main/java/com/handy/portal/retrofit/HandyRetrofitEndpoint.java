package com.handy.portal.retrofit;

import android.content.Context;

import com.handy.portal.BuildConfig;
import com.handy.portal.core.PropertiesReader;

import java.util.Properties;

import javax.inject.Inject;

import retrofit.Endpoint;

public class HandyRetrofitEndpoint implements Endpoint
{

    private final String apiEndpoint;
    private final String baseUrl;

    @Inject
    public HandyRetrofitEndpoint(Context context)
    {
        final Properties config = PropertiesReader.getConfigProperties(context);
        apiEndpoint = config.getProperty("api_endpoint");
        baseUrl = config.getProperty("base_url");
    }

    @Override
    public String getUrl()
    {
        return apiEndpoint;
    }

    @Override
    public String getName()
    {
        return BuildConfig.FLAVOR;
    }

    public String getBaseUrl()
    {
        return baseUrl;
    }
}