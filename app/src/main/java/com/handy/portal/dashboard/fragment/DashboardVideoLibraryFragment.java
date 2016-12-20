package com.handy.portal.dashboard.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;

import com.handy.portal.R;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.manager.ProviderManager;
import com.handy.portal.webview.PortalWebViewFragment;

import javax.inject.Inject;

public class DashboardVideoLibraryFragment extends PortalWebViewFragment
{
    @Inject
    ProviderManager mProviderManager;

    private static final String VIDEO_URL = "http://www.handy.com/pro/resources?native=1";

    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        final String providerId = mProviderManager.getLastProviderId();
        if (!TextUtils.isEmpty(providerId))
        {
            getArguments().putString(BundleKeys.TARGET_URL, VIDEO_URL + "&provider_id="
                    + providerId);
        }
        else
        {
            getArguments().putString(BundleKeys.TARGET_URL, VIDEO_URL);
        }
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState)
    {
        setActionBarTitle(R.string.video_library);
        super.onViewCreated(view, savedInstanceState);
    }
}
