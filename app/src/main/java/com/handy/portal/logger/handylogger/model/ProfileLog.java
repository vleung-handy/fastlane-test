package com.handy.portal.logger.handylogger.model;

import com.google.gson.annotations.SerializedName;

public class ProfileLog extends EventLog
{
    private static final String EVENT_CONTEXT = "profile";

    public ProfileLog(String eventType)
    {
        super(eventType, EVENT_CONTEXT);
    }

    // Referral logs


    public static class ReferralSelected extends ProfileLog
    {
        private static final String EVENT_TYPE = "referral_selected";

        public ReferralSelected()
        {
            super(EVENT_TYPE);
        }
    }

    // Resupply kit logs


    public static class ResupplyKitSelected extends ProfileLog
    {
        private static final String EVENT_TYPE = "resupply_kit_selected";

        public ResupplyKitSelected()
        {
            super(EVENT_TYPE);
        }
    }


    public static class ResupplyKitRequestSubmitted extends ProfileLog
    {
        private static final String EVENT_TYPE = "resupply_kit_request_submitted";

        public ResupplyKitRequestSubmitted()
        {
            super(EVENT_TYPE);
        }
    }


    public static class ResupplyKitRequestConfirmed extends ProfileLog
    {
        private static final String EVENT_TYPE = "resupply_kit_confirmed";

        public ResupplyKitRequestConfirmed()
        {
            super(EVENT_TYPE);
        }
    }


    public static class ResupplyKitRequestError extends ProfileLog
    {
        private static final String EVENT_TYPE = "resupply_kit_error";

        @SerializedName("error_message")
        private final String mErrorMessage;

        public ResupplyKitRequestError(final String errorMessage)
        {
            super(EVENT_TYPE);
            mErrorMessage = errorMessage;
        }
    }

    // Edit profile logs


    public static class EditProfileSelected extends ProfileLog
    {
        private static final String EVENT_TYPE = "edit_profile_selected";

        public EditProfileSelected()
        {
            super(EVENT_TYPE);
        }
    }


    public static class EditProfileSubmitted extends ProfileLog
    {
        private static final String EVENT_TYPE = "edit_profile_submitted";

        public EditProfileSubmitted()
        {
            super(EVENT_TYPE);
        }
    }


    public static class EditProfileConfirmed extends ProfileLog
    {
        private static final String EVENT_TYPE = "edit_profile_confirmed";

        public EditProfileConfirmed()
        {
            super(EVENT_TYPE);
        }
    }


    public static class EditProfileValidationFailure extends ProfileLog
    {
        private static final String EVENT_TYPE = "edit_profile_validation_failure";

        @SerializedName("error_message")
        private final String mErrorMessage;

        public EditProfileValidationFailure(final String errorMessage)
        {
            super(EVENT_TYPE);
            mErrorMessage = errorMessage;
        }
    }


    public static class EditProfileError extends ProfileLog
    {
        private static final String EVENT_TYPE = "edit_profile_error";

        @SerializedName("error_message")
        private final String mErrorMessage;

        public EditProfileError(final String errorMessage)
        {
            super(EVENT_TYPE);
            mErrorMessage = errorMessage;
        }
    }
}
