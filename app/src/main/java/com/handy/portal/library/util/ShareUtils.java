package com.handy.portal.library.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

public class ShareUtils
{
    public static final String CHANNEL_EMAIL = "email";
    public static final String CHANNEL_GMAIL = "gmail";
    public static final String CHANNEL_GPLUS = "gplus";
    public static final String CHANNEL_FACEBOOK = "facebook";
    public static final String CHANNEL_TWITTER = "twitter";
    public static final String CHANNEL_SMS = "sms";
    public static final String CHANNEL_OTHER = "other";

    private static final String PACKAGE_IDENTIFIER_GMAIL = "android.gm";
    private static final String PACKAGE_IDENTIFIER_GPLUS = "android.apps.plus";
    private static final String PACKAGE_IDENTIFIER_FACEBOOK = "facebook";
    private static final String PACKAGE_IDENTIFIER_TWITTER = "twitter";
    private static final String SCHEME_SMS = "sms:";
    private static final String SCHEME_MAIL = "mailto:";
    private static final String MIME_TYPE_PLAIN_TEXT = "text/plain";


    @Retention(RetentionPolicy.SOURCE)
    @StringDef({
            CHANNEL_EMAIL,
            CHANNEL_GMAIL,
            CHANNEL_GPLUS,
            CHANNEL_FACEBOOK,
            CHANNEL_TWITTER,
            CHANNEL_SMS,
            CHANNEL_OTHER,
    })
    public @interface Channel {}

    @Channel
    public static String getChannelFromIntent(final Context context, final Intent intent)
    {
        final String targetPackage = intent.getComponent().getPackageName();
        if (targetPackage.contains(PACKAGE_IDENTIFIER_GMAIL))
        {
            return CHANNEL_GMAIL;
        }
        else if (targetPackage.contains(PACKAGE_IDENTIFIER_GPLUS))
        {
            return CHANNEL_GPLUS;
        }
        else if (targetPackage.contains(PACKAGE_IDENTIFIER_FACEBOOK))
        {
            return CHANNEL_FACEBOOK;
        }
        else if (targetPackage.contains(PACKAGE_IDENTIFIER_TWITTER))
        {
            return CHANNEL_TWITTER;
        }
        else if (canPackageHandleScheme(context, targetPackage, SCHEME_SMS))
        {
            return CHANNEL_SMS;
        }
        else if (canPackageHandleScheme(context, targetPackage, SCHEME_MAIL))
        {
            return CHANNEL_EMAIL;
        }
        else
        {
            return CHANNEL_OTHER;
        }
    }

    private static boolean canPackageHandleScheme(
            @NonNull final Context context,
            @NonNull final String targetPackage,
            @NonNull final String scheme
    )
    {
        final Intent dummyIntent = new Intent();
        dummyIntent.setAction(Intent.ACTION_SEND);
        dummyIntent.setData(Uri.parse(scheme));
        dummyIntent.setType(MIME_TYPE_PLAIN_TEXT);
        List<ResolveInfo> resolveInfos =
                context.getPackageManager().queryIntentActivities(dummyIntent, 0);
        for (final ResolveInfo resolveInfo : resolveInfos)
        {
            final String potentialHandlerPackage = resolveInfo.activityInfo.packageName;
            if (potentialHandlerPackage.equalsIgnoreCase(targetPackage))
            {
                return true;
            }
        }
        return false;
    }
}
