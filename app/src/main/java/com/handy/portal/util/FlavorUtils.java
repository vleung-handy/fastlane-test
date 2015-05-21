package com.handy.portal.util;

import com.handy.portal.BuildConfig;

public class FlavorUtils
{
    public static final String BETA_FLAVOR = "beta";
    public static final String STABLE_FLAVOR = "stable";
    public static final String STAGE_FLAVOR = "stage";
    public static final String LOCAL_FLAVOR = "local";

    public static boolean isBetaFlavor()
    {
        return BuildConfig.FLAVOR.equals(BETA_FLAVOR);
    }

    public static boolean isStageFlavor()
    {
        return BuildConfig.FLAVOR.equals(STAGE_FLAVOR);
    }

    public static boolean isLocalFlavor()
    {
        return BuildConfig.FLAVOR.equals(LOCAL_FLAVOR);
    }

    public static boolean isStableFlavor()
    {
        return BuildConfig.FLAVOR.equals(STABLE_FLAVOR);
    }
}
