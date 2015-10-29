package com.handy.portal.webview;

import com.handy.portal.R;
import com.handy.portal.constant.MainViewTab;

public class BlockScheduleFragment extends PortalWebViewFragment
{
    @Override
    protected MainViewTab getTab()
    {
        return MainViewTab.BLOCK_PRO_AVAILABLE_JOBS_WEBVIEW;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        setActionBar(R.string.block_jobs_schedule, false);
    }
}
