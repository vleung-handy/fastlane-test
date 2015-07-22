package com.handy.portal.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.crashlytics.android.Crashlytics;
import com.handy.portal.R;
import com.handy.portal.constant.PrefsKey;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.manager.PrefsManager;
import com.handy.portal.retrofit.HandyRetrofitEndpoint;
import com.squareup.otto.Subscribe;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

public class SplashActivity extends BaseActivity
{
    private static final String STATE_LAUNCHED_NEXT = "LAUNCHED_NEXT";

    private boolean launchedNext;

    @Inject
    PrefsManager prefsManager;
    @Inject
    HandyRetrofitEndpoint endpoint;

    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        String providerId = prefsManager.getString(PrefsKey.USER_CREDENTIALS_ID, null);
        if (providerId != null)
        {
            Crashlytics.setUserIdentifier(providerId);
        }

        String userCredentialsToken = prefsManager.getString(PrefsKey.USER_CREDENTIALS_TOKEN, null);
        String userCredentialsTokenFromCookieManager = getUserCredentialsTokenFromCookieManager();
        if (userCredentialsToken == null && userCredentialsTokenFromCookieManager != null)
        {
            prefsManager.setString(PrefsKey.USER_CREDENTIALS_TOKEN, userCredentialsTokenFromCookieManager);
            if (providerId == null)
            {
                prefsManager.setString(PrefsKey.USER_CREDENTIALS_ID, "1"); // doesn't matter, we have a token
            }

            checkForTerms();
        }
        else
        {
            launchActivity(LoginActivity.class);
        }
    }

    private String getUserCredentialsTokenFromCookieManager()
    {
        CookieSyncManager.createInstance(this);
        String allCookies = CookieManager.getInstance().getCookie(endpoint.getBaseUrl());
        CookieSyncManager.getInstance().sync();

        if (allCookies != null)
        {
            Pattern pattern = Pattern.compile(".*user_credentials=(.*?)(?:;|$).*");
            Matcher matcher = pattern.matcher(allCookies);
            if (matcher.matches())
            {
                return matcher.group(1);
            }
            else
            {
                Crashlytics.log("Token cannot be obtained from cookie manager");
            }
        }
        else
        {
            Crashlytics.log("No cookies found in this device");
        }
        return null;
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
    public void onReceiveUpdateAvailableSuccess(HandyEvent.ReceiveUpdateAvailableSuccess event)
    {
        //Do nothing
    }
}
