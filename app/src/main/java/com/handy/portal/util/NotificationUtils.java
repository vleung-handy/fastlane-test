package com.handy.portal.util;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class NotificationUtils
{
    private static final String CHECK_OP_NO_THROW = "checkOpNoThrow";
    private static final String OP_POST_NOTIFICATION = "OP_POST_NOTIFICATION";

    public static boolean isNotificationEnabled(Context context)
    {
        Object service = context.getSystemService(Context.APP_OPS_SERVICE);

        if (service == null) { return true; }

        AppOpsManager appOps = (AppOpsManager) service;
        ApplicationInfo appInfo = context.getApplicationInfo();

        String pkg = context.getApplicationContext().getPackageName();

        int uid = appInfo.uid;

        try
        {
            Class appOpsClass = Class.forName(AppOpsManager.class.getName());

            Method checkOpNoThrowMethod = appOpsClass.getMethod(CHECK_OP_NO_THROW, Integer.TYPE, Integer.TYPE, String.class);

            Field opPostNotificationValue = appOpsClass.getDeclaredField(OP_POST_NOTIFICATION);
            int value = (int) opPostNotificationValue.get(Integer.class);

            return ((int) checkOpNoThrowMethod.invoke(appOps, value, uid, pkg) == AppOpsManager.MODE_ALLOWED);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        // We don't want to block the if we are not sure the notification if off or not
        return true;
    }
}
