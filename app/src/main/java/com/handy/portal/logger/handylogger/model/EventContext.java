package com.handy.portal.logger.handylogger.model;

public interface EventContext {
    String UNKNOWN = "unknown";
    String DEEPLINK = "deeplink";
    String JOB_DETAILS = "job_details";
    String AVAILABILITY = "availability";
    String SEND_AVAILABILITY = "send_availability";
    String AVAILABLE_JOBS = "available_jobs";
    String REQUESTED_JOBS = "requested_jobs";
    String SCHEDULED_JOBS = "scheduled_jobs";
    String CHECKOUT_FLOW = "checkout_flow";
}
