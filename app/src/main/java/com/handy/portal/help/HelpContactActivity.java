package com.handy.portal.help;

import android.app.Activity;
import android.support.v4.app.Fragment;

public final class HelpContactActivity extends Activity
{

    public static final String EXTRA_HELP_NODE = "com.handy.handy.EXTRA_HELP_NODE";
    public static final String EXTRA_HELP_PATH = "com.handy.handy.EXTRA_PATH";

    protected final Fragment createFragment() {
        final HelpNode node = getIntent().getParcelableExtra(EXTRA_HELP_NODE);
        final String path = getIntent().getStringExtra(EXTRA_HELP_PATH);
        return HelpContactFragment.newInstance(node, path);
    }

    protected final String getNavItemTitle() {
        return "";
    }
}