package com.handy.portal.webview;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.handy.portal.R;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.ui.fragment.ActionBarFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PortalWebViewFragment extends ActionBarFragment
{
    @Bind(R.id.portal_web_view)
    WebView webView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_webportal, container, false);
        ButterKnife.bind(this, view);

        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) //needed to workaround a bug in android 4.4 that cause webview artifacts to show.
        {
            view.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        initWebView();

        if (validateRequiredArguments())
        {
            openPortalUrl(getArguments().getString(BundleKeys.TARGET_URL));
        }

        return view;
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        setOptionsMenuEnabled(true);
        setBackButtonEnabled(true);
    }

    @Override
    protected List<String> requiredArguments()
    {
        List<String> requiredArguments = new ArrayList<>();
        requiredArguments.add(BundleKeys.TARGET_URL);
        return requiredArguments;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item)
    {
        if (item.getItemId() == android.R.id.home && webView.canGoBack())
        {
            webView.goBack();
            return true;
        }
        else
        {
            return super.onOptionsItemSelected(item);
        }
    }

    public void openPortalUrl(String url)
    {
        if (webView != null)
        {
            webView.setWebChromeClient(new WebChromeClient()
            {
                @Override
                public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback)
                {
                    callback.invoke(origin, true, false);
                }
            });
            loadUrlWithFromAppParam(url);
        }
    }

    private void initWebView()
    {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setGeolocationEnabled(true);
        webView.setWebViewClient(new PortalWebViewClient(this, webView, googleManager, bus));
    }

    private void loadUrlWithFromAppParam(String url)
    {
        //TODO: This code seems to be duplicated in the PortalWebViewClient
        String endOfUrl = "from_app=true&device_id=" + googleManager.getOrSetDeviceId()
                + "&device_type=android&hide_nav=1"
                + "&hide_banner=1"
                + "&hide_payments_tab=1"
                + "&hide_pro_request=1"
                + "&ht=1"
                + "&skip_web_portal_version_tracking=1"
                + "&skip_web_portal_blocking=1"
                + "&from_android_native=1"
                + "&disable_mobile_splash=1";
        String urlWithParams = url + (url.contains("?") ? "&" : "?") + endOfUrl;
        webView.loadUrl(urlWithParams);
    }

    @NonNull
    protected WebView getWebView()
    {
        return webView;
    }

    @Override
    public void onResume()
    {
        webView.onResume();
        super.onResume();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        webView.onPause();
    }

    @Override
    public void onDestroy()
    {
        /*
        quick-fix: since the whole app shares one loading spinner,
        have to remove it when this fragment is destroyed (i.e. back press)
        */
        bus.post(new HandyEvent.SetLoadingOverlayVisibility(false));
        super.onDestroy();
    }
}
