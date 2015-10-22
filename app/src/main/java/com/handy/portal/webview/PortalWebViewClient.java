package com.handy.portal.webview;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.crashlytics.android.Crashlytics;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.manager.GoogleManager;
import com.handy.portal.util.Utils;
import com.squareup.otto.Bus;

public class PortalWebViewClient extends WebViewClient
{
    private Fragment parentFragment;
    private WebView webView;
    private GoogleManager googleManager;
    private Bus bus;

    public PortalWebViewClient(Fragment parentFragment,
                               WebView webView,
                               GoogleManager gs,
                               Bus bus)
    {
        this.parentFragment = parentFragment;
        this.webView = webView;
        this.googleManager = gs;
        this.bus = bus;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url)
    {
        // To prevent a bug in webkit where it redirects to a url ending with /undefined
        if (url.substring(Math.max(0, url.length() - 10)).equals("/undefined"))
        {
            return true; // don't load the url
        }
        else if (url.startsWith("tel:"))
        {
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
            Utils.safeLaunchIntent(intent, parentFragment.getActivity());
            return true;
        }
        else if (url.startsWith("sms:"))
        {
            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse(url));
            Utils.safeLaunchIntent(intent, parentFragment.getActivity());
            return true;
        }

        String fixedUrl;
        if (url.contains("help"))
        {
            fixedUrl = url;
        }
        else
        {
            // changes #future to goto=future
            fixedUrl = url.replaceFirst("#(.*)(?:\\?|$)", "?goto=$1&");
        }

        loadUrlWithFromAppParam(fixedUrl);
        return true;
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon)
    {
        super.onPageStarted(view, url, favicon);
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(true));
    }

    @Override
    public void onPageFinished(WebView view, String url)
    {
        super.onPageFinished(view, url);
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
    }

    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl)
    {
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        // API level 5: WebViewClient.ERROR_HOST_LOOKUP
        if (errorCode == -2)
        {
            final String mimeType = "text/html";
            final String encoding = "utf-8";
            final String html = "<html style=\"background-color: #949493; color: white\"><body style=\"margin-top: 50px;\"><div style=\"text-align:center\"><img src=\"file:///android_asset/antenna.png\" /><h1>Connection Error</h1><h3>You're not connected to the Internet. Please check your cell signal and make sure you're not in airplane mode, then refresh.</h3></div></body></html>";
            view.loadDataWithBaseURL("fake://not/needed", html, mimeType, encoding, "");
            return;
        }
        // Default behaviour
        super.onReceivedError(view, errorCode, description, failingUrl);
    }

    private void loadUrlWithFromAppParam(String url)
    {
        if (googleManager == null)
        {
            Crashlytics.log("Can not contact google service");
            return;
        }

        //TODO: This code seems to be duplicated in the PortalWebViewFragment
        String endOfUrl = "from_app=true&device_id=" + googleManager.getOrSetDeviceId()
                + "&device_type=android&hide_nav=1"
                + "&hide_banner=1"
                + "&hide_payments_tab=1"
                + "&hide_pro_request=1"
                + "&ht=1"
                + "&skip_web_portal_version_tracking=1"
                + "&skip_web_portal_blocking=1"
                + "&from_android_native=1"
                ;
        String urlWithParams = url + (url.contains("?") ? (url.endsWith("&") ? "" : "&") : "?") + endOfUrl;
        webView.loadUrl(urlWithParams);
    }
}