package com.handy.portal.retrofit.logevents;

import android.content.Context;

import com.handy.portal.constant.UrlName;
import com.handy.portal.core.PropertiesReader;

import java.util.Properties;

import javax.inject.Inject;

import retrofit.Endpoint;

public class EventLogEndpoint implements Endpoint
{
    private final String mBaseUrl;

    @Inject
    public EventLogEndpoint(Context context)
    {
        final Properties config = PropertiesReader.getConfigProperties(context);
        mBaseUrl = config.getProperty(UrlName.LOG_EVENTS_BASE_URL);
    }

    @Override
    public String getUrl()
    {
        return mBaseUrl;
    }

    @Override
    public String getName()
    {
        return null;
    }

}
