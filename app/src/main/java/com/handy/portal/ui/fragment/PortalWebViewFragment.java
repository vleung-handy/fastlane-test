package com.handy.portal.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.handy.portal.R;
import com.handy.portal.core.PortalWebViewClient;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class PortalWebViewFragment extends InjectedFragment
{
    @InjectView(R.id.web_view_portal)
    WebView webView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_webportal, null);
        ButterKnife.inject(this, view);

        initWebView();

        //whats in our cookie list?
        String cookies =  CookieManager.getInstance().getCookie(dataManager.getBaseUrl());
        System.out.println("See cookies! : " + cookies);

        return view;
    }

    public void openPortalUrl(String target)
    {
        webView.setWebChromeClient(new WebChromeClient()
        {
            @Override
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback)
            {
                callback.invoke(origin, true, false);
            }
        });
        loadUrlWithFromAppParam(target);
    }

    private void initWebView()
    {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setGeolocationEnabled(true);
        webView.setWebViewClient(new PortalWebViewClient(this, webView, googleService));
    }

    private void loadUrlWithFromAppParam(String url)
    {
        String endOfUrl = "from_app=true&device_id=" + googleService.getOrSetDeviceId() + "&device_type=android";
        String urlWithParams = url + (url.contains("?") ? "&" : "?") + endOfUrl;
        webView.loadUrl(urlWithParams);
    }
}
