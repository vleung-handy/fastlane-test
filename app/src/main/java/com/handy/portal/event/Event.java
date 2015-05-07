package com.handy.portal.event;

import com.handy.portal.core.BookingSummary;
import com.handy.portal.core.booking.BookingCalendarDay;

import java.util.List;
import java.util.Map;

/**
 * Created by cdavis on 5/6/15.
 */
public class Event
{
    public static class BookingsRetrievedEvent extends Event
    {
        public Map<BookingCalendarDay, BookingSummary> bookingSummaries;

        public BookingsRetrievedEvent(Map<BookingCalendarDay, BookingSummary> bookingSummaries)
        {
            this.bookingSummaries = bookingSummaries;
        }
    }

    public static class RequestAvailableBookingsEvent extends Event
    {
    }

    public static class RequestScheduledBookingsEvent extends Event
    {
    }

    public static class RequestBookingDetailsEvent extends Event
    {
        public String bookingId;

        public RequestBookingDetailsEvent(String bookingId)
        {
            this.bookingId = bookingId;
        }
    }


}


