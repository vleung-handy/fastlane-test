package com.handy.portal.logger.handylogger.model;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;

import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.gson.annotations.SerializedName;

/**
 * TODO finish this
 * we want to know the availability of google services on this device
 * so we can evaluate whether it'd be a good experience to host the app on the play store instead
 * and determine when certain google services (location, maps) do not work
 */
public abstract class GoogleServicesLog extends EventLog
{
    private static final String EVENT_CONTEXT = "google_services";

    protected GoogleServicesLog(String eventType)
    {
        super(eventType, EVENT_CONTEXT);
    }

    public static class PlayStoreAvailabilityLog extends GoogleServicesLog
    {
        private static final String EVENT_TYPE = "play_store_availability";

        @SerializedName("available")
        private final boolean mAvailable;

        public PlayStoreAvailabilityLog(final boolean available)
        {
            super(EVENT_TYPE);
            mAvailable = available;
        }

        //TODO MOVE SOMEWHERE ELSE!!
        //TODO also need to check if the play store is accessible
        // (ex. in case user not signed in with google account to device)
        public static boolean isGooglePlayStoreInstalled(@NonNull Context context)
        {
            try
            {
                PackageInfo packageInfo = context.getPackageManager()
                        .getPackageInfo(GooglePlayServicesUtil.GOOGLE_PLAY_STORE_PACKAGE, 0);
                return true;
            }
            catch (PackageManager.NameNotFoundException e)
            {
                return false;
            }
        }
    }

    public static class PlayServicesAvailabilityLog extends GoogleServicesLog
    {
        private static final String EVENT_TYPE = "play_services_availability";

        @SerializedName("available")
        private final boolean mAvailable;

        public PlayServicesAvailabilityLog(final boolean available)
        {
            super(EVENT_TYPE);
            mAvailable = available;
        }
        //TODO we need a util to check for this somewhere
    }


    public static class AccountPresentLog extends GoogleServicesLog
    {
        private static final String EVENT_TYPE = "account_present";

        //TODO arbitrary; rename and structure this better
        @SerializedName("account_present")
        private final boolean mAccountPresent;

        public AccountPresentLog(final boolean accountPresent)
        {
            super(EVENT_TYPE);
            mAccountPresent = accountPresent;
        }
    }
}
