package com.handy.portal.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.handy.portal.R;

import butterknife.ButterKnife;

public class SplashActivity extends BaseActivity
{
    private static final String STATE_LAUNCHED_NEXT = "LAUNCHED_NEXT";

    private boolean launchedNext;

    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.inject(this);

        if (loginManager.getLoggedInUserId() != null)
        {
            openMainActivity();
        }
        else
        {
            //TODO: Handle install referrers and deep links
            openLoginActivity();
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

    private void openLoginActivity()
    {
        startActivity(new Intent(this, LoginActivity.class));
    }

    private void openMainActivity()
    {
        startActivity(new Intent(this, MainActivity.class));
    }
}
