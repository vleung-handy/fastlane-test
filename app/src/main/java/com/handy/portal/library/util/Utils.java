package com.handy.portal.library.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.Gravity;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.handy.portal.R;
import com.handy.portal.core.BaseApplication;

public final class Utils //TODO: we should reorganize these methods into more specific util classes
{
    public final static float MDPI = 1.0f;
    public final static float HDPI = 1.5f;
    public final static float XHDPI = 2.0f;
    public final static float XXHDPI = 3.0f;

    //TODO move somewhere else
    public static boolean areAllPermissionsGranted(@NonNull Context context, @NonNull String[] permissions) {
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(context,
                    permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public static boolean wereAnyPermissionsRequestedPreviously(@NonNull Activity activity, @NonNull String[] permissions) {
        for (String permission : permissions) {
            /**
             *  The method returns true if the app has requested this permission previously and the user denied the request.
             */
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                return true;
            }
        }
        return false;
    }

    public static int getObjectIdentifier(Object object) {
        return System.identityHashCode(object);
    }

    /**
     * @param intent
     * @param context
     * @return true if the given intent was successfully launched
     */
    public static boolean safeLaunchIntent(@NonNull Intent intent, @NonNull Context context) {
        try {
            //check if there's an activity to handle this intent
            if (intent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(intent);
                return true;
            }
            else //no activity found to handle the intent
            {
                //note: this must be called from the UI thread
                Toast toast = Toast.makeText(context, R.string.error_no_intent_handler_found, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                Crashlytics.logException(new Exception("No activity found to handle the intent " + intent.toString()));
            }
        }
        catch (Exception e) {
            Toast toast = Toast.makeText(context, R.string.an_error_has_occurred, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            Crashlytics.logException(e);
        }
        return false;
    }

    public static void inject(Context context, Object object) {
        ((BaseApplication) context.getApplicationContext()).inject(object);
    }

    public static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        }
        catch (PackageManager.NameNotFoundException e) {
            // should never happen
            Crashlytics.logException(new RuntimeException("Could not get package name", e));
            return -1;
        }
    }
}
