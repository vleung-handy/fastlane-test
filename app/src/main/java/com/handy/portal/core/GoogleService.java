package com.handy.portal.core;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.handy.portal.ui.activity.MainActivity;
import com.handy.portal.util.Utils;

/**
 * Created by cdavis on 4/30/15.
 */
public class GoogleService
{
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    public static final String PROPERTY_REG_ID = "69696969qqq"; // I'm not sure what to put here
    private static final String PROPERTY_APP_VERSION = "1.0";

    private GoogleCloudMessaging gcm;
    private String regId;

    private Context context;

    public GoogleService(Context context)
    {
        this.context = context;
    }

    public GoogleCloudMessaging getCloudMessaging(Activity targetActivity)
    {
        if (gcm == null)
        {
            if (checkPlayServices(targetActivity))
            {
                gcm = GoogleCloudMessaging.getInstance(targetActivity);
                regId = getRegistrationId(context);
            }
        }
        return new GoogleCloudMessaging();
    }

    private boolean checkPlayServices(Activity targetActivity)
    {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(targetActivity);
        if (resultCode != ConnectionResult.SUCCESS)
        {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode))
            {
                GooglePlayServicesUtil.getErrorDialog(resultCode, targetActivity, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else
            {
                targetActivity.finish();
            }
            return false;
        }
        return true;
    }


    /**
     * Gets the current registration ID for application on GCM service.
     * <p/>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     * registration ID.
     */
    private String getRegistrationId(Context context)
    {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty())
        {
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new
        // app version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = Utils.getAppVersion(context);
        if (registeredVersion != currentVersion)
        {
            return "";
        }
        return registrationId;
    }

    /**
     * @return Application's {@code SharedPreferences}.
     */
    private SharedPreferences getGCMPreferences(Context context)
    {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return context.getSharedPreferences(MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }

    public String getOrSetDeviceId()
    {
        new AsyncTask<Void, Void, String>()
        {
            protected String doInBackground(Void... params)
            {
                try
                {
                    if (gcm == null)
                    {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    regId = gcm.register(ServerParams.SENDER_ID);
                    SharedPreferences pref = context.getSharedPreferences("HandybookProviderApp", 0);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("deviceId", regId);
                    editor.commit();
                } catch (Exception ex)
                {
                }
                return "";
            }
        }.execute(null, null, null);
        return regId;
    }

}
