package com.handy.portal.logger.handylogger;

import com.handy.portal.core.event.HandyEvent;
import com.handy.portal.logger.handylogger.model.EventLog;

public abstract class LogEvent extends HandyEvent
{
    public static class AddLogEvent extends RequestEvent
    {
        private final EventLog mLog;

        public AddLogEvent(EventLog log) {mLog = log;}

        public EventLog getLog()
        {
            return mLog;
        }
    }

    public static class SendLogsEvent extends RequestEvent {}

    public static class SaveLogsEvent extends RequestEvent {}
}
