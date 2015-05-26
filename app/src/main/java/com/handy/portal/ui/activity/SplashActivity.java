package com.handy.portal.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.CookieManager;

import com.handy.portal.R;

import java.util.regex.Pattern;

import butterknife.ButterKnife;

public class SplashActivity extends BaseActivity
{

    private static final Pattern USER_CREDENTIALS_PATTERN = Pattern.compile("(?:^|;)\\s*user_credentials=[^\\s]+\\s*;?");

    private static final String STATE_LAUNCHED_NEXT = "LAUNCHED_NEXT";

    private boolean launchedNext;

    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.inject(this);

        String cookie = CookieManager.getInstance().getCookie(dataManager.getBaseUrl());
        boolean isUserLoggedIn = cookie != null && USER_CREDENTIALS_PATTERN.matcher(cookie).find();
        if (isUserLoggedIn)
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
