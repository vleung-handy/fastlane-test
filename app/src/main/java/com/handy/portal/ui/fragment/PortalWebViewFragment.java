package com.handy.portal.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.handy.portal.R;
import com.handy.portal.core.PortalWebViewClient;
import com.handy.portal.core.ServerParams;

import butterknife.ButterKnife;
import butterknife.InjectView;


/**
 * A placeholder fragment containing a simple view.
 */
public class PortalWebViewFragment extends InjectedFragment {

    @InjectView(R.id.webViewPortal)
    WebView webView;

    public PortalWebViewFragment() {
    }

    protected String getWebParam()
    {
        return "";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_webportal, null);
        ButterKnife.inject(this, view);

        initWebView();
        openPortalUrl();

        return view;
    }

    private void initWebView()
    {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setGeolocationEnabled(true);
        webView.setWebViewClient(new PortalWebViewClient(this, webView, googleService));
    }

    public void openUrlWithChrome(String url){
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                callback.invoke(origin,  true, false);
            }
        });
        loadUrlWithFromAppParam(url);
    }

    private void loadUrlWithFromAppParam(String url){
        String endOfUrl = "from_app=true&device_id=" + googleService.getOrSetDeviceId() + "&device_type=android";
        if(url.contains("?")){
            url = url + "&" + endOfUrl;
        } else {
            url = url + "?" + endOfUrl;
        }

        System.out.println("Target url : " + url);

        webView.loadUrl(url);
    }

    private void openPortalUrl()
    {
        String FinalUrl;
        Intent intent = getActivity().getIntent();
        CharSequence booking_id = intent.getStringExtra("booking_id");

        if(booking_id != null) {
            FinalUrl = ServerParams.BaseUrl + "portal/jobs/" + booking_id + "/job_details";
        } else {
            FinalUrl = ServerParams.BaseUrl + "professional";
        }
        openUrlWithChrome(FinalUrl);
    }

}
