package com.handy.portal.model.logs;


public class VideoLog extends EventLog
{
    public VideoLog(final String eventType, final String eventContext)
    {
        super(eventType, eventContext);
    }

    public static class VideoClickedLog extends VideoLog
    {
        private static final String EVENT_TYPE = "video_clicked";

        public VideoClickedLog(String eventContext)
        {
            super(EVENT_TYPE, eventContext);
        }
    }
}
