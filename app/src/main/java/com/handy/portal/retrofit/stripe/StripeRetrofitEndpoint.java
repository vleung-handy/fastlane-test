package com.handy.portal.retrofit.stripe;

import android.content.Context;

import com.handy.portal.constant.UrlName;
import com.handy.portal.core.PropertiesReader;

import java.util.Properties;

import javax.inject.Inject;

public class StripeRetrofitEndpoint
{
    private final String baseUrl;

    @Inject
    public StripeRetrofitEndpoint(Context context)
    {
        final Properties config = PropertiesReader.getConfigProperties(context);
        baseUrl = config.getProperty(UrlName.STRIPE_BASE_URL);
    }

    public String getUrl()
    {
        return baseUrl;
    }

    public String getName()
    {
        return null;
    }

}
