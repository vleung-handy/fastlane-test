package com.handy.portal.ui.activity;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.handy.portal.onboarding.model.OnboardingDetails;
import com.handy.portal.onboarding.model.subflow.SubflowStatus;
import com.handy.portal.onboarding.ui.activity.OnboardingFlowActivity;
import com.handy.portal.retrofit.HandyRetrofitEndpoint;
import com.handy.portal.setup.SetupData;
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
    }

    @Override
    public void onResume()
    {
        super.onResume();
        bus.register(this);

        if (!hasUser())
        {
            final Intent loginActivityIntent = getActivityIntent(LoginActivity.class);
            loginActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(loginActivityIntent);
            finish();
        }
    }

    @Override
    protected void onSetupComplete(final SetupData setupData)
    {
        final Intent activityIntent = getTerminalActivityIntent(setupData);
        activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(activityIntent);
        finish();
    }

    @Override
    protected void onSetupFailure()
    {
        onSetupComplete(null);
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

    public Intent getTerminalActivityIntent(@Nullable final SetupData setupData)
    {
        final Intent activityIntent;
        if (setupData != null && shouldShowOnboarding(setupData.getOnboardingDetails()))
        {
            activityIntent = getActivityIntent(OnboardingFlowActivity.class);
            activityIntent.putExtra(BundleKeys.ONBOARDING_DETAILS,
                    setupData.getOnboardingDetails());
        }
        else
        {
            activityIntent = getActivityIntent(MainActivity.class);
        }
        return activityIntent;
    }

    private boolean shouldShowOnboarding(final OnboardingDetails onboardingDetails)
    {
        if (onboardingDetails != null && onboardingDetails.getSubflows() != null)
        {
            return anyOnboardingSubflowsIncomplete(onboardingDetails);
        }
        return false;
    }

    private boolean anyOnboardingSubflowsIncomplete(final OnboardingDetails onboardingDetails)
    {
        return !onboardingDetails.getSubflowsByStatus(SubflowStatus.INCOMPLETE).isEmpty();
    }

    private Intent getActivityIntent(final Class<? extends BaseActivity> activityClass)
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
        if (!TextUtils.isNullOrEmpty(authToken))
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
                CookieManager.getInstance().setCookie(endpoint.getBaseUrl(),
                        "user_credentials=" + authToken);
                CookieSyncManager.getInstance().sync();
            }
        }
    }

    private boolean hasUser()
    {
        return !TextUtils.isNullOrEmpty(mAuthToken);
    }
}
