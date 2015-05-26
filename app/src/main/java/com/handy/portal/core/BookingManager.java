package com.handy.portal.core;

import com.handy.portal.core.booking.Booking;
import com.handy.portal.core.booking.BookingCalendarDay;
import com.handy.portal.data.DataManager;
import com.handy.portal.data.SecurePreferences;
import com.handy.portal.event.Event;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

public final class BookingManager
{
    private BookingRequest request;
    private BookingQuote quote;
    private BookingTransaction transaction;
    private BookingPostInfo postInfo;
    private final SecurePreferences securePrefs;
    private final Bus bus;
    private final DataManager dataManager;

    //we are always providing in the context of our current user

    private Map<String, Booking> cachedBookings; //booking ID to booking

    //Do we really need to cache booking summaries? We're dealing with relatively small data sets
    //Maybe the booking summaries will contain a list of ids instead of the full data
    //and we keep our cached booking data distinct from the by day summaries which is a convenience
    private Map<BookingCalendarDay, BookingSummary> cachedBookingSummaries;

    @Inject
    BookingManager(final Bus bus, final SecurePreferences prefs, final DataManager dataManager)
    {
        this.securePrefs = prefs;
        this.bus = bus;
        this.bus.register(this);
        this.dataManager = dataManager;

        this.cachedBookings = new HashMap<String, Booking>();
        this.cachedBookingSummaries = new HashMap<BookingCalendarDay, BookingSummary>();
    }

    private void updateBookingsCache(Booking booking)
    {
        cachedBookings.put(booking.getId(), booking);
    }

    //all communication will be done through the bus
    //booking manager
    //requests and caches data about bookings
    //responds to requests for data about bookings or lists of bookings
    //listens and responds to requests to claim / cancel

    @Subscribe
    public void onRequestBookingDetails(Event.RequestBookingDetailsEvent event)
    {
        String bookingId = event.bookingId;

        dataManager.getBookingDetails(bookingId, new DataManager.Callback<Booking>()
        {
            @Override
            public void onSuccess(Booking booking)
            {
                onBookingDetailsReceived(booking);
            }

            @Override
            public void onError(DataManager.DataManagerError error)
            {
                System.err.println("Failed to get booking details " + error);
                //TODO: Throw a failed event
            }
        });
    }

    private void onBookingDetailsReceived(Booking booking)
    {
        bus.post(new Event.BookingsDetailsRetrievedEvent(booking));
    }


    @Subscribe
    public void onRequestAvailableBookings(Event.RequestAvailableBookingsEvent event)
    {
        dataManager.getAvailableBookings(new DataManager.Callback<List<BookingSummary>>()
                                         {
                                             @Override
                                             public void onSuccess(final List<BookingSummary> bookingSummaries)
                                             {
                                                 onBookingSummariesReceived(bookingSummaries);
                                             }

                                             @Override
                                             public void onError(final DataManager.DataManagerError error)
                                             {
                                                 System.err.println("Failed to get available bookings " + error);
                                                 //TODO: Throw a failed event
                                             }
                                         }
        );
    }

    @Subscribe
    public void onRequestScheduledBookings(Event.RequestScheduledBookingsEvent event)
    {
        dataManager.getScheduledBookings(new DataManager.Callback<List<BookingSummary>>()
                                         {
                                             @Override
                                             public void onSuccess(final List<BookingSummary> bookingSummaries)
                                             {
                                                 onBookingSummariesReceived(bookingSummaries);
                                             }

                                             @Override
                                             public void onError(final DataManager.DataManagerError error)
                                             {
                                                 System.err.println("Failed to get scheduled bookings " + error);
                                                 //TODO: Throw a failed event
                                             }
                                         }
        );
    }

    @Subscribe
    public void onRequestClaimJob(Event.RequestClaimJobEvent event)
    {
        String bookingId = event.bookingId;

        dataManager.claimBooking(bookingId, new DataManager.Callback<Booking>()
        {
            @Override
            public void onSuccess(Booking booking)
            {
                onClaimBookingsReceived(booking);
            }

            @Override
            public void onError(DataManager.DataManagerError error)
            {
                System.err.println("Failed to get claim booking response " + error);
                bus.post(new Event.ClaimJobRequestReceivedEvent(null, false));
            }
        });
    }

    private void onClaimBookingsReceived(Booking booking)
    {
        updateBookingsCache(booking);

        //just passing this through right now until we figure out our caching strategy
        bus.post(new Event.ClaimJobRequestReceivedEvent(booking, true));
    }


    public void onBookingSummariesReceived(final List<BookingSummary> bookingSummaries)
    {
        if (bookingSummaries == null)
        {
            System.err.println("No booking summaries from server");
            return;
        }

        //extract all of the bookings and update our local cache
        for (BookingSummary bs : bookingSummaries)
        {
            for (Booking b : bs.getBookings())
            {
                updateBookingsCache(b);
            }
        }

        //update the summaries cache
        updateSummariesCache(bookingSummaries);

        //just passing this through right now until we figure out our caching strategy
        bus.post(new Event.BookingsRetrievedEvent(cachedBookingSummaries));
    }

    //may get rid of the caching of summaries and just keep raw booking data?
    private void updateSummariesCache(final List<BookingSummary> bookingSummaries)
    {
        cachedBookingSummaries = new HashMap<BookingCalendarDay, BookingSummary>();
        for (BookingSummary bs : bookingSummaries)
        {
            BookingCalendarDay bcd = new BookingCalendarDay(bs.getDate());
            cachedBookingSummaries.put(bcd, bs);
        }
    }
}
