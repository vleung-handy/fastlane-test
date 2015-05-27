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
import com.handy.portal.core.UpdateManager;
import com.handy.portal.core.UserManager;
import com.handy.portal.data.DataManager;
import com.handy.portal.data.DataManagerErrorHandler;
import com.handy.portal.data.Mixpanel;
import com.handy.portal.event.Event;
import com.handy.portal.ui.widget.ProgressDialog;
import com.handy.portal.util.FlavorUtils;
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
    UserManager userManager;
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


    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);


        //Crashlytics.start(this);
        //Yozio.initialize(this);

        ((BaseApplication) this.getApplication()).inject(this);

//        if (!BuildConfig.FLAVOR.equals(BaseApplication.FLAVOR_STAGE)
//                && !BuildConfig.BUILD_TYPE.equals("debug")) {
//            setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//            //Yozio.YOZIO_ENABLE_LOGGING = false;
//        }

        final Intent intent = getIntent();
        final Uri data = intent.getData();

//        if (data != null && data.getHost() != null && data.getHost().equals("deeplink.yoz.io"))
//        {
//            //mixpanel.trackEventYozioOpen(Yozio.getMetaData(intent));
//        }

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

//        if (PlayServicesUtils.isGooglePlayStoreAvailable()) {
//            PlayServicesUtils.handleAnyPlayServicesError(this);
//        }

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
            bus.post(new Event.UpdateCheckEvent(FlavorUtils.getFlavor(), pInfo.versionCode));
        } catch (PackageManager.NameNotFoundException e)
        {
            throw new RuntimeException();
        }

    }

    public void onUpdateCheckReceived(Event.UpdateCheckRequestReceivedEvent event)
    {
        if (event.updateDetails.getShouldUpdate())
        {
            startActivity(new Intent(this, PleaseUpdateActivity.class));
        }
    }

}
