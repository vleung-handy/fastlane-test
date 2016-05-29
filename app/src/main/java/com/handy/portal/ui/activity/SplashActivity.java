package com.handy.portal.ui.activity;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.ImageView;

import com.crashlytics.android.Crashlytics;
import com.handy.portal.R;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.constant.PrefsKey;
import com.handy.portal.core.BuildConfigWrapper;
import com.handy.portal.library.util.TextUtils;
import com.handy.portal.logger.handylogger.model.DeeplinkLog;
import com.handy.portal.manager.PrefsManager;
import com.handy.portal.retrofit.HandyRetrofitEndpoint;
import com.handy.portal.util.DeeplinkUtils;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.BindInt;
import butterknife.ButterKnife;

public class SplashActivity extends BaseActivity
{
    @Inject
    PrefsManager prefsManager;
    @Inject
    HandyRetrofitEndpoint endpoint;
    @Inject
    BuildConfigWrapper buildConfigWrapper;

    @Bind(R.id.progress_spinner)
    ImageView mProgressSpinner;
    @BindInt(R.integer.progress_spinner_start_offset_millis)
    int mProgressSpinnerStartOffsetMillis;

    private String mAuthToken;
    private String mProviderId;
    private Runnable mLoadingAnimationStarter = new Runnable()
    {
        @Override
        public void run()
        {
            if (mProgressSpinner != null)
            {
                mProgressSpinner.setVisibility(View.VISIBLE);
                final AnimationDrawable animation =
                        (AnimationDrawable) mProgressSpinner.getBackground();
                animation.start();
            }
        }
    };

    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);

        mProgressSpinner.postDelayed(mLoadingAnimationStarter, mProgressSpinnerStartOffsetMillis);

        if (buildConfigWrapper.isDebug())
        {
            processInjectedCredentials();
        }

        mAuthToken = prefsManager.getString(PrefsKey.AUTH_TOKEN, null);
        mProviderId = prefsManager.getString(PrefsKey.LAST_PROVIDER_ID, null);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        bus.register(this);

        if (hasUser())
        {
            Crashlytics.setUserIdentifier(mProviderId);
        }
        else
        {
            final Intent loginActivityIntent = getActivityIntent(LoginActivity.class);
            loginActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(loginActivityIntent);
            finish();
        }
    }

    @Override
    protected void onSetupComplete()
    {
        final Intent mainActivityIntent = getActivityIntent(MainActivity.class);
        mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainActivityIntent);
        finish();
    }

    @Override
    protected void onSetupFailure()
    {
        onSetupComplete();
    }

    @Override
    protected boolean shouldTriggerSetup()
    {
        return hasUser();
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
        mProgressSpinner.removeCallbacks(mLoadingAnimationStarter);
        super.onPause();
    }

    @Override
    public final void onSaveInstanceState(final Bundle outState)
    {
        try
        {
            super.onSaveInstanceState(outState);
        }
        catch (IllegalArgumentException e)
        {
            // Non fatal
            Crashlytics.logException(e);
        }
    }

    private Intent getActivityIntent(Class<? extends BaseActivity> activityClass)
    {
        final Intent intent = new Intent(this, activityClass);
        final Uri data = getIntent().getData();
        final Bundle deeplinkBundle = DeeplinkUtils.createDeeplinkBundleFromUri(data);
        if (deeplinkBundle != null)
        {
            intent.putExtra(BundleKeys.DEEPLINK_DATA, deeplinkBundle);
            intent.putExtra(BundleKeys.DEEPLINK_SOURCE, DeeplinkLog.Source.LINK);
        }
        return intent;
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

    private void processInjectedCredentials()
    {
        final String authToken = getIntent().getStringExtra(PrefsKey.AUTH_TOKEN);
        final String providerId = getIntent().getStringExtra(PrefsKey.LAST_PROVIDER_ID);
        if (!TextUtils.isNullOrEmpty(authToken) && !TextUtils.isNullOrEmpty(providerId))
        {
            prefsManager.setString(PrefsKey.AUTH_TOKEN, authToken);
            prefsManager.setString(PrefsKey.LAST_PROVIDER_ID, providerId);

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
                CookieManager.getInstance().setCookie(endpoint.getBaseUrl(),
                        "user_credentials=" + authToken);
                CookieSyncManager.getInstance().sync();
            }
        }
    }

    private boolean hasUser()
    {
        return !TextUtils.isNullOrEmpty(mAuthToken) && !TextUtils.isNullOrEmpty(mProviderId);
    }
}
