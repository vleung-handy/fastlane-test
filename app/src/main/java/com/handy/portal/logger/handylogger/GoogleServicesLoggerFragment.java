package com.handy.portal.logger.handylogger;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.handy.portal.library.util.Utils;
import com.handy.portal.logger.handylogger.model.GoogleServicesLog;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

/**
 * headless fragment used for logging google services info such as
 * whether play store is installed, whether play services is available
 * and whether google account exists on device,
 * handling runtime permissions if necessary
 */

public class GoogleServicesLoggerFragment extends Fragment
{
    private static final int GET_ACCOUNTS_PERMISSION_REQUEST_CODE = 1001;

    public static final String TAG = GoogleServicesLoggerFragment.class.getName();

    @Inject
    EventBus mBus;

    public static GoogleServicesLoggerFragment newInstance()
    {
        return new GoogleServicesLoggerFragment();
    }

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Utils.inject(getContext(), this);
        setRetainInstance(true);

        logGooglePlayStoreInstallationStatus();
        logGooglePlayServicesAvailability();
        logGoogleAccountExistenceOrRequestAccountPermission();
    }

    private void logGooglePlayServicesAvailability()
    {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(getContext());
        boolean playServicesAvailable = resultCode == ConnectionResult.SUCCESS;
        mBus.post(new LogEvent.AddLogEvent(
                new GoogleServicesLog.PlayServicesAvailability(playServicesAvailable)));
    }

    private void logGooglePlayStoreInstallationStatus()
    {
        boolean playStoreInstalled = false;
        try
        {
            PackageInfo packageInfo = getContext().getPackageManager()
                    .getPackageInfo(GooglePlayServicesUtil.GOOGLE_PLAY_STORE_PACKAGE, 0);
            playStoreInstalled = packageInfo != null;
        }
        catch (PackageManager.NameNotFoundException e)
        {
            //no google play store
        }
        mBus.post(new LogEvent.AddLogEvent(
                new GoogleServicesLog.PlayStoreInstallationStatus(playStoreInstalled)));
    }

    private void logGoogleAccountExistenceOrRequestAccountPermission()
    {
        AccountManager accountManager = AccountManager.get(getContext());
        if (ActivityCompat.checkSelfPermission(
                getContext(),
                Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED)
        {
            //ask for permission to get accounts
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.GET_ACCOUNTS},
                    GET_ACCOUNTS_PERMISSION_REQUEST_CODE);
        }
        else
        {
            //check if google account set up on device
            Account[] accounts = accountManager.getAccountsByType("com.google");
            boolean googleAccountExists = accounts.length >= 1;
            mBus.post(new LogEvent.AddLogEvent(new GoogleServicesLog.AccountExistence(googleAccountExists)));
        }
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults)
    {
        if (requestCode == GET_ACCOUNTS_PERMISSION_REQUEST_CODE)
        {
            logGoogleAccountExistenceOrRequestAccountPermission();
        }
    }
}
