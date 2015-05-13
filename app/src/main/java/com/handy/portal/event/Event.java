package com.handy.portal.event;

import android.os.Bundle;

import com.handy.portal.core.BookingSummary;
import com.handy.portal.core.booking.Booking;
import com.handy.portal.core.booking.BookingCalendarDay;
import com.handy.portal.ui.fragment.MainActivityFragment;

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

    public static class BookingsDetailsRetrievedEvent extends Event
    {
        public Booking booking;

        public BookingsDetailsRetrievedEvent(Booking booking)
        {
            this.booking = booking;
        }
    }

    public static class NavigateToTabEvent extends Event
    {
        public MainActivityFragment.MainViewTab targetTab;
        public Bundle arguments;

        public NavigateToTabEvent(MainActivityFragment.MainViewTab targetTab)
        {
            this.targetTab = targetTab;
        }

        public NavigateToTabEvent(MainActivityFragment.MainViewTab targetTab, Bundle arguments)
        {
            this.targetTab = targetTab;
            this.arguments = arguments;
        }

    }

}


