package com.handy.portal.library.util;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.provider.Settings;
import android.support.annotation.NonNull;

/**
 * utilities to access information about the system
 */
public final class SystemUtils
{
    public static boolean isServiceRunning(@NonNull Context context, @NonNull Class<?> serviceClass)
    {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE))
        {
            if (serviceClass.getName().equals(service.service.getClassName()))
            {
                return true;
            }
        }
        return false;
    }

    public static float getBatteryLevelPercent(@NonNull Context context)
    {
        Intent batteryIntent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        if (batteryIntent == null) { return -1f; }
        int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        if (level == -1 || scale == -1)
        {
            return -1f; //unavailable
        }

        return ((float) level / (float) scale);
    }

    public static String getActiveNetworkType(final Context context)
    {
        final ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null)
        {
            final NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            if (activeNetworkInfo != null)
            {
                final String type = activeNetworkInfo.getTypeName();
                final String subtype = activeNetworkInfo.getSubtypeName();
                return (type + " " + subtype).trim(); // this yields "MOBILE LTE" or "WIFI"
            }
        }
        return "";
    }

    public static String getDeviceId(final Context context)
    {
        return Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }
}
