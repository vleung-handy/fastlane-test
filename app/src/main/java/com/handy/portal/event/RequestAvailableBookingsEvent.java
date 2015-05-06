package com.handy.portal.event;

/**
 * Created by cdavis on 5/6/15.
 */
public class RequestAvailableBookingsEvent extends Event {
    public String providerId;
    public RequestAvailableBookingsEvent(String providerId)
    {
        this.providerId = providerId;
    }
}
