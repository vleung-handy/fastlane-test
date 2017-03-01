package com.handy.portal.core.ui.fragment;

import android.webkit.WebView;

import com.handy.portal.library.util.SystemUtils;
import com.handy.portal.webview.PortalWebViewFragment;
import com.handy.portal.webview.RequestSuppliesWebViewClient;

public class RequestSuppliesWebViewFragment extends PortalWebViewFragment {
    @Override
    protected void initWebView() {
        WebView webview = getWebView();
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setGeolocationEnabled(true);
        webview.setWebViewClient(new RequestSuppliesWebViewClient(this, getWebView(), bus,
                SystemUtils.getDeviceId(getContext())));
    }
}
