package com.handy.portal.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.handy.portal.R;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.constant.PrefsKey;
import com.handy.portal.core.BuildConfigWrapper;
import com.handy.portal.data.DataManager;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.logger.handylogger.model.DeeplinkLog;
import com.handy.portal.manager.PrefsManager;
import com.handy.portal.manager.ProviderManager;
import com.handy.portal.retrofit.HandyRetrofitEndpoint;
import com.handy.portal.util.DeeplinkUtils;
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
    ProviderManager providerManager;
    @Inject
    HandyRetrofitEndpoint endpoint;
    @Inject
    BuildConfigWrapper buildConfigWrapper;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        String providerId = prefsManager.getString(PrefsKey.LAST_PROVIDER_ID);
        if (providerId != null)
        {
            // needs to happen immediately just in case anything bad happens after this line
            Crashlytics.setUserIdentifier(providerId);
        }

        if (buildConfigWrapper.isDebug())
        {
            String authToken = getIntent().getDataString();
            if (authToken != null && authToken.matches("[A-Za-z0-9_]+"))
            {
                prefsManager.setString(PrefsKey.AUTH_TOKEN, authToken);

                //For use with WebView
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                {
                    CookieManager.getInstance().setCookie(endpoint.getBaseUrl(),
                            "user_credentials=" + authToken);
                    CookieManager.getInstance().flush();
                }
                else
                {
                    CookieSyncManager.createInstance(this);
                    CookieManager.getInstance().setCookie(endpoint.getBaseUrl(), "user_credentials=" + authToken);
                    CookieSyncManager.getInstance().sync();
                }
            }
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        bus.register(this);

        String authToken = prefsManager.getString(PrefsKey.AUTH_TOKEN, null);
        String authTokenFromCookieManager = getAuthTokenFromCookieManager();
        if (authToken == null && authTokenFromCookieManager != null)
        {
            authToken = authTokenFromCookieManager;
            prefsManager.setString(PrefsKey.AUTH_TOKEN, authToken);
        }

        if (authToken == null)
        {
            launchActivity(LoginActivity.class);
        }
        else
        {
            // TODO: SplashActivity is always relaunched when user info is received or when terms are accepted. When we move away from that logic, refactor this section.
            if (providerManager.getCachedActiveProvider() != null)
            {
                checkForTerms();
            }
            else
            {
                requestProviderInfo();
            }

        }
    }

    private void requestProviderInfo()
    {
        bus.post(new HandyEvent.RequestProviderInfo());
    }

    @Subscribe
    public void onReceiveUserInfoSuccess(HandyEvent.ReceiveProviderInfoSuccess event)
    {
        // at this point, ProviderManager should have set the provider ID in prefs
        configManager.prefetch();
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
                    requestProviderInfo();
                }
            });
        }
        else
        {
            launchActivity(LoginActivity.class);
        }
    }

    @SuppressWarnings("deprecation")
    private String getAuthTokenFromCookieManager()
    {
        String allCookies;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            allCookies = CookieManager.getInstance().getCookie(endpoint.getBaseUrl());
            CookieManager.getInstance().flush();
        }
        else
        {
            CookieSyncManager.createInstance(this);
            allCookies = CookieManager.getInstance().getCookie(endpoint.getBaseUrl());
            CookieSyncManager.getInstance().sync();
        }

        if (allCookies != null)
        {
            Pattern pattern = Pattern.compile(".*user_credentials=(.*?)(?:%3A|;|$).*");
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
            // TODO: Maybe remove this since we remove all cookies on logout
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
    public void onPause()
    {
        try
        {
             /*
                 on mostly Samsung Android 5.0 devices (responsible for ~97% of crashes here),
                 Activity.onPause() can be called without Activity.onResume()
                 so unregistering the bus here can cause an exception
              */
            bus.unregister(this);
        }
        catch (Exception e)
        {
            Crashlytics.logException(e); //want more info for now
        }
        super.onPause();
    }

    @Override
    public void startActivityForResult(final Intent intent, final int resultCode)
    {
        super.startActivityForResult(intent, resultCode);
        launchedNext = true;
    }

    @Override
    public final void onSaveInstanceState(final Bundle outState)
    {
        try
        {
            outState.putBoolean(STATE_LAUNCHED_NEXT, launchedNext);
            super.onSaveInstanceState(outState);
        }
        catch (IllegalArgumentException e)
        {
            // Non fatal
            Crashlytics.logException(e);
        }
    }

    private void checkForTerms()
    {
        bus.post(new HandyEvent.RequestCheckTerms());
    }

    @Subscribe
    public void onReceiveCheckTermsSuccess(HandyEvent.ReceiveCheckTermsSuccess event) //TODO: we check terms in MainActivity also! need to consolidate
    {
        if (event.termsDetailsGroup.hasTerms())
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
        final Intent intent = new Intent(this, activityClass);
        final Uri data = getIntent().getData();
        final Bundle deeplinkBundle = DeeplinkUtils.createDeeplinkBundleFromUri(data);
        if (deeplinkBundle != null)
        {
            intent.putExtra(BundleKeys.DEEPLINK_DATA, deeplinkBundle);
            intent.putExtra(BundleKeys.DEEPLINK_SOURCE, DeeplinkLog.Source.LINK);
        }
        startActivity(intent);
    }

    @Override
    public void checkForUpdates()
    {
        //Do nothing
    }

    @Override
    public void launchAppUpdater()
    {
        //do nothing
    }
}
