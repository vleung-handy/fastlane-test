package com.handy.portal.logger.handylogger.model;

public class MenuDrawerLog extends EventLog {
    private static final String EVENT_CONTEXT = "more";

    private MenuDrawerLog(String eventType) {
        super(eventType, EVENT_CONTEXT);
    }

    public static class PaymentsMenuItemSelected extends MenuDrawerLog
    {
        private static final String EVENT_TYPE = "payments_tapped";

        public PaymentsMenuItemSelected()
        {
            super(EVENT_TYPE);
        }
    }
}
