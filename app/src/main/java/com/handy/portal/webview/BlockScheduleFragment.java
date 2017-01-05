package com.handy.portal.webview;

import android.os.Bundle;

import com.handy.portal.R;
import com.handy.portal.core.constant.BundleKeys;
import com.handy.portal.core.constant.MainViewPage;
import com.handy.portal.core.manager.ProviderManager;
import com.handy.portal.retrofit.HandyRetrofitEndpoint;

import javax.inject.Inject;

public class BlockScheduleFragment extends PortalWebViewFragment
{
    @Inject
    ProviderManager mProviderManager;
    @Inject
    HandyRetrofitEndpoint mEndpoint;

    @Override
    protected MainViewPage getAppPage()
    {
        return MainViewPage.BLOCK_PRO_WEBVIEW;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        String url = mEndpoint.getBaseUrl() + "providers/" + mProviderManager.getLastProviderId() + "/provider_schedules";
        args.putString(BundleKeys.TARGET_URL, url);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        setActionBar(R.string.block_jobs_schedule, false);
    }
}
