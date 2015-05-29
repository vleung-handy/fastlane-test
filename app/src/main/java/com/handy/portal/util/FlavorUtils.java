package com.handy.portal.util;

import com.handy.portal.BuildConfig;

public class FlavorUtils
{
    public static final String BETA_FLAVOR = "beta";
    public static final String STABLE_FLAVOR = "stable";

    public static boolean isBetaFlavor()
    {
        return BuildConfig.FLAVOR.equals(BETA_FLAVOR);
    }

    public static boolean isStableFlavor()
    {
        return BuildConfig.FLAVOR.equals(STABLE_FLAVOR);
    }

    public static String getFlavor() { return BuildConfig.FLAVOR; }
}
