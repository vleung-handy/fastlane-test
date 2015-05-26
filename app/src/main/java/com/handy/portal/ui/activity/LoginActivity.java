package com.handy.portal.ui.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.handy.portal.R;
import com.handy.portal.core.BaseApplication;
import com.handy.portal.core.UpdateManager;
import com.handy.portal.event.Event;
import com.handy.portal.util.FlavorUtils;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

import butterknife.ButterKnife;

public class LoginActivity extends BaseActivity
{
    @Inject
    Bus bus;
    @Inject
    UpdateManager updateManager;

    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.inject(this);

        ((BaseApplication) this.getApplication()).inject(this);
        this.bus.register(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        checkForUpdates();
    }

    @Override
    public void startActivity(final Intent intent)
    {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        super.startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed()
    {
        moveTaskToBack(true);
    }

    private void openMainActivity()
    {
        startActivity(new Intent(this, MainActivity.class));
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

    @Subscribe
    public void onUpdateCheckReceived(Event.UpdateCheckRequestReceivedEvent event)
    {
        if(event.updateDetails.getShouldUpdate()) {
            startActivity(new Intent(this, PleaseUpdateActivity.class));
        }
    }

}
