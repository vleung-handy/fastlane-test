package com.handy.portal.logger.handylogger.model;

public class ProfilePhotoLog extends EventLog {
    private static final String EVENT_CONTEXT = "profile_photo";

    public ProfilePhotoLog(final String eventType) {
        super(eventType, EVENT_CONTEXT);
    }

    public static class CameraTapped extends ProfilePhotoLog {
        private static final String EVENT_TYPE = "camera_tapped";

        public CameraTapped() {
            super(EVENT_TYPE);
        }
    }


    public static class CameraError extends ProfilePhotoLog {
        private static final String EVENT_TYPE = "camera_error";

        public CameraError() {
            super(EVENT_TYPE);
        }
    }


    public static class PhotoLibraryTapped extends ProfilePhotoLog {
        private static final String EVENT_TYPE = "photo_library_tapped";

        public PhotoLibraryTapped() {
            super(EVENT_TYPE);
        }
    }


    public static class PhotoLibraryError extends ProfilePhotoLog {
        private static final String EVENT_TYPE = "photo_library_error";

        public PhotoLibraryError() {
            super(EVENT_TYPE);
        }
    }


    public static class ImageChosen extends ProfilePhotoLog {
        private static final String EVENT_TYPE = "image_chosen";

        public ImageChosen() {
            super(EVENT_TYPE);
        }
    }


    public static class ImagePickerDismissed extends ProfilePhotoLog {
        private static final String EVENT_TYPE = "image_picker_dismissed";

        public ImagePickerDismissed() {
            super(EVENT_TYPE);
        }
    }
}
