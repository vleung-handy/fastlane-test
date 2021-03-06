package com.handy.portal.webview;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.handy.portal.core.BaseApplication;
import com.handy.portal.core.event.HandyEvent;
import com.handy.portal.core.manager.PageNavigationManager;
import com.handy.portal.library.util.Utils;
import com.handy.portal.logger.handylogger.model.DeeplinkLog;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

public class PortalWebViewClient extends WebViewClient {
    private Fragment parentFragment;
    private WebView webView;
    private String mDeviceId;
    protected EventBus bus;

    @Inject
    PageNavigationManager mPageNavigationManager;

    public PortalWebViewClient(Fragment parentFragment,
                               WebView webView,
                               EventBus bus, String deviceId) {
        this.parentFragment = parentFragment;
        this.webView = webView;
        this.bus = bus;
        this.mDeviceId = deviceId;
        ((BaseApplication) parentFragment.getActivity().getApplication()).inject(this);
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        mPageNavigationManager.handleDeeplinkUrl((parentFragment.getActivity()).getSupportFragmentManager(), DeeplinkLog.Source.WEBVIEW, url);

        // To prevent a bug in webkit where it redirects to a url ending with /undefined
        if (url.substring(Math.max(0, url.length() - 10)).equals("/undefined")) {
            return true; // don't load the url
        }
        else if (url.startsWith("tel:")) {
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
            Utils.safeLaunchIntent(intent, parentFragment.getActivity());
            return true;
        }
        else if (url.startsWith("sms:")) {
            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse(url));
            Utils.safeLaunchIntent(intent, parentFragment.getActivity());
            return true;
        }

        String fixedUrl;
        if (url.contains("help")) {
            fixedUrl = url;
        }
        else {
            // changes #future to goto=future
            fixedUrl = url.replaceFirst("#(.*)(?:\\?|$)", "?goto=$1&");
        }

        loadUrlWithFromAppParam(fixedUrl);
        return true;
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(true));
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        // API level 5: WebViewClient.ERROR_HOST_LOOKUP
        if (errorCode == -2) {
            final String mimeType = "text/html";
            final String encoding = "utf-8";
            final String html = "<html style=\"background-color: #949493; color: white\"><body style=\"margin-top: 50px;\"><div style=\"text-align:center\"><img src=\"file:///android_asset/antenna.png\" /><h1>Connection Error</h1><h3>You're not connected to the Internet. Please check your cell signal and make sure you're not in airplane mode, then refresh.</h3></div></body></html>";
            view.loadDataWithBaseURL("fake://not/needed", html, mimeType, encoding, "");
            return;
        }
        // Default behaviour
        super.onReceivedError(view, errorCode, description, failingUrl);
    }

    @SuppressWarnings("deprecation")
    @TargetApi(android.os.Build.VERSION_CODES.M)
    @Override
    public void onReceivedError(WebView view, WebResourceRequest req, WebResourceError rerr) {
        // Redirect to deprecated method, so you can use it in all SDK versions
        onReceivedError(view, rerr.getErrorCode(), rerr.getDescription().toString(),
                req.getUrl().toString());
    }

    private void loadUrlWithFromAppParam(String url) {
        //TODO: This code seems to be duplicated in the PortalWebViewFragment
        String endOfUrl = "from_app=true&device_id=" + mDeviceId
                + "&device_type=android&hide_nav=1"
                + "&hide_banner=1"
                + "&hide_payments_tab=1"
                + "&hide_pro_request=1"
                + "&ht=1"
                + "&skip_web_portal_version_tracking=1"
                + "&skip_web_portal_blocking=1"
                + "&from_android_native=1"
                + "&disable_mobile_splash=1";
        String urlWithParams = url + (url.contains("?") ? (url.endsWith("&") ? "" : "&") : "?") + endOfUrl;
        webView.loadUrl(urlWithParams);
    }
}
