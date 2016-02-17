package com.handy.portal.location;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.crashlytics.android.Crashlytics;

/**
 * starts location service on device boot up
 */
public class BootBroadcastReceiver extends BroadcastReceiver
{
    static final String ACTION_BOOT_COMPLETED = "android.intent.action.BOOT_COMPLETED";
    @Override
    public void onReceive(final Context context, final Intent intent)
    {
        //TODO: make it not start the service if config param says not to
        if(ACTION_BOOT_COMPLETED.equals(intent.getAction()))
        {
            Log.i(getClass().getName(), "Boot completed. Starting location service...");
            try
            {
                Intent serviceIntent = new Intent(context, LocationService.class);
                context.startService(serviceIntent);
            }
            catch (Exception e)
            {
                //TODO: probably should never happen, but just in case, handle gracefully for now
                e.printStackTrace();
                Crashlytics.logException(e);
            }

        }
    }
}
