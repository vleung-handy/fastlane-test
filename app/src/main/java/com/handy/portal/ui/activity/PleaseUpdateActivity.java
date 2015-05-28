package com.handy.portal.ui.activity;

import android.os.Bundle;

import com.handy.portal.R;
import com.handy.portal.event.Event;

import butterknife.ButterKnife;

public class PleaseUpdateActivity extends BaseActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_please_update);
        ButterKnife.inject(this);
    }

    @Override
    public void onResume() {
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
    public void onUpdateCheckReceived(Event.UpdateCheckRequestReceivedEvent event)
    {
        //Do nothing
    }


}
