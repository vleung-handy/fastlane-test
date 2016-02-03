package com.handy.portal.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.handy.portal.constant.BundleKeys;
import com.handy.portal.constant.DeeplinkConstants;
import com.handy.portal.ui.activity.SplashActivity;

public class PushUtils
{
    public static boolean handleDeeplink(@NonNull final Context context,
                                         @NonNull final Bundle arguments)
    {
        final String deeplink = arguments.getString(BundleKeys.DEEPLINK);
        if (deeplink == null)
        {
            return false;
        }
        switch (deeplink)
        {
            case DeeplinkConstants.DEEPLINK_BOOKING_DETAILS:
                showBookingDetails(context, arguments);
                return true;
            default:
                return false;
        }
    }

    private static void showBookingDetails(@NonNull final Context context,
                                           @NonNull final Bundle arguments)
    {
        // TODO: Correct implementation
        final Bundle filteredArguments =
                filterArgumentsByKey(arguments, BundleKeys.BOOKING_ID, BundleKeys.BOOKING_TYPE);
        final Intent intent = getLaunchIntent(context, SplashActivity.class, filteredArguments);
        context.startActivity(intent);
    }

    private static Bundle filterArgumentsByKey(Bundle arguments, String... keys)
    {
        Bundle filteredArguments = new Bundle();
        for (String key : keys)
        {
            filteredArguments.putString(key, arguments.getString(key));
        }
        return filteredArguments;
    }

    private static Intent getLaunchIntent(final Context context,
                                          final Class<? extends Activity> activityClass,
                                          final Bundle arguments)
    {
        final Intent intent = new Intent(context, activityClass);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtras(arguments);
        return intent;
    }
}
