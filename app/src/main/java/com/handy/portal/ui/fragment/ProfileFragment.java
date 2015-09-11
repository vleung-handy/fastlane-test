package com.handy.portal.ui.fragment;

import com.handy.portal.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class ProfileFragment extends PortalWebViewFragment
{

    public ProfileFragment()
    {
    }

    public void onResume()
    {
        super.onResume();
        setActionBar(R.string.profile, false);
    }

}
