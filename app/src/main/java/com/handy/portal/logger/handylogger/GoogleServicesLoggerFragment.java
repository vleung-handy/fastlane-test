package com.handy.portal.logger.handylogger;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.handy.portal.library.util.FragmentUtils;
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

    private static final String[] REQUIRED_PERMISSIONS = new String[]{Manifest.permission.GET_ACCOUNTS};

    public static final String TAG = GoogleServicesLoggerFragment.class.getName();

    /**
     * needed because account existence is logged in onResume
     * because we need to handle required permissions for it
     * and we don't want multiple unnecessary logs for this instance
     */
    private static boolean sAccountExistenceLogged = false;

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

    @Override
    public void onResume()
    {
        super.onResume();
        logGoogleAccountExistenceOrRequestAccountPermission();
        /*
        putting this in resume to handle case in which user
        is redirected to settings to change permissions, but
        simply navigates back without doing anything

        also cannot just use onRequestPermissionsResult()
        because we use custom permissions dialog
         */
    }

    @SuppressWarnings({"MissingPermission"})
    private void logGoogleAccountExistenceOrRequestAccountPermission()
    {
        if(sAccountExistenceLogged) return;

        AccountManager accountManager = AccountManager.get(getContext());
        if (!Utils.areAllPermissionsGranted(getContext(), REQUIRED_PERMISSIONS))
        {
            if (Utils.wereAnyPermissionsRequestedPreviously(getActivity(), REQUIRED_PERMISSIONS))
            {
                /*
                show our custom permissions dialog with explanation if
                the user already denied permissions via default dialog
                 */
                if(getActivity().getSupportFragmentManager().findFragmentByTag(AccountPermissionsBlockerDialogFragment.TAG) == null)
                {
                    FragmentUtils.safeLaunchDialogFragment(new AccountPermissionsBlockerDialogFragment(),
                            getActivity(), AccountPermissionsBlockerDialogFragment.TAG);
                }
            }
            else
            {
                //ask for permission to get accounts using default permissions dialog
                requestPermissions(REQUIRED_PERMISSIONS,
                        GET_ACCOUNTS_PERMISSION_REQUEST_CODE);
            }
        }
        else
        {
            /*
            won't throw SecurityException at this point because
            this logic is only reached if all permissions were granted
             */
            //check if google account set up on device
            Account[] accounts = accountManager.getAccountsByType("com.google");
            boolean googleAccountExists = accounts.length >= 1;
            mBus.post(new LogEvent.AddLogEvent(new GoogleServicesLog.AccountExistence(googleAccountExists)));
            sAccountExistenceLogged = true;
        }
    }

    //don't need onRequestPermissionsResult() because onResume already checks for permissions again
}
