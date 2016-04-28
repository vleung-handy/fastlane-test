package com.handy.portal.ui.fragment.dashboard;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.handy.portal.R;
import com.handy.portal.manager.ProviderManager;
import com.handy.portal.model.Provider;
import com.handy.portal.webview.PortalWebViewFragment;

import javax.inject.Inject;

public class DashboardVideoLibraryFragment extends PortalWebViewFragment
{
    @Inject
    ProviderManager mProviderManager;

    private static final String VIDEO_URL = "http://www.handy.com/pro/resources?native=1";

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        getWebView().getSettings().setBuiltInZoomControls(true);
        setOptionsMenuEnabled(true);
        setBackButtonEnabled(true);
        setActionBarTitle(R.string.video_library);
        Provider provider = mProviderManager.getCachedActiveProvider();
        if (provider != null)
        {
            getWebView().loadUrl(VIDEO_URL + "&provider_id=" + mProviderManager.getCachedActiveProvider().getId());
        }
        else
        {
            getWebView().loadUrl(VIDEO_URL);
        }
    }
}
