package com.handy.portal.webview;


import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.webkit.WebView;

import com.handy.portal.logger.handylogger.LogEvent;
import com.handy.portal.logger.handylogger.model.ProfileLog;

import org.greenrobot.eventbus.EventBus;

public class RequestSuppliesWebViewClient extends PortalWebViewClient
{
    private boolean loadFailedLogSent;
    private boolean loadStartedLogSent;

    public RequestSuppliesWebViewClient(final Fragment parentFragment, final WebView webView,
                                        final EventBus bus, String deviceId)
    {
        super(parentFragment, webView, bus, deviceId);
        loadFailedLogSent = false;
        loadStartedLogSent = false;
    }

    @Override
    public void onReceivedError(final WebView view, final int errorCode, final String description, final String failingUrl)
    {
        super.onReceivedError(view, errorCode, description, failingUrl);
        if (!loadFailedLogSent)
        {
            loadFailedLogSent = true;
            bus.post(new LogEvent.AddLogEvent(new ProfileLog.ResupplyKitSiteLoadFailed()));
        }
    }

    @Override
    public void onPageStarted(final WebView view, final String url, final Bitmap favicon)
    {
        super.onPageStarted(view, url, favicon);
        if (!loadStartedLogSent)
        {
            loadStartedLogSent = true;
            bus.post(new LogEvent.AddLogEvent(new ProfileLog.ResupplyKitSiteLoadStarted()));
        }
    }
}
