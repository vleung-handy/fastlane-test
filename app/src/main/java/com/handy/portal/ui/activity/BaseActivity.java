package com.handy.portal.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.handy.portal.core.ApplicationOnResumeWatcher;
import com.handy.portal.core.BaseApplication;
import com.handy.portal.core.GoogleService;
import com.handy.portal.core.LoginManager;
import com.handy.portal.core.NavigationManager;
import com.handy.portal.core.TermsManager;
import com.handy.portal.core.UpdateManager;
import com.handy.portal.data.BuildConfigWrapper;
import com.handy.portal.data.DataManager;
import com.handy.portal.data.DataManagerErrorHandler;
import com.handy.portal.data.Mixpanel;
import com.handy.portal.event.Event;
import com.handy.portal.ui.widget.ProgressDialog;
import com.securepreferences.SecurePreferences;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public abstract class BaseActivity extends FragmentActivity
{
    private Object busEventListener;
    protected boolean allowCallbacks;
    private OnBackPressedListener onBackPressedListener;
    protected ProgressDialog progressDialog;

    //Public Properties
    public boolean getAllowCallbacks()
    {
        return this.allowCallbacks;
    }

    @Inject
    Mixpanel mixpanel;
    @Inject
    Bus bus;
    @Inject
    DataManager dataManager;
    @Inject
    DataManagerErrorHandler dataManagerErrorHandler;
    @Inject
    NavigationManager navigationManager;
    @Inject
    GoogleService googleService;
    @Inject
    LoginManager loginManager;
    @Inject
    UpdateManager updateManager;
    @Inject
    TermsManager termsManager;
    ApplicationOnResumeWatcher applicationOnResumeWatcher;
    @Inject
    SecurePreferences prefs;
    @Inject
    BuildConfigWrapper buildConfigWrapper;

    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        ((BaseApplication) this.getApplication()).inject(this);

        final Intent intent = getIntent();
        final Uri data = intent.getData();

        busEventListener = new Object()
        {
            @Subscribe
            public void onUpdateCheckReceived(Event.UpdateAvailable event)
            {
                BaseActivity.this.onUpdateAvailable(event);
            }
        };
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        allowCallbacks = true;
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        allowCallbacks = false;
    }

    @Override
    protected void onPostResume()
    {
        super.onPostResume();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        this.bus.register(busEventListener);
        checkForUpdates();
        postActivityResumeEvent(); //do not disable this
    }

    @Override
    protected void attachBaseContext(Context newBase)
    {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onBackPressed()
    {
        if (onBackPressedListener != null) onBackPressedListener.onBack();
        else super.onBackPressed();
    }

    @Override
    public void onPause()
    {
        bus.unregister(busEventListener);
        super.onPause();
    }

    @Override
    protected void onDestroy()
    {
        mixpanel.flush();
        super.onDestroy();
    }

    public void setOnBackPressedListener(final OnBackPressedListener onBackPressedListener)
    {
        this.onBackPressedListener = onBackPressedListener;
    }

    public interface OnBackPressedListener
    {
        void onBack();
    }

    public void checkForUpdates()
    {
        bus.post(new Event.UpdateCheckEvent(this));
    }

    public void postActivityResumeEvent()
    {
        bus.post(new Event.ActivityResumed(this));
    }

    public void onUpdateAvailable(Event.UpdateAvailable event)
    {
        startActivity(new Intent(this, PleaseUpdateActivity.class));
    }

}
