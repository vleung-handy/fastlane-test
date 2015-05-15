package com.handy.portal.core;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.handy.portal.ui.widget.ProgressDialog;
import com.handy.portal.util.Utils;

public class PortalWebViewClient extends WebViewClient
{
    private Fragment parentFragment;
    private WebView webView;
    private ProgressDialog pd;
    private GoogleService googleService;

    public PortalWebViewClient(Fragment parentFragment,
                               WebView webView,
                               GoogleService gs)
    {
        this.parentFragment = parentFragment;
        this.webView = webView;
        this.pd = new ProgressDialog(this.parentFragment.getActivity());
        this.googleService = gs;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url)
    {
        String[] splitBySlash = url.split("/");
        // To prevent a bug in webkit where it redirects to a url ending with /undefined
        if (url.substring(Math.max(0, url.length() - 10)).equals("/undefined"))
        {
            return true; // don't load the url
        } else if (url.startsWith("tel:"))
        {
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
            parentFragment.startActivity(intent);
            return true;
        } else if (url.startsWith("sms:"))
        {
            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse(url));
            parentFragment.startActivity(intent);
            return true;
        } else if (Utils.isInteger(splitBySlash[splitBySlash.length - 1]))
        {
            SharedPreferences pref = parentFragment.getActivity().getApplicationContext().getSharedPreferences("HandybookProviderApp", 0); // 0 - for private mode
            String potentialId = pref.getString("providerId", null);
            if (potentialId == null)
            {
            }
        }
        loadUrlWithFromAppParam(url);
        return true;
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon)
    {
        super.onPageStarted(view, url, favicon);
        pd.setTitle("Please wait");
        pd.show();
    }

    @Override
    public void onPageFinished(WebView view, String url)
    {
        super.onPageFinished(view, url);
        pd.dismiss();
    }

    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl)
    {
        pd.dismiss();
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

        if (googleService == null)
        {
            System.err.println("Can not contact google service");
            return;
        }

        String endOfUrl = "from_app=true&device_id=" + googleService.getOrSetDeviceId() + "&device_type=android";
        if (url.contains("?"))
        {
            url = url + "&" + endOfUrl;
        } else
        {
            url = url + "?" + endOfUrl;
        }
        webView.loadUrl(url);
    }
}
