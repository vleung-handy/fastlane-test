package com.handy.portal.library.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.StringDef;

import com.google.common.base.Strings;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


public class IDVerificationUtils {
    public static final String ID_VERIFICATION_SUCCESS = "success";
    public static final String ID_VERIFICATION_CANCELLATION = "cancellation";
    public static final String ID_VERIFICATION_INIT_ERROR = "error";


    @StringDef({ID_VERIFICATION_SUCCESS, ID_VERIFICATION_CANCELLATION, ID_VERIFICATION_INIT_ERROR})
    @Retention(RetentionPolicy.SOURCE)
    public @interface IdVerificationStatus {}

    public static void initJumioWebFlow(Context context, String url) {
        if (!Strings.isNullOrEmpty(url)) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            Utils.safeLaunchIntent(browserIntent, context);
        }
    }
}
