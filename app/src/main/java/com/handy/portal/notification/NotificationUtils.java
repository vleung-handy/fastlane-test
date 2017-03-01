package com.handy.portal.notification;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Build;
import android.support.annotation.IntDef;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class NotificationUtils {
    public static final int NOTIFICATION_ENABLED = 1;
    public static final int NOTIFICATION_DISABLED = 2;
    public static final int NOTIFICATION_UNKNOWN = 3;


    @IntDef({NOTIFICATION_ENABLED, NOTIFICATION_DISABLED, NOTIFICATION_UNKNOWN})
    public @interface NotificationStatus {}


    private static final String CHECK_OP_NO_THROW = "checkOpNoThrow";
    private static final String OP_POST_NOTIFICATION = "OP_POST_NOTIFICATION";

    @NotificationStatus
    public static int isNotificationEnabled(Context context) {
        Object service = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            service = context.getSystemService(Context.APP_OPS_SERVICE);
        }

        if (service == null) { return NOTIFICATION_UNKNOWN; }

        AppOpsManager appOps = (AppOpsManager) service;
        ApplicationInfo appInfo = context.getApplicationInfo();

        String pkg = context.getApplicationContext().getPackageName();

        int uid = appInfo.uid;

        try {
            // AppOpsManager.checkOpNoThrow(int, String, int) is a hidden method.
            // We'll have to use reflection magic to get round it.
            Class appOpsClass = Class.forName(AppOpsManager.class.getName());

            Method checkOpNoThrowMethod =
                    appOpsClass.getMethod(CHECK_OP_NO_THROW, Integer.TYPE, Integer.TYPE, String.class);

            Field opPostNotificationValue = appOpsClass.getDeclaredField(OP_POST_NOTIFICATION);
            int value = (int) opPostNotificationValue.get(Integer.class);

            return ((int) checkOpNoThrowMethod.invoke(appOps, value, uid, pkg) == AppOpsManager.MODE_ALLOWED) ?
                    NOTIFICATION_ENABLED : NOTIFICATION_DISABLED;
        }
        catch (Exception e) {
            e.printStackTrace();
            return NOTIFICATION_UNKNOWN;
        }
    }
}
