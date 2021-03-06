package com.handy.portal.logger.handylogger.model;

import com.google.gson.annotations.SerializedName;

public class ProfileLog extends EventLog {
    private static final String EVENT_CONTEXT = "profile";

    public ProfileLog(String eventType) {
        super(eventType, EVENT_CONTEXT);
    }

    // Referral logs


    public static class ReferralOpen extends ProfileLog {
        private static final String EVENT_TYPE = "referral_open";

        public ReferralOpen() {
            super(EVENT_TYPE);
        }
    }


    public static class ReferralSelected extends ProfileLog {
        private static final String EVENT_TYPE = "referral_selected";

        public ReferralSelected() {
            super(EVENT_TYPE);
        }
    }

    // Resupply kit logs


    public static class ResupplyKitSelected extends ProfileLog {
        private static final String EVENT_TYPE = "resupply_kit_selected";

        public ResupplyKitSelected() {
            super(EVENT_TYPE);
        }
    }


    public static class ResupplyKitSiteLoadStarted extends ProfileLog {
        private static final String EVENT_TYPE = "resupply_kit_site_load_started";

        public ResupplyKitSiteLoadStarted() {
            super(EVENT_TYPE);
        }
    }


    public static class ResupplyKitSiteLoadFailed extends ProfileLog {
        private static final String EVENT_TYPE = "resupply_kit_site_load_failed";

        public ResupplyKitSiteLoadFailed() {
            super(EVENT_TYPE);
        }
    }

    // Edit profile logs


    public static class EditProfileSelected extends ProfileLog {
        private static final String EVENT_TYPE = "edit_profile_selected";

        public EditProfileSelected() {
            super(EVENT_TYPE);
        }
    }


    public static class EditProfileSubmitted extends ProfileLog {
        private static final String EVENT_TYPE = "edit_profile_submitted";

        public EditProfileSubmitted() {
            super(EVENT_TYPE);
        }
    }


    public static class EditProfileConfirmed extends ProfileLog {
        private static final String EVENT_TYPE = "edit_profile_confirmed";

        public EditProfileConfirmed() {
            super(EVENT_TYPE);
        }
    }


    public static class EditProfileValidationFailure extends ProfileLog {
        private static final String EVENT_TYPE = "edit_profile_validation_failure";

        @SerializedName("error_message")
        private final String mErrorMessage;

        public EditProfileValidationFailure(final String errorMessage) {
            super(EVENT_TYPE);
            mErrorMessage = errorMessage;
        }
    }


    public static class EditProfileError extends ProfileLog {
        private static final String EVENT_TYPE = "edit_profile_error";

        @SerializedName("error_message")
        private final String mErrorMessage;

        public EditProfileError(final String errorMessage) {
            super(EVENT_TYPE);
            mErrorMessage = errorMessage;
        }
    }


    public static class ProfileShareClicked extends ProfileLog {
        private static final String EVENT_TYPE = "profile_share_clicked";

        public ProfileShareClicked() {
            super(EVENT_TYPE);
        }
    }


    public static class ProfileShareSubmitted extends ProfileLog {
        private static final String EVENT_TYPE = "profile_share_submitted";

        @SerializedName("app_name")
        private final String mAppName;
        @SerializedName("channel")
        private final String mChannel;

        public ProfileShareSubmitted(final String appName, final String channel) {
            super(EVENT_TYPE);
            mAppName = appName;
            mChannel = channel;
        }
    }
}
