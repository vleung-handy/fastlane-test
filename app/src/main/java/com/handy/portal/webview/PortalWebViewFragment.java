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
import com.handy.portal.core.constant.BundleKeys;
import com.handy.portal.core.event.HandyEvent;
import com.handy.portal.core.ui.fragment.ActionBarFragment;
import com.handy.portal.library.util.SystemUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PortalWebViewFragment extends ActionBarFragment
{
    @BindView(R.id.portal_web_view)
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

        return view;
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        setOptionsMenuEnabled(true);
        setBackButtonEnabled(true);

        initWebView();

        if (validateRequiredArguments() && webView != null)
        {
            webView.setWebChromeClient(new WebChromeClient()
            {
                @Override
                public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback)
                {
                    callback.invoke(origin, true, false);
                }
            });
            webView.loadUrl(getArguments().getString(BundleKeys.TARGET_URL));
        }
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

    protected void initWebView()
    {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setGeolocationEnabled(true);
        webView.setWebViewClient(new PortalWebViewClient(this, webView, bus,
                SystemUtils.getDeviceId(getContext())));
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
