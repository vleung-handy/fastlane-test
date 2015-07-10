package com.handy.portal.help;

import android.app.Activity;
import android.support.v4.app.Fragment;

import com.handy.portal.R;

public final class HelpActivity extends Activity
{
    public static final String EXTRA_HELP_NODE = "com.handy.handy.EXTRA_HELP_NODE";
    public static final String EXTRA_BOOKING_ID = "com.handy.handy.EXTRA_BOOKING_ID";
    public static final String EXTRA_LOGIN_TOKEN = "com.handy.handy.EXTRA_LOGIN_TOKEN";
    public static final String EXTRA_PATH = "com.handy.handy.EXTRA_PATH";

    //@Override
    protected final Fragment createFragment()
    {
        final HelpNode node = getIntent().getParcelableExtra(EXTRA_HELP_NODE);
        final String bookingId = getIntent().getStringExtra(EXTRA_BOOKING_ID);
        final String loginToken = getIntent().getStringExtra(EXTRA_LOGIN_TOKEN);
        final String path = getIntent().getStringExtra(EXTRA_PATH);
        return HelpFragment.newInstance(node, bookingId, loginToken, path);
    }

    //@Override
    protected final String getNavItemTitle() {
        return getString(R.string.help);
    }
}
