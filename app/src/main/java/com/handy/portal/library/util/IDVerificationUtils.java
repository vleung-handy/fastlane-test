package com.handy.portal.library.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.google.common.base.Strings;


public class IDVerificationUtils
{
    public static void initJumioWebFlow(Context context, String url)
    {
        if (!Strings.isNullOrEmpty(url))
        {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            Utils.safeLaunchIntent(browserIntent, context);
        }
    }
}
