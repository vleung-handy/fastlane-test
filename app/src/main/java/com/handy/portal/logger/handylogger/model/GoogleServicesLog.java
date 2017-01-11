package com.handy.portal.logger.handylogger.model;

import com.google.gson.annotations.SerializedName;

/**
 * we want to know the availability of google services on this device
 * so we can evaluate whether it'd be a good experience to host the app on the play store instead
 * and determine when certain google services (location, maps) do not work
 */
public abstract class GoogleServicesLog extends EventLog
{
    private static final String EVENT_CONTEXT = "google_services";

    private GoogleServicesLog(String eventType)
    {
        super(eventType, EVENT_CONTEXT);
    }

    public static class PlayStoreInstallationStatus extends GoogleServicesLog
    {
        private static final String EVENT_TYPE = "play_store_installation_status";

        @SerializedName("installed")
        private final boolean mInstalled;

        public PlayStoreInstallationStatus(final boolean playStoreInstalled)
        {
            super(EVENT_TYPE);
            mInstalled = playStoreInstalled;
        }
    }

    public static class PlayServicesAvailability extends GoogleServicesLog
    {
        private static final String EVENT_TYPE = "play_services_availability";

        @SerializedName("available")
        private final boolean mAvailable;

        public PlayServicesAvailability(final boolean available)
        {
            super(EVENT_TYPE);
            mAvailable = available;
        }
    }


    /**
     * whether a Google account exists on the device
     */
    public static class AccountExistence extends GoogleServicesLog
    {
        private static final String EVENT_TYPE = "account_existence";

        @SerializedName("exists")
        private final boolean mAccountExists;

        public AccountExistence(final boolean accountExists)
        {
            super(EVENT_TYPE);
            mAccountExists = accountExists;
        }
    }
}
