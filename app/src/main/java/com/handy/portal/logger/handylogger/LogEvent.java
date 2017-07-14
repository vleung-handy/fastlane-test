package com.handy.portal.logger.handylogger;

import com.handy.portal.core.event.HandyEvent;

public abstract class LogEvent extends HandyEvent {

    public static class SendLogsEvent extends RequestEvent {}


    public static class SaveLogsEvent extends RequestEvent {}
}
