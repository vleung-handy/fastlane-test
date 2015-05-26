package com.handy.portal.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.widget.Toast;

import com.handy.portal.core.BaseApplication;
import com.handy.portal.core.GoogleService;
import com.handy.portal.core.LoginManager;
import com.handy.portal.core.NavigationManager;
import com.handy.portal.core.UserManager;
import com.handy.portal.data.DataManager;
import com.handy.portal.data.DataManagerErrorHandler;
import com.handy.portal.data.Mixpanel;
import com.handy.portal.ui.widget.ProgressDialog;

import javax.inject.Inject;

public abstract class BaseActivity extends FragmentActivity
{

    protected boolean allowCallbacks;
    private OnBackPressedListener onBackPressedListener;
    protected ProgressDialog progressDialog;
    protected Toast toast;

    //Public Properties
    public boolean getAllowCallbacks()
    {
        return this.allowCallbacks;
    }

    @Inject
    Mixpanel mixpanel;
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

        toast = Toast.makeText(this, null, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
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
    public void onBackPressed()
    {
        if (onBackPressedListener != null) onBackPressedListener.onBack();
        else super.onBackPressed();
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

}
