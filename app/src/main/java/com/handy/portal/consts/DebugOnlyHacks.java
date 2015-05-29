package com.handy.portal.consts;

import com.handy.portal.BuildConfig;

/**
 * Created by cdavis on 5/29/15.
 */
public class DebugOnlyHacks
{
    private static final boolean SKIP_LOGIN = true; //bypass the native login and use the old web login
    private static final String SKIP_LOGIN_USER_ID = "11"; //for quick development by bypassing the login procedure

    private static boolean areHacksAllowed()
    {
        return (BuildConfig.BUILD_TYPE.equals("debug"));
    }

    public static boolean canSkipLogin()
    {
        return areHacksAllowed() && SKIP_LOGIN;
    }

    public static String getSkippedLoginUserId()
    {
        return (areHacksAllowed() ? SKIP_LOGIN_USER_ID : "");
    }

}
