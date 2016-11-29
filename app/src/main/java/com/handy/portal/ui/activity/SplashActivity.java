package com.handy.portal.ui.activity;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.handy.portal.R;
import com.handy.portal.constant.BundleKeys;
import com.handy.portal.constant.PrefsKey;
import com.handy.portal.core.BuildConfigWrapper;
import com.handy.portal.data.DataManager;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.logger.handylogger.LogEvent;
import com.handy.portal.logger.handylogger.model.AppLog;
import com.handy.portal.logger.handylogger.model.DeeplinkLog;
import com.handy.portal.logger.handylogger.model.LoginLog;
import com.handy.portal.manager.LoginManager;
import com.handy.portal.onboarding.model.OnboardingDetails;
import com.handy.portal.onboarding.model.subflow.SubflowStatus;
import com.handy.portal.onboarding.ui.activity.OnboardingFlowActivity;
import com.handy.portal.retrofit.HandyRetrofitEndpoint;
import com.handy.portal.setup.SetupData;
import com.handy.portal.util.DeeplinkUtils;
import com.handybook.shared.LayerHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import javax.inject.Inject;

import butterknife.BindInt;
import butterknife.BindView;
import butterknife.ButterKnife;

public class SplashActivity extends BaseActivity
{
    @Inject
    LoginManager mLoginManager;
    @Inject
    HandyRetrofitEndpoint endpoint;
    @Inject
    BuildConfigWrapper buildConfigWrapper;
    @Inject
    LayerHelper mLayerHelper;
    @Inject
    EventBus mBus;

    @BindView(R.id.progress_spinner)
    ImageView mProgressSpinner;
    @BindInt(R.integer.progress_spinner_start_offset_millis)
    int mProgressSpinnerStartOffsetMillis;

    private String mAuthToken;
    private boolean mSltLoggingIn;

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
        mBus.register(this);

        mProgressSpinner.postDelayed(mLoadingAnimationStarter, mProgressSpinnerStartOffsetMillis);

        if (buildConfigWrapper.isDebug())
        {
            processInjectedCredentials();
        }

        mAuthToken = mPrefsManager.getSecureString(PrefsKey.AUTH_TOKEN, null);

        // Check for logging in with single login token deep link
        loginWithSltIfNeeded();

        if (mAuthToken != null)
        {
            mLayerHelper.initLayer(mAuthToken);
        }
        else if (mLayerHelper.getLayerClient().isAuthenticated())
        {
            mLayerHelper.deauthenticate();
        }

        if (mPrefsManager.getSecureBoolean(PrefsKey.APP_FIRST_LAUNCH, true))
        {
            mBus.post(new LogEvent.AddLogEvent(new AppLog.AppOpenLog(true, true)));
            mPrefsManager.setSecureBoolean(PrefsKey.APP_FIRST_LAUNCH, false);
        }
        else
        {
            mBus.post(new LogEvent.AddLogEvent(new AppLog.AppOpenLog(false, true)));
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        // If not logged in and not logging in, prefetch login type before login
        if (!hasUser() && !mSltLoggingIn)
        {
            mConfigManager.prefetch();
        }
    }

    @Subscribe
    public void onLoginSuccess(HandyEvent.ReceiveLoginSuccess event)
    {
        mBus.post(new LogEvent.AddLogEvent(new LoginLog.Success(LoginLog.TYPE_TOKEN)));
        startActivity(new Intent(this, SplashActivity.class));
        finish();
    }

