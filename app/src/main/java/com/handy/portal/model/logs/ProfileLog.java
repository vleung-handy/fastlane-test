package com.handy.portal.model.logs;

public class ProfileLog extends EventLog
{
    private static final String EVENT_CONTEXT = "profile";

    protected ProfileLog(
            String osVersion, String appVersion, String deviceId, long timestamp,
            String providerId, String versionTrack, String eventType)
    {
        super(osVersion, appVersion, deviceId, timestamp, providerId, versionTrack,
                eventType, EVENT_CONTEXT);
    }

    public static class ReferralSelectedLog extends ProfileLog
    {
        private static final String EVENT_TYPE = "referral_selected";

        protected ReferralSelectedLog(
                String osVersion, String appVersion, String deviceId, long timestamp,
                String providerId, String versionTrack)
        {
            super(osVersion, appVersion, deviceId, timestamp, providerId, versionTrack, EVENT_TYPE);
        }
    }

    public static class ResupplyKitSelectedLog extends ProfileLog
    {
        private static final String EVENT_TYPE = "resupply_kit_selected";

        protected ResupplyKitSelectedLog(
                String osVersion, String appVersion, String deviceId, long timestamp,
                String providerId, String versionTrack)
        {
            super(osVersion, appVersion, deviceId, timestamp, providerId, versionTrack, EVENT_TYPE);
        }
    }

    public static class ResupplyKitConfirmedLog extends ProfileLog
    {
        private static final String EVENT_TYPE = "resupply_kit_confirmed";

        protected ResupplyKitConfirmedLog(
                String osVersion, String appVersion, String deviceId, long timestamp,
                String providerId, String versionTrack)
        {
            super(osVersion, appVersion, deviceId, timestamp, providerId, versionTrack, EVENT_TYPE);
        }
    }

    public static class EditProfileSelectedLog extends ProfileLog
    {
        private static final String EVENT_TYPE = "edit_profile_selected";

        protected EditProfileSelectedLog(
                String osVersion, String appVersion, String deviceId, long timestamp,
                String providerId, String versionTrack)
        {
            super(osVersion, appVersion, deviceId, timestamp, providerId, versionTrack, EVENT_TYPE);
        }
    }

    public static class EditProfileConfirmedLog extends ProfileLog
    {
        private static final String EVENT_TYPE = "edit_profile_confirmed";

        protected EditProfileConfirmedLog(
                String osVersion, String appVersion, String deviceId, long timestamp,
                String providerId, String versionTrack)
        {
            super(osVersion, appVersion, deviceId, timestamp, providerId, versionTrack, EVENT_TYPE);
        }
    }
}
