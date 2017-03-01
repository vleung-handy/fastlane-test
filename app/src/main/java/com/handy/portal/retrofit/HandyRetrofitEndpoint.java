package com.handy.portal.retrofit;

import android.content.Context;

import com.handy.portal.core.constant.UrlName;
import com.handy.portal.library.util.PropertiesReader;

import java.util.Properties;

import javax.inject.Inject;

import retrofit.Endpoint;

public class HandyRetrofitEndpoint implements Endpoint {
    private static final String PROD_IDENTIFIER = "p";

    private final String apiEndpoint;
    private final String baseUrl;

    @Inject
    public HandyRetrofitEndpoint(Context context) {
        final Properties config = PropertiesReader.getConfigProperties(context);
        apiEndpoint = config.getProperty(UrlName.API_URL);
        baseUrl = config.getProperty(UrlName.BASE_URL);
    }

    @Override
    public String getUrl() {
        return apiEndpoint;
    }

    @Override
    public String getName() {
        return PROD_IDENTIFIER;
    }

    public String getBaseUrl() {
        return baseUrl;
    }
}
