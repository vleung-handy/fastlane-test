package com.handy.portal.retrofit;

import android.content.Context;

import com.handy.portal.core.EnvironmentModifier;
import com.handy.portal.core.constant.UrlName;
import com.handy.portal.library.util.PropertiesReader;
import com.handy.portal.library.util.TextUtils;

import java.text.MessageFormat;
import java.util.Properties;

public class HandyRetrofitFluidEndpoint extends HandyRetrofitEndpoint {
    private final EnvironmentModifier mEnvironmentModifier;
    private final Properties mProperties;

    public HandyRetrofitFluidEndpoint(Context context, EnvironmentModifier environmentModifier) {
        super(context);
        mProperties = PropertiesReader.getConfigProperties(context);
        mEnvironmentModifier = environmentModifier;
    }

    @Override
    public String getUrl() {
        String url = mProperties.getProperty(UrlName.API_URL + "_" +
                mEnvironmentModifier.getEnvironment().name().toLowerCase());
        if (TextUtils.isNullOrEmpty(url)) {
            url = super.getUrl();
        }
        return formatUrl(url);
    }

    @Override
    public String getName() {
        return mEnvironmentModifier.getEnvironment().name().toLowerCase();
    }

    @Override
    public String getBaseUrl() {
        String url = mProperties.getProperty(UrlName.BASE_URL + "_" +
                mEnvironmentModifier.getEnvironment().name().toLowerCase());
        if (TextUtils.isNullOrEmpty(url)) {
            url = super.getBaseUrl();
        }
        return formatUrl(url);
    }

    private String formatUrl(String url) {
        final String environmentPrefix = mEnvironmentModifier.getEnvironmentPrefix();
        return MessageFormat.format(url, environmentPrefix);
    }
}
