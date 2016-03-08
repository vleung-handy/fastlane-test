package com.handy.portal.util;

import java.util.regex.Pattern;

public enum SupportedDeeplinkPath
{
    ROOT(Pattern.compile("/hp/?\\z")) // /hp or /hp/
    ;

    private Pattern mPathPattern;

    SupportedDeeplinkPath(final Pattern pathPattern)
    {
        mPathPattern = pathPattern;
    }

    public boolean matches(final String path)
    {
        return mPathPattern.matcher(path).matches();
    }

    public static boolean matchesAny(final String path)
    {
        for (final SupportedDeeplinkPath supportedDeeplinkPath : values())
        {
            if (supportedDeeplinkPath.matches(path))
            {
                return true;
            }
        }
        return false;
    }
}
