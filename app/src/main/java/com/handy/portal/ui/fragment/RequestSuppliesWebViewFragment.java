package com.handy.portal.ui.fragment;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

import com.handy.portal.webview.PortalWebViewFragment;
import com.handy.portal.webview.RequestSuppliesWebViewClient;

public class RequestSuppliesWebViewFragment extends PortalWebViewFragment
{
    private static final String REQUEST_SUPPLIES_URL = "https://www.handy.com/boxed";

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        setOptionsMenuEnabled(true);
        setBackButtonEnabled(true);

        WebView requestSuppliesWebView = getWebView();
        requestSuppliesWebView.getSettings().setBuiltInZoomControls(true);
        initResupplyKitWebViewClient();
        requestSuppliesWebView.loadUrl(REQUEST_SUPPLIES_URL);
    }

    private void initResupplyKitWebViewClient()
    {
        getWebView().setWebViewClient(new RequestSuppliesWebViewClient(this, getWebView(), googleManager, bus));
    }
}
