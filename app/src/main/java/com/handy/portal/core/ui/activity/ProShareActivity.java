package com.handy.portal.core.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.handy.portal.R;
import com.handy.portal.library.util.FragmentUtils;
import com.handy.portal.webview.ShareProviderWebViewFragment;

public class ProShareActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pro_share);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        ShareProviderWebViewFragment fragment = ShareProviderWebViewFragment.newInstance();
        FragmentUtils.switchToFragment(getSupportFragmentManager(), fragment, false);
    }
}
