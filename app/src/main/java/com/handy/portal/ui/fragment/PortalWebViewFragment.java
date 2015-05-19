package com.handy.portal.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.handy.portal.R;
import com.handy.portal.consts.BundleKeys;
import com.handy.portal.core.PortalWebViewClient;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class PortalWebViewFragment extends InjectedFragment
{
    @InjectView(R.id.portal_web_view)
    WebView portalWebView;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState)
    {
        System.out.println("Web view fragment creation");

        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_webportal, null);
        ButterKnife.inject(this, view);

        initWebView();

        if(this.getArguments().containsKey(BundleKeys.TARGET_URL))
        {
            String targetUrl = this.getArguments().getString(BundleKeys.TARGET_URL);
            if (targetUrl != null)
            {
                openPortalUrl(targetUrl);
            }
        }

        return view;
    }

    private void initWebView()
    {
        portalWebView.getSettings().setJavaScriptEnabled(true);
        portalWebView.getSettings().setGeolocationEnabled(true);
        portalWebView.setWebViewClient(new PortalWebViewClient(this, portalWebView, googleService));
    }

    public void openPortalUrl(String target)
    {
        portalWebView.setWebChromeClient(new WebChromeClient()
        {
            @Override
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback)
            {
                callback.invoke(origin, true, false);
            }
        });
        loadUrlWithFromAppParam(target);
    }

    private void loadUrlWithFromAppParam(String url)
    {
        String endOfUrl = "from_app=true&device_id=" + googleService.getOrSetDeviceId() + "&device_type=android";
        String urlWithParams = url + (url.contains("?") ? "&" : "?") + endOfUrl;
        portalWebView.loadUrl(urlWithParams);
    }
}
