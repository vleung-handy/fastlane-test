package com.handy.portal.webview;

import com.handy.portal.R;
import com.handy.portal.constant.MainViewPage;

public class BlockScheduleFragment extends PortalWebViewFragment
{
    @Override
    protected MainViewPage getAppPage()
    {
        return MainViewPage.BLOCK_PRO_WEBVIEW;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        setActionBar(R.string.block_jobs_schedule, false);
    }
}
