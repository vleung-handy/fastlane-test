package com.handy.portal.ui.activity;

import android.content.Intent;
import android.os.Bundle;

import com.handy.portal.R;
import com.handy.portal.core.LoginManager;
import com.handy.portal.event.HandyEvent;
import com.squareup.otto.Subscribe;

public class SplashActivity extends BaseActivity
{
    private static final String STATE_LAUNCHED_NEXT = "LAUNCHED_NEXT";

    private boolean launchedNext;

    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        configManager.init();

        String loggedInUserId = prefs.getString(LoginManager.USER_CREDENTIALS_ID_KEY, null);
        if (loggedInUserId != null)
        {
            checkForTerms();
        }
        else
        {
            //TODO: Handle install referrers and deep links
            launchActivity(LoginActivity.class);
        }
    }

    @Override
    public void startActivity(final Intent intent)
    {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        super.startActivity(intent);

        launchedNext = true;
        finish();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        bus.register(this);
    }

    @Override
    public void onPause()
    {
        bus.unregister(this);
        super.onPause();
    }

    @Override
    public void startActivityForResult(final Intent intent, final int resultCode)
    {
        super.startActivityForResult(intent, resultCode);
        launchedNext = true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public final void onSaveInstanceState(final Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_LAUNCHED_NEXT, launchedNext);
    }

    private void checkForTerms()
    {
        bus.post(new HandyEvent.RequestCheckTerms());
    }

    @Subscribe
    public void onReceiveCheckTermsSuccess(HandyEvent.ReceiveCheckTermsSuccess event)
    {
        if (event.termsDetails.getCode() != null)
        {
            launchActivity(TermsActivity.class);
        }
        else
        {
            launchActivity(MainActivity.class);
        }
    }

    @Subscribe
    public void onReceiveCheckTermsError(HandyEvent.ReceiveCheckTermsError event)
    {
        launchActivity(TermsActivity.class);
    }

    private void launchActivity(Class<? extends BaseActivity> activityClass)
    {
        startActivity(new Intent(this, activityClass));
    }

    @Override
    public void checkForUpdates()
    {
        //Do nothing
    }

    @Override
    public void onUpdateAvailable(HandyEvent.ReceiveUpdateAvailableSuccess event)
    {
        //Do nothing
    }
}
