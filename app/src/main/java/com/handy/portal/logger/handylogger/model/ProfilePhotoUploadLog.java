package com.handy.portal.logger.handylogger.model;

import com.handy.portal.core.ui.fragment.EditPhotoFragment;

public class ProfilePhotoUploadLog extends EventLog {
    public ProfilePhotoUploadLog(final String eventType, final EditPhotoFragment.Source source) {
        super(eventType, source.name().toLowerCase());
    }

    public static class ProfilePhotoUploadSubmitted extends ProfilePhotoUploadLog {
        private static final String EVENT_TYPE = "profile_photo_upload_submitted";

        public ProfilePhotoUploadSubmitted(final EditPhotoFragment.Source source) {
            super(EVENT_TYPE, source);
        }
    }


    public static class ProfilePhotoUploadSuccess extends ProfilePhotoUploadLog {
        private static final String EVENT_TYPE = "profile_photo_upload_success";

        public ProfilePhotoUploadSuccess(final EditPhotoFragment.Source source) {
            super(EVENT_TYPE, source);
        }
    }


    public static class ProfilePhotoUploadError extends ProfilePhotoUploadLog {
        private static final String EVENT_TYPE = "profile_photo_upload_error";

        public ProfilePhotoUploadError(final EditPhotoFragment.Source source) {
            super(EVENT_TYPE, source);
        }
    }
}
