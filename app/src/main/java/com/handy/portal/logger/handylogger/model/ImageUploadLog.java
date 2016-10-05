package com.handy.portal.logger.handylogger.model;

public class ImageUploadLog extends EventLog
{
    private static final String EVENT_CONTEXT = "image_upload";

    public ImageUploadLog(final String eventType)
    {
        super(eventType, EVENT_CONTEXT);
    }

    public static class MetadataRequestSubmitted extends ImageUploadLog
    {
        private static final String EVENT_TYPE = "metadata_request_submitted";

        public MetadataRequestSubmitted()
        {
            super(EVENT_TYPE);
        }
    }


    public static class MetadataRequestSuccess extends ImageUploadLog
    {
        private static final String EVENT_TYPE = "metadata_request_success";

        public MetadataRequestSuccess()
        {
            super(EVENT_TYPE);
        }
    }


    public static class MetadataRequestError extends ImageUploadLog
    {
        private static final String EVENT_TYPE = "metadata_request_error";

        public MetadataRequestError()
        {
            super(EVENT_TYPE);
        }
    }


    public static class ImageRequestSubmitted extends ImageUploadLog
    {
        private static final String EVENT_TYPE = "image_request_submitted";

        public ImageRequestSubmitted()
        {
            super(EVENT_TYPE);
        }
    }


    public static class ImageRequestSuccess extends ImageUploadLog
    {
        private static final String EVENT_TYPE = "image_request_success";

        public ImageRequestSuccess()
        {
            super(EVENT_TYPE);
        }
    }


    public static class ImageRequestError extends ImageUploadLog
    {
        private static final String EVENT_TYPE = "image_request_error";

        public ImageRequestError()
        {
            super(EVENT_TYPE);
        }
    }
}
