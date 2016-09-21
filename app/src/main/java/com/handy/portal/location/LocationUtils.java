package com.handy.portal.location;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;

import com.crashlytics.android.Crashlytics;
import com.handy.portal.library.util.FragmentUtils;
import com.handy.portal.library.util.SystemUtils;
import com.handy.portal.library.util.TextUtils;
import com.handy.portal.library.util.Utils;
import com.handy.portal.location.scheduler.LocationScheduleService;
import com.handy.portal.location.ui.LocationPermissionsBlockerDialogFragment;
import com.handy.portal.location.ui.LocationSettingsBlockerDialogFragment;

/**
 * utility class for location-related stuff
 */
public abstract class LocationUtils
{
    private static final int ACCESS_FINE_LOCATION_PERMISSION_REQUEST_CODE = 10;

    //TODO don't like some of these methods, refactor later
    /**
     * either shows the necessary location blockers,
     * or launches the location service
     *
     * @param fragmentActivity
     * @param isLocationServiceEnabled
     */
    public static void showLocationBlockersOrStartServiceIfNecessary(
            @NonNull FragmentActivity fragmentActivity,
            boolean isLocationServiceEnabled)
    {
        /*
        because this can be called each time this resumes,
        putting it in a try/catch block to be super safe to prevent crashes
         */
        try
        {
            showNecessaryLocationSettingsAndPermissionsBlockers(fragmentActivity);
            startLocationServiceIfNecessary(fragmentActivity, isLocationServiceEnabled);
        }
        catch (Exception e)
        {
            Crashlytics.logException(e);
        }
    }

    private static void showNecessaryLocationSettingsAndPermissionsBlockers(FragmentActivity fragmentActivity)
    {
        showLocationPermissionsBlockerIfNecessary(fragmentActivity);
        showLocationSettingsBlockerIfNecessary(fragmentActivity);
    }
    /**
     * called in onResume
     * <p/>
     * determines if,
     * in kitkat and above: the user has the location setting on
     * pre-kitkat: user has any location provider enabled
     * <p/>
     * if not, block them with a dialog until they do.
     */
    private static void showLocationSettingsBlockerIfNecessary(FragmentActivity fragmentActivity)
    {
        //check whether location services setting is on
        if (!LocationUtils.hasRequiredLocationSettings(fragmentActivity) &&
                fragmentActivity.getSupportFragmentManager().findFragmentByTag(LocationSettingsBlockerDialogFragment.FRAGMENT_TAG) == null)
        //don't want to show this dialog if it's already showing
        {
            LocationSettingsBlockerDialogFragment locationSettingsBlockerDialogFragment
                    = new LocationSettingsBlockerDialogFragment();
            FragmentUtils.safeLaunchDialogFragment(locationSettingsBlockerDialogFragment, fragmentActivity,
                    LocationSettingsBlockerDialogFragment.FRAGMENT_TAG);
        }
    }

    /**
     * shows the location permissions blocker (Android 6.0+) if user didn't grant permissions yet
     */
    private static void showLocationPermissionsBlockerIfNecessary(FragmentActivity fragmentActivity)
    {
        if (!LocationUtils.hasRequiredLocationPermissions(fragmentActivity) &&
                fragmentActivity.getSupportFragmentManager().findFragmentByTag(LocationPermissionsBlockerDialogFragment.FRAGMENT_TAG) == null)
        {
            if (Utils.wereAnyPermissionsRequestedPreviously(fragmentActivity, LocationConstants.LOCATION_PERMISSIONS))
            {
                //this will be shown if the app requested this permission previously and the user denied the request or revoked it
                FragmentUtils.safeLaunchDialogFragment(new LocationPermissionsBlockerDialogFragment(),
                        fragmentActivity, LocationPermissionsBlockerDialogFragment.FRAGMENT_TAG);
            }
            else
            {
                //otherwise show the default permission request dialog
                ActivityCompat.requestPermissions(fragmentActivity, LocationConstants.LOCATION_PERMISSIONS, ACCESS_FINE_LOCATION_PERMISSION_REQUEST_CODE);
            }
        }
    }

    /**
     * don't want to simply call startLocationServiceIfNecessary because when permissions dialog disappears,
     * onResume() is called and thus triggers onConfigSuccess which wants to start this service,
     * and the startLocationServiceIfNecessary may launch the permissions dialog
     * TODO see if we can clean this up
     */
    public static void startLocationServiceIfNecessary(@NonNull FragmentActivity fragmentActivity,
                                                boolean isLocationServiceEnabled)
    {
        if (LocationUtils.hasRequiredLocationPermissions(fragmentActivity)
                && LocationUtils.hasRequiredLocationSettings(fragmentActivity))
        {
            Intent locationServiceIntent = new Intent(fragmentActivity, LocationScheduleService.class);
            if (isLocationServiceEnabled)
            {
                //nothing will happen if it's already running
                if (!SystemUtils.isServiceRunning(fragmentActivity, LocationScheduleService.class))
                {
                    fragmentActivity.startService(locationServiceIntent);
                }
            }
            else
            {
                //nothing will happen if it's not running
                fragmentActivity.stopService(locationServiceIntent);
            }
            //at most one service instance will be running
        }
    }

    @SuppressWarnings("deprecation")
    public static boolean hasRequiredLocationSettings(@NonNull Context context)
    {
        boolean locationServicesEnabled;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
        {
            int locationMode = 0;
            try
            {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
            }
            catch (Settings.SettingNotFoundException e)
            {
                e.printStackTrace();
                Crashlytics.logException(e);
            }
            locationServicesEnabled = locationMode != Settings.Secure.LOCATION_MODE_OFF;
        }
        else
        {
            //in versions before KitKat, must check for a different settings key
            String locationProviders =
                    Settings.Secure.getString(context.getContentResolver(),
                            Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            locationServicesEnabled = !TextUtils.isNullOrEmpty(locationProviders);
        }
        return locationServicesEnabled;
    }

    /**
     * convenience method for checking if the device has required location permissions
     *
     * @param context
     * @return
     */
    public static boolean hasRequiredLocationPermissions(@NonNull Context context)
    {
        return Utils.areAllPermissionsGranted(context, LocationConstants.LOCATION_PERMISSIONS);
    }
}
