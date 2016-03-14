package com.handy.portal.ui.fragment.dashboard;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.handy.portal.R;
import com.handy.portal.constant.MainViewTab;
import com.handy.portal.manager.ProviderManager;
import com.handy.portal.model.Provider;
import com.handy.portal.ui.fragment.ActionBarFragment;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DashboardVideoLibraryFragment extends ActionBarFragment
{
    @Inject
    ProviderManager mProviderManager;

    @Bind(R.id.webview)
    WebView mWebView;

    private static final String VIDEO_URL = "http://www.handy.com/pro/resources?native=1";

    @Override
    protected MainViewTab getTab()
    {
        return MainViewTab.DASHBOARD_VIDEO_LIBRARY;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_dashboard_video_library, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        setOptionsMenuEnabled(true);
        setBackButtonEnabled(true);
        setActionBarTitle(R.string.entire_video_library);
    }

    @Override
    public void onResume()
    {
        super.onResume();

        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setBuiltInZoomControls(true);
        
        Provider provider = mProviderManager.getCachedActiveProvider();
        if (provider != null)
        {
            mWebView.loadUrl(VIDEO_URL + "&provider_id=" + mProviderManager.getCachedActiveProvider().getId());
        }
        else
        {
            mWebView.loadUrl(VIDEO_URL);
        }
    }
}
