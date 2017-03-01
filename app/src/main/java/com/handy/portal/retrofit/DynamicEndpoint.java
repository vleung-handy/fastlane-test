package com.handy.portal.retrofit;

import retrofit.Endpoint;

public class DynamicEndpoint implements Endpoint {
    private String mUrl;

    @Override
    public String getUrl() {
        return mUrl;
    }

    @Override
    public String getName() {
        return DynamicEndpoint.class.getSimpleName();
    }

    public void setUrl(final String url) {
        mUrl = url;
    }
}
