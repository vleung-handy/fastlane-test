package com.handy.portal.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.crashlytics.android.Crashlytics;
import com.handy.portal.R;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.constant.PrefsKey;
import com.handy.portal.core.BuildConfigWrapper;
import com.handy.portal.flow.Flow;
import com.handy.portal.logger.handylogger.model.DeeplinkLog;
import com.handy.portal.manager.PrefsManager;
import com.handy.portal.retrofit.HandyRetrofitEndpoint;
import com.handy.portal.setup.SetupData;
import com.handy.portal.setup.SetupEvent;
import com.handy.portal.setup.step.AcceptTermsStep;
import com.handy.portal.setup.step.AppUpdateStep;
import com.handy.portal.setup.step.OnboardingStep;
import com.handy.portal.setup.step.SetConfigurationStep;
import com.handy.portal.setup.step.SetProviderProfileStep;
import com.handy.portal.util.DeeplinkUtils;
import com.handy.portal.util.TextUtils;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

public class SplashActivity extends BaseActivity
{
    @Inject
    PrefsManager prefsManager;
    @Inject
    HandyRetrofitEndpoint endpoint;
    @Inject
    BuildConfigWrapper buildConfigWrapper;

    private Flow mSetupFlow;

    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if (buildConfigWrapper.isDebug())
        {
            processInjectedCredentials();
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        bus.register(this);

        final String authToken = prefsManager.getString(PrefsKey.AUTH_TOKEN, null);
        final String providerId = prefsManager.getString(PrefsKey.LAST_PROVIDER_ID, null);
        if (authToken != null && providerId != null)
        {
            Crashlytics.setUserIdentifier(providerId);
            if (mSetupFlow == null)
            {
                bus.post(new SetupEvent.RequestSetupData());
            }
        }
        else
        {
            launchActivity(LoginActivity.class);
            finish();
        }
    }

    @Subscribe
    public void onReceiveSetupDataSuccess(final SetupEvent.ReceiveSetupDataSuccess event)
    {
        final SetupData setupData = event.getSetupData();
        mSetupFlow = new Flow()
                .addStep(new AppUpdateStep()) // this does NOTHING for now
                .addStep(new AcceptTermsStep(this, setupData.getTermsDetails()))
                .addStep(new SetConfigurationStep(this, setupData.getConfigurationResponse()))
                .addStep(new SetProviderProfileStep(this, setupData.getProviderProfile()))
                .addStep(new OnboardingStep())
                .setOnFlowCompleteListener(new Flow.OnFlowCompleteListener()
                {
                    @Override
                    public void onFlowComplete()
                    {
                        launchActivity(MainActivity.class);
                        finish();
                    }
                })
                .start();
    }

    @Subscribe
    public void onReceiveSetupDataError(final SetupEvent.ReceiveSetupDataError event)
    {
        // FIXME: Implement
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
}
