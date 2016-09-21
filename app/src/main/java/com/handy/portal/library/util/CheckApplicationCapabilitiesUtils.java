package com.handy.portal.library.util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;

public class CheckApplicationCapabilitiesUtils
{
    public static final String DOWNLOAD_MANAGER_PACKAGE_NAME = "com.android.providers.downloads";

    public static boolean isExternalStorageWritable()
    {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    public static boolean isDownloadManagerEnabled(Context context)
    {
        try
        {
            int state = context.getPackageManager().getApplicationEnabledSetting(DOWNLOAD_MANAGER_PACKAGE_NAME);
            //is there a way to just check if there is a package that handles downloads?
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2)
            {
                return !(state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED
                        || state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER
                        || state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_UNTIL_USED);
            }
            else
            {
                return !(state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED
                        || state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER);
            }
        }
        catch (Exception e)
        {
            //package not found
            e.printStackTrace();
            return false;
        }

    }
}
