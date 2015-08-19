package com.handy.portal.core;

import com.handy.portal.BuildConfig;

public class BuildConfigWrapper
{
    public boolean isDebug()
    {
        return BuildConfig.DEBUG;
    }

    public String getFlavor()
    {
        return BuildConfig.FLAVOR;
    }
}
