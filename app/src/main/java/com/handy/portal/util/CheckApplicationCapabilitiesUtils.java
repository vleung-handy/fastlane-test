package com.handy.portal.util;

import android.content.Context;
import android.content.pm.PackageManager;
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
            return !(state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED ||
                    state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER
                    || state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_UNTIL_USED);
        } catch (Exception e)
        {
            //package not found
            e.printStackTrace();
            return false;
        }

    }
}
