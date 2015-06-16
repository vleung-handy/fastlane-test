package com.handy.portal.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.handy.portal.core.BaseApplication;
import com.handy.portal.core.GoogleService;
import com.handy.portal.core.LoginManager;
import com.handy.portal.core.NavigationManager;
import com.handy.portal.core.TermsManager;
import com.handy.portal.core.UpdateManager;
import com.handy.portal.data.DataManager;
import com.handy.portal.data.DataManagerErrorHandler;
import com.handy.portal.data.BuildConfigWrapper;
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
            public void onUpdateCheckReceived(Event.UpdateCheckRequestReceivedEvent event)
            {
                BaseActivity.this.onUpdateCheckReceived(event);
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
        PackageInfo pInfo = null;
        try
        {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            bus.post(new Event.UpdateCheckEvent(buildConfigWrapper.getFlavor(), pInfo.versionCode));
        } catch (PackageManager.NameNotFoundException e)
        {
            throw new RuntimeException();
        }
    }

    public void onUpdateCheckReceived(Event.UpdateCheckRequestReceivedEvent event)
    {
        if (event.updateDetails != null && event.updateDetails.getShouldUpdate())
        {
            startActivity(new Intent(this, PleaseUpdateActivity.class));
        }
    }

}
