package com.handy.portal.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.handy.portal.R;
import com.handy.portal.consts.BundleKeys;
import com.handy.portal.core.PortalWebViewClient;
import com.handy.portal.data.HandyRetrofitEndpoint;
import com.squareup.otto.Bus;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class PortalWebViewFragment extends InjectedFragment
{
    public enum Target
    {
        JOBS("available"),
        SCHEDULE("future"),
        PROFILE("profile"),
        HELP("help"),
        DETAILS("details");

        private String value;

        Target(String value)
        {
            this.value = value;
        }

        public String getValue()
        {
            return value;
        }
    }

    @InjectView(R.id.portal_web_view)
    WebView webView;

    @Inject
    HandyRetrofitEndpoint endpoint;
    @Inject
    Bus bus;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_webportal, null);
        ButterKnife.inject(this, view);

        initWebView();

        if(validateRequiredArguments())
        {
            openPortalUrl(getArguments().getString(BundleKeys.TARGET_URL));
        }

        return view;
    }

    @Override
    protected List<String> requiredArguments()
    {
        List<String> requiredArguments = new ArrayList<>();
        requiredArguments.add(BundleKeys.TARGET_URL);
        return requiredArguments;
    }

    public void openPortalUrl(Target target)
    {
        openPortalUrl(target.getValue());
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
        String url = endpoint.getBaseUrl() + "/portal/home?goto=" + target;
        loadUrlWithFromAppParam(url);
    }

    private void initWebView()
    {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setGeolocationEnabled(true);
        webView.setWebViewClient(new PortalWebViewClient(this, webView, googleService, bus));
    }

    private void loadUrlWithFromAppParam(String url)
    {

        String endOfUrl = "from_app=true&device_id=" + googleService.getOrSetDeviceId() + "&device_type=android&hide_nav=1&hide_pro_request=1&ht=1&skip_web_portal_version_tracking=1";
        String urlWithParams = url + (url.contains("?") ? "&" : "?") + endOfUrl;
        Log.d(PortalWebViewFragment.class.getSimpleName(), "Loading url: " + urlWithParams);
        webView.loadUrl(urlWithParams);
    }
}
