package com.handy.portal.data;

import android.content.Context;

import java.util.Properties;

import javax.inject.Inject;

import retrofit.Endpoint;

public final class HandyRetrofitEndpoint implements Endpoint
{
    enum Environment
    {
        P, S, Q1, Q2, Q3, Q4, Q6, D1, L
    }

    private Environment env = Environment.S;
    private Context context;
    private final String apiEndpoint;
    private final String apiEndpointInternal;
    private final String baseUrl;
    private final String baseUrlInternal;

    @Inject
    public HandyRetrofitEndpoint(Context context)
    {
        final Properties config = PropertiesReader.getProperties(context, "config.properties");
        apiEndpoint = config.getProperty("api_endpoint");
        apiEndpointInternal = config.getProperty("api_endpoint_internal");
        baseUrl = config.getProperty("base_url");
        baseUrlInternal = config.getProperty("base_url_internal");
    }

    final Environment getEnv()
    {
        return env;
    }

    final void setEnv(Environment env)
    {
        this.env = env;
    }

    @Override
    public final String getUrl()
    {
        switch (env)
        {
            case P:
                return apiEndpoint;

            case Q1:
                return apiEndpointInternal.replace("#", "q1");

            case Q2:
                return apiEndpointInternal.replace("#", "q2");

            case Q3:
                return apiEndpointInternal.replace("#", "q3");

            case Q4:
                return apiEndpointInternal.replace("#", "q4");

            case Q6:
                return apiEndpointInternal.replace("#", "q6");

            case D1:
                return apiEndpointInternal.replace("#", "d1");

            default:
                return apiEndpointInternal.replace("#", "s");
        }
    }

    public final String getBaseUrl()
    {
        switch (env)
        {
            case P:
                return baseUrl;

            case Q1:
                return baseUrlInternal.replace("#", "q1");

            case Q2:
                return baseUrlInternal.replace("#", "q2");

            case Q3:
                return baseUrlInternal.replace("#", "q3");

            case Q4:
                return baseUrlInternal.replace("#", "q4");

            case Q6:
                return baseUrlInternal.replace("#", "q6");

            case D1:
                return baseUrlInternal.replace("#", "d1");

            default:
                return baseUrlInternal.replace("#", "s");
        }
    }

    @Override
    public final String getName()
    {
        switch (env)
        {
            case P:
                return "Prod";

            case Q1:
                return "Q1";

            case Q2:
                return "Q2";

            case Q3:
                return "Q3";

            case Q4:
                return "Q4";

            case Q6:
                return "Q6";

            case D1:
                return "D1";

            default:
                return "Stage";
        }
    }
}
