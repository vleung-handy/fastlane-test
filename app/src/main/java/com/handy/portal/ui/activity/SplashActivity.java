package com.handy.portal.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.handy.portal.R;
import com.handy.portal.constant.PrefsKey;
import com.handy.portal.data.DataManager;
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
            userCredentialsToken = userCredentialsTokenFromCookieManager;
            prefsManager.setString(PrefsKey.USER_CREDENTIALS_TOKEN, userCredentialsToken);
        }

        if (userCredentialsToken == null)
        {
            launchActivity(LoginActivity.class);
        }
        else
        {
            if (providerId == null)
            {
                requestUserInfo();
            }
            else
            {
                checkForTerms();
            }
        }
    }

    private void requestUserInfo()
    {
        bus.post(new HandyEvent.RequestProviderInfo());
    }

    @Subscribe
    public void onReceiveUserInfoSuccess(HandyEvent.ReceiveProviderInfoSuccess event)
    {
        launchActivity(SplashActivity.class);
    }

    @Subscribe
    public void onReceiveUserInfoError(HandyEvent.ReceiveProviderInfoError event)
    {
        if (event.error.getType() == DataManager.DataManagerError.Type.NETWORK)
        {
            // only allow retries on network errors
            findViewById(R.id.progress_spinner).setVisibility(View.GONE);
            findViewById(R.id.fetch_error_view).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.fetch_error_text)).setText(R.string.unable_to_fetch_user);
            findViewById(R.id.try_again_button).setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    findViewById(R.id.progress_spinner).setVisibility(View.VISIBLE);
                    findViewById(R.id.fetch_error_view).setVisibility(View.GONE);
                    requestUserInfo();
                }
            });
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
