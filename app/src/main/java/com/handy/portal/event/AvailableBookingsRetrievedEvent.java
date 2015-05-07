package com.handy.portal.event;

import com.handy.portal.core.BookingSummary;
import com.handy.portal.core.booking.BookingCalendarDay;

import java.util.List;
import java.util.Map;

/**
 * Created by cdavis on 5/6/15.
 */
public class AvailableBookingsRetrievedEvent extends Event {
    public Map<BookingCalendarDay, BookingSummary> bookingSummaries;
    public AvailableBookingsRetrievedEvent(Map<BookingCalendarDay, BookingSummary> bookingSummaries)
    {
        this.bookingSummaries = bookingSummaries;
    }

}