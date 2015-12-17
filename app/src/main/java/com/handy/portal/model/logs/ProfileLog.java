package com.handy.portal.model.logs;

public class ProfileLog extends EventLog
{
    private static final String EVENT_CONTEXT = "profile";

    public ProfileLog(String providerId, String versionTrack, String eventType)
    {
        super(providerId, versionTrack, eventType, EVENT_CONTEXT);
    }

    public static class ReferralSelectedLog extends ProfileLog
    {
        private static final String EVENT_TYPE = "referral_selected";

        public ReferralSelectedLog(String providerId, String versionTrack)
        {
            super(providerId, versionTrack, EVENT_TYPE);
        }
    }

    public static class ResupplyKitSelectedLog extends ProfileLog
    {
        private static final String EVENT_TYPE = "resupply_kit_selected";

        public ResupplyKitSelectedLog(String providerId, String versionTrack)
        {
            super(providerId, versionTrack, EVENT_TYPE);
        }
    }

    public static class ResupplyKitConfirmedLog extends ProfileLog
    {
        private static final String EVENT_TYPE = "resupply_kit_confirmed";

        public ResupplyKitConfirmedLog(String providerId, String versionTrack)
        {
            super(providerId, versionTrack, EVENT_TYPE);
        }
    }

    public static class EditProfileSelectedLog extends ProfileLog
    {
        private static final String EVENT_TYPE = "edit_profile_selected";

        public EditProfileSelectedLog(String providerId, String versionTrack)
        {
            super(providerId, versionTrack, EVENT_TYPE);
        }
    }

    public static class EditProfileConfirmedLog extends ProfileLog
    {
        private static final String EVENT_TYPE = "edit_profile_confirmed";

        public EditProfileConfirmedLog(String providerId, String versionTrack)
        {
            super(providerId, versionTrack, EVENT_TYPE);
        }
    }
}
