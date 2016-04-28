package com.handy.portal.ui.fragment;

import android.os.Bundle;
import android.view.View;

import com.handy.portal.constant.MainViewTab;
import com.handy.portal.webview.PortalWebViewFragment;

public class RequestSuppliesWebViewFragment extends PortalWebViewFragment
{
    private static final String REQUEST_SUPPLIES_URL = "http://www.boxed.com/handy-zcxknjvlij34iojq3";

    @Override
    protected MainViewTab getTab()
    {
        return MainViewTab.REQUEST_SUPPLIES_WEB_VIEW;
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        getWebView().getSettings().setBuiltInZoomControls(true);
        setOptionsMenuEnabled(true);
        setBackButtonEnabled(true);
        getWebView().loadUrl(REQUEST_SUPPLIES_URL);
    }
}
