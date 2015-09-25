package com.handy.portal.ui.fragment;

import com.handy.portal.R;
import com.handy.portal.constant.MainViewTab;

/**
 * A placeholder fragment containing a simple view.
 */
public class ProfileFragment extends PortalWebViewFragment
{
    @Override
    public void onResume()
    {
        super.onResume();
        setActionBar(R.string.profile, false);
        tabsCallback.updateTabs(MainViewTab.PROFILE);
    }
}
