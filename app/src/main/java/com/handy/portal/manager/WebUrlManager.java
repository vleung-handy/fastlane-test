package com.handy.portal.manager;

import android.support.annotation.StringDef;

import com.crashlytics.android.Crashlytics;
import com.handy.portal.constant.MainViewTab;
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

    public WebUrlManager(final ProviderManager providerManager, final HandyRetrofitEndpoint endpoint)
    {
        mProviderManager = providerManager;
        mEndpoint = endpoint;
    }

    public String constructUrlForTargetTab(MainViewTab targetTab)
    {
        String targetUrl = mEndpoint.getBaseUrl() + targetTab.getWebViewTarget();
        String constructedUrl = replaceVariablesInUrl(targetUrl);
        System.out.println("ConstructedUrl URL is : " + constructedUrl);
        return constructedUrl;
    }

    public String replaceVariablesInUrl(String url)
    {
        //need to replace certain tokens
        String providerIdReplacement = ":id";
        Provider provider = mProviderManager.getCachedActiveProvider();
        if(provider == null)
        {
            Crashlytics.log("replaceVariablesInUrl : called before cached provider retrieved");
        }
        String providerId = (provider != null ? provider.getId() : "");
        url = url.replace(providerIdReplacement, providerId);
        return url;
    }

}
