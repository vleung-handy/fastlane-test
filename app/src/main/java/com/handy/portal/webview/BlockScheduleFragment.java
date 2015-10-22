package com.handy.portal.webview;

import com.handy.portal.R;
import com.handy.portal.constant.MainViewTab;

public class BlockScheduleFragment extends PortalWebViewFragment
{
    @Override
    protected MainViewTab getTab()
    {
        return MainViewTab.AVAILABLE_JOBS;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        setActionBar(R.string.available_jobs, false);
    }
}
