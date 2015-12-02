package com.handy.portal.retrofit.stripe;

import android.content.Context;

import com.handy.portal.constant.UrlName;
import com.handy.portal.core.PropertiesReader;

import java.util.Properties;

import javax.inject.Inject;

import retrofit.Endpoint;

public class StripeRetrofitEndpoint implements Endpoint
{
    private final String baseUrl;

    @Inject
    public StripeRetrofitEndpoint(Context context)
    {
        final Properties config = PropertiesReader.getConfigProperties(context);
        baseUrl = config.getProperty(UrlName.STRIPE_BASE_URL);
    }

    @Override
    public String getUrl()
    {
        return baseUrl;
    }

    @Override
    public String getName()
    {
        return null;
    }

}
