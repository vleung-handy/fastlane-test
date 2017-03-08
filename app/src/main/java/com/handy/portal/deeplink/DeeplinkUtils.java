package com.handy.portal.deeplink;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.handy.portal.core.constant.BundleKeys;

import java.util.List;

public class DeeplinkUtils {
    public static final String HANDY_PRO_PATH_PREFIX = "hp";

    /**
     * This is the parameter we accept for /hp/scheduled_jobs?day=0
     */
    public static final String DEEP_LINK_PARAM_DAY = "day";

    public static final String DEEP_LINK_PARAM_FIRST_AVAILABLE = "first_available";
    public static final String DEEP_LINK_AVAILABLE_HOURS = "available_hours";

    @Nullable
    public static Bundle createDeeplinkBundleFromUri(final Uri uri) {
        if (uri != null) {
            final Bundle deeplinkBundle = new Bundle();
            final String path = sanitizeUriPath(uri);
            if (DeeplinkMapper.getPageForDeeplink(path) != null) {
                handleScheduledJobsAvailabilityDeepLink(deeplinkBundle, path);
                deeplinkBundle.putString(BundleKeys.DEEPLINK, path);
                for (String key : uri.getQueryParameterNames()) {
                    deeplinkBundle.putString(key, uri.getQueryParameter(key));
                    // TODO: Support list types
                }
                return deeplinkBundle;
            }
            else {
                return null;
            }
        }
        return null;
    }

    /**
     * "availability" is a sub page of scheduled-jobs, so we handle that by navigating to the
     * scheduled jobs page and enter
     *
     * @param deeplinkBundle
     * @param path
     */
    private static void handleScheduledJobsAvailabilityDeepLink(
            @NonNull Bundle deeplinkBundle,
            @NonNull final String path) {
        if (path.contains(DeeplinkMapper.SCHEDULE_AVAILABILITY)) {
            deeplinkBundle.putBoolean(DEEP_LINK_AVAILABLE_HOURS, true);
        }
    }

    // This removes "/hp" from the beginning of the URI.
    private static String sanitizeUriPath(final Uri uri) {
        final List<String> pathSegments = uri.getPathSegments();
        final StringBuilder builder = new StringBuilder();
        for (int i = 0; i < pathSegments.size(); i++) {
            final String segment = pathSegments.get(i);
            if (!segment.equalsIgnoreCase(HANDY_PRO_PATH_PREFIX)) {
                builder.append(segment);
                if (i < pathSegments.size() - 1) {
                    builder.append("/");
                }
            }
        }
        return builder.toString();
    }
}
