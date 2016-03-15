package com.handy.portal.logger.handylogger.model;

public class ProfileLog extends EventLog
{
    private static final String EVENT_CONTEXT = "profile";

    public ProfileLog(String eventType)
    {
        super(eventType, EVENT_CONTEXT);
    }

    public static class ReferralSelectedLog extends ProfileLog
    {
        private static final String EVENT_TYPE = "referral_selected";

        public ReferralSelectedLog()
        {
            super(EVENT_TYPE);
        }
    }


    public static class ResupplyKitSelectedLog extends ProfileLog
    {
        private static final String EVENT_TYPE = "resupply_kit_selected";

        public ResupplyKitSelectedLog()
        {
            super(EVENT_TYPE);
        }
    }


    public static class ResupplyKitConfirmedLog extends ProfileLog
    {
        private static final String EVENT_TYPE = "resupply_kit_confirmed";

        public ResupplyKitConfirmedLog()
        {
            super(EVENT_TYPE);
        }
    }


    public static class EditProfileSelectedLog extends ProfileLog
    {
        private static final String EVENT_TYPE = "edit_profile_selected";

        public EditProfileSelectedLog()
        {
            super(EVENT_TYPE);
        }
    }


    public static class EditProfileConfirmedLog extends ProfileLog
    {
        private static final String EVENT_TYPE = "edit_profile_confirmed";

        public EditProfileConfirmedLog()
        {
            super(EVENT_TYPE);
        }
    }
}