    @Subscribe
    public void onLoginError(HandyEvent.ReceiveLoginError event)
    {
        mBus.post(new LogEvent.AddLogEvent(new LoginLog.Error(LoginLog.TYPE_TOKEN)));
        DataManager.DataManagerError.Type errorType = event.error == null ? null : event.error.getType();
        if (errorType != null)
        {
            if (errorType.equals(DataManager.DataManagerError.Type.NETWORK))
            {
                Toast.makeText(this, R.string.error_connectivity, Toast.LENGTH_SHORT).show();
            }
            else if (errorType.equals(DataManager.DataManagerError.Type.CLIENT))
            {
                Toast.makeText(this, R.string.login_error_bad_login, Toast.LENGTH_SHORT).show();
            }
            else //server error
            {
                Toast.makeText(this, R.string.login_error_connectivity, Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            //should never happen
            Toast.makeText(this, R.string.login_error_connectivity, Toast.LENGTH_SHORT).show();
            Crashlytics.logException(new Exception("Login request error type is null"));
        }
        launchLoginActivity();
    }

    @Subscribe
    public void onReceiveConfigurationSuccess(HandyEvent.ReceiveConfigurationSuccess event)
    {
        // Start Login Screen if not logged in.
        // Config is needed to determine pin vs slt login
        if (!hasUser())
        {
            launchLoginActivity();
        }
    }

    @Subscribe
    public void onReceiveConfigurationError(HandyEvent.ReceiveConfigurationError event)
    {
        // Start Login Screen if not logged in.
        // Config is needed to determine pin vs slt login
        if (!hasUser())
        {
            launchLoginActivity();
        }
    }

    public void launchLoginActivity()
    {
        final Intent loginActivityIntent = getActivityIntent(LoginActivity.class);
        loginActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(loginActivityIntent);
        finish();
    }

    @Override
    protected void onDestroy()
    {
        mBus.unregister(this);
        super.onDestroy();
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
            final String startupDeeplink = setupData != null ?
                    setupData.getStartupDeeplink() : null;
            Uri defaultDeeplinkUri = null;
            if (!TextUtils.isEmpty(startupDeeplink))
            {
                defaultDeeplinkUri = Uri.parse(startupDeeplink);
            }
            activityIntent = getActivityIntent(MainActivity.class, defaultDeeplinkUri);
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
        return getActivityIntent(activityClass, null);
    }

    private Intent getActivityIntent(final Class<? extends BaseActivity> activityClass,
                                     @Nullable final Uri defaultDeeplinkUri)
    {
        final Intent intent = new Intent(this, activityClass);
        final boolean isLinkDeeplinkAttached = attachDeeplinkDataIfAvailable(intent,
                getIntent().getData(), DeeplinkLog.Source.LINK);
        if (!isLinkDeeplinkAttached)
        {
            attachDeeplinkDataIfAvailable(intent, defaultDeeplinkUri, DeeplinkLog.Source.STARTUP);
        }
        return intent;
    }

    private boolean attachDeeplinkDataIfAvailable(
            @NonNull final Intent intent,
            @Nullable final Uri data,
            @NonNull @DeeplinkLog.Source.DeeplinkSource final String source)
    {
        final Bundle deeplinkBundle = DeeplinkUtils.createDeeplinkBundleFromUri(data);
        if (deeplinkBundle != null)
        {
            intent.putExtra(BundleKeys.DEEPLINK_DATA, deeplinkBundle);
            intent.putExtra(BundleKeys.DEEPLINK_SOURCE, source);
            return true;
        }
        else
        {
            return false;
        }

    }

    private void loginWithSltIfNeeded()
    {
        Intent intent = getIntent();
        if (!hasUser() && intent != null && getIntent().getData() != null)
        {
            mSltLoggingIn = true;
            String n = getIntent().getData().getQueryParameter("n");
            String sig = getIntent().getData().getQueryParameter("sig");
            String slt = getIntent().getData().getQueryParameter("slt");

            if (!TextUtils.isEmpty(n) && !TextUtils.isEmpty(sig) && !TextUtils.isEmpty(slt))
            {
                mLoginManager.loginWithSlt(n, sig, slt);
            }
        }
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

    /**
     * called on debug builds only
     */
    @SuppressWarnings("deprecation")
    private void processInjectedCredentials()
    {
        final String authToken = getIntent().getStringExtra(PrefsKey.AUTH_TOKEN);
        if (authToken != null)
        {
            //want to set even if empty string, in the case of testing
            mPrefsManager.setSecureString(PrefsKey.AUTH_TOKEN, authToken);
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
        return !TextUtils.isEmpty(mAuthToken);
    }
}
