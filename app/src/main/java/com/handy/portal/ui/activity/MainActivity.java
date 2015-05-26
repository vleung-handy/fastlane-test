package com.handy.portal.ui.activity;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.handy.portal.R;
import com.handy.portal.core.UpdateManager;
import com.handy.portal.event.Event;
import com.handy.portal.util.FlavorUtils;
import com.squareup.otto.Bus;

import javax.inject.Inject;

public class MainActivity extends BaseActivity
{
    @Inject
    Bus bus;
    @Inject
    UpdateManager updateManager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    @Override
    public void onResume() {
        super.onResume();
        checkForUpdates();
    }

    protected void checkForUpdates() {
        PackageInfo pInfo = null;
        try
        {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            bus.post(new Event.UpdateCheckEvent(FlavorUtils.getFlavor(), pInfo.versionCode));
        } catch (PackageManager.NameNotFoundException e)
        {
            throw new RuntimeException();
        }

    }

}
