package com.handy.portal.event;

/**
 * Created by cdavis on 5/6/15.
 */
public class RequestScheduledBookingsEvent extends Event {
    public String providerId;
    public RequestScheduledBookingsEvent(String providerId)
    {
        this.providerId = providerId;
    }
}
