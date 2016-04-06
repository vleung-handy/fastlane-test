package com.handy.portal.location;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;

import com.crashlytics.android.Crashlytics;
import com.handy.portal.util.TextUtils;
import com.handy.portal.util.Utils;

/**
 * utility class for location-related stuff
 */
public abstract class LocationUtils
{
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
