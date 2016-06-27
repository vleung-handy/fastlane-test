package com.handy.portal.util;

import android.net.Uri;
import android.os.Bundle;

import com.handy.portal.constant.BundleKeys;

import java.util.List;

public class DeeplinkUtils
{
    public static final String HANDY_PRO_PATH_PREFIX = "hp";

    public static Bundle createDeeplinkBundleFromUri(final Uri uri)
    {
        if (uri != null)
        {
            final Bundle deeplinkBundle = new Bundle();
            final String path = sanitizeUriPath(uri);
            if (DeeplinkMapper.getPageForDeeplink(path) != null)
            {
                deeplinkBundle.putString(BundleKeys.DEEPLINK, path);
                for (String key : uri.getQueryParameterNames())
                {
                    deeplinkBundle.putString(key, uri.getQueryParameter(key));
                    // TODO: Support list types
                }
                return deeplinkBundle;
            }
            else
            {
                return null;
            }
        }
        return null;
    }

    // This removes "/hp" from the beginning of the URI.
    private static String sanitizeUriPath(final Uri uri)
    {
        final List<String> pathSegments = uri.getPathSegments();
        final StringBuilder builder = new StringBuilder();
        for (int i = 0; i < pathSegments.size(); i++)
        {
            final String segment = pathSegments.get(i);
            if (!segment.equalsIgnoreCase(HANDY_PRO_PATH_PREFIX))
            {
                builder.append(segment);
                if (i < pathSegments.size() - 1)
                {
                    builder.append("/");
                }
            }
        }
        return builder.toString();
    }
}
