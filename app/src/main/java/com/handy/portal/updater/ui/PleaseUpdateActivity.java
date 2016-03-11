package com.handy.portal.updater.ui;

import android.os.Bundle;

import com.handy.portal.R;
import com.handy.portal.ui.activity.BaseActivity;

public class PleaseUpdateActivity extends BaseActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_please_update);
    }

    @Override
    public void onResume() {
        super.onResume();
        mixpanel.track("portal app update blocking screen shown");
    }

    @Override
    public void onBackPressed()
    {
        //Do nothing
    }

    @Override
    public void checkForUpdates()
    {
        //Do nothing
    }

    @Override
    public boolean isAppUpdateFlowEnabled()
    {
        return false;
    }
}
