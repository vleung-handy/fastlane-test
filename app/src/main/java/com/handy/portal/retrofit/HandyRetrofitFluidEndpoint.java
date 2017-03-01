package com.handy.portal.retrofit;

import android.content.Context;

import com.handy.portal.core.EnvironmentModifier;
import com.handy.portal.core.constant.UrlName;
import com.handy.portal.library.util.PropertiesReader;
import com.handy.portal.library.util.TextUtils;

import java.text.MessageFormat;
import java.util.Properties;

public class HandyRetrofitFluidEndpoint extends HandyRetrofitEndpoint {
    private static final String PROVISIONED_Q_PREFIX = "q";
    private static final String DOMAIN_HBINTERNAL = "hbinternal";
    private static final String DOMAIN_HANDY_INTERNAL = "handy-internal";
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
            url = super.getUrl();
        }
        return formatUrl(url);
    }

    private String formatUrl(String url) {
        final String environmentPrefix = mEnvironmentModifier.getEnvironmentPrefix();
        if (!TextUtils.isNullOrEmpty(environmentPrefix)) {
            if (mEnvironmentModifier.getEnvironment() == EnvironmentModifier.Environment.Q) {
                // Q could be spun up through docker which will give it "handy-internal.com" domain
                // or through normal Q provisioning which will give it "hbinternal.com" domain.
                if (environmentPrefix.startsWith(PROVISIONED_Q_PREFIX)) {
                    return MessageFormat.format(url, environmentPrefix, DOMAIN_HBINTERNAL);
                }
                else {
                    return MessageFormat.format(url, environmentPrefix, DOMAIN_HANDY_INTERNAL);
                }

            }
            else {
                return MessageFormat.format(url, environmentPrefix);
            }
        }
        else {
            return url;
        }
    }
}
