package com.handy.portal.data;

import com.handy.portal.BuildConfig;

public class FlavorManager
{
    public static final String BETA_FLAVOR = "beta";
    public static final String STABLE_FLAVOR = "stable";
    public static final String STAGE_FLAVOR = "stage";
    public static final String LOCAL_FLAVOR = "local";

    public boolean isBetaFlavor()
    {
        return BETA_FLAVOR.equals(getFlavor());
    }

    public boolean isStageFlavor()
    {
        return STAGE_FLAVOR.equals(getFlavor());
    }

    public boolean isLocalFlavor()
    {
        return LOCAL_FLAVOR.equals(getFlavor());
    }

    public boolean isStableFlavor()
    {
        return STABLE_FLAVOR.equals(getFlavor());
    }

    public String getFlavor() { return BuildConfig.FLAVOR; }
}
