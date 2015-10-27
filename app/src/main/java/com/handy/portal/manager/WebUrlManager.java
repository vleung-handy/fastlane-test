package com.handy.portal.manager;

import android.support.annotation.StringDef;

import com.handy.portal.constant.MainViewTab;
import com.handy.portal.constant.PrefsKey;
import com.handy.portal.model.Provider;
import com.handy.portal.retrofit.HandyRetrofitEndpoint;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class WebUrlManager
{
    private static final String WEB_URL_PROVIDER_ID_TOKEN = ":id";

    @StringDef({BLOCK_JOBS_PAGE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface TargetPage {}
    public static final String BLOCK_JOBS_PAGE = "providers/"+WEB_URL_PROVIDER_ID_TOKEN+"/provider_schedules";

    private final ProviderManager mProviderManager;
    private final HandyRetrofitEndpoint mEndpoint;
    private final PrefsManager mPrefsManager;

    public WebUrlManager(final ProviderManager providerManager, final PrefsManager prefsManager, final HandyRetrofitEndpoint endpoint)
    {
        mProviderManager = providerManager;
        mEndpoint = endpoint;
        mPrefsManager = prefsManager;
    }

    public String constructUrlForTargetTab(MainViewTab targetTab)
    {
        String targetUrl = mEndpoint.getBaseUrl() + targetTab.getWebViewTarget();
        return replaceVariablesInUrl(targetUrl);
    }

    //need to replace certain tokens, currently just WEB_URL_PROVIDER_ID_TOKEN so hacking it in, if need to replace more make a smarter function
    private String replaceVariablesInUrl(String url)
    {
        Provider provider = mProviderManager.getCachedActiveProvider();
        //Try to use the cached provider, and if not available fall back to last logged in provider id
        String providerId = (provider != null ? provider.getId() : mPrefsManager.getString(PrefsKey.LAST_PROVIDER_ID));
        url = url.replace(WEB_URL_PROVIDER_ID_TOKEN, providerId);
        return url;
    }
}