package com.handy.portal.ui.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.handy.portal.R;
import com.handy.portal.core.GoogleService;
import com.handy.portal.core.PortalWebViewClient;
import com.handy.portal.core.ServerParams;
import com.handy.portal.ui.activity.MainActivity;
import com.handy.portal.ui.widget.ProgressDialog;
import com.handy.portal.util.Utils;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends InjectedFragment {

    @InjectView(R.id.webView) WebView webView;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_main, container, true);
        ButterKnife.inject(this, view);
        initWebView();
        openPortalUrl();
        return view;
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

    private void initWebView()
    {
        if(googleService == null)
        {
            System.out.println("Our google service did not inject properly");
        }
        else
        {
            System.out.println("YAY it injected");
        }
        GoogleCloudMessaging gcm = googleService.getCloudMessaging(getActivity());


        if(webView == null)
        {
            System.out.println("Our injected webview is null");
        }
        else
        {
            System.out.println("yay have injected webview");
        }

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
        webView.loadUrl(url);
    }

}
