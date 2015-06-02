package com.handy.portal.data;

import com.handy.portal.BuildConfig;

public class BuildConfigWrapper
{
    public static final String BETA_FLAVOR = "beta";
    public static final String STABLE_FLAVOR = "stable";

    public boolean isBetaFlavor()
    {
        return BETA_FLAVOR.equals(getFlavor());
    }

    public boolean isStableFlavor()
    {
        return STABLE_FLAVOR.equals(getFlavor());
    }

    public boolean isDebug()
    {
        return BuildConfig.DEBUG;
    }

    public String getFlavor()
    {
        return BuildConfig.FLAVOR;
    }
}
