package com.handy.portal.model.logs;


import com.handy.portal.logger.handylogger.model.EventLog;

public class VideoLog extends EventLog
{
    private static final String EVENT_CONTEXT = "feedback";

    public VideoLog(final String eventType)
    {
        super(eventType, EVENT_CONTEXT);
    }

    public static class VideoTappedLog extends VideoLog
    {
        private static final String EVENT_TYPE = "video_tapped";
        private String mSection;

        public VideoTappedLog(String section)
        {
            super(EVENT_TYPE);
            mSection = section;
        }
    }


    public static class VideoLibraryTappedLog extends VideoLog
    {
        private static final String EVENT_TYPE = "video_library_tapped";

        public VideoLibraryTappedLog()
        {
            super(EVENT_TYPE);
        }
    }
}
