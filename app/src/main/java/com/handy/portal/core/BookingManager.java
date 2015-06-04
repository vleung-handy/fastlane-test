package com.handy.portal.core;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.handy.portal.core.booking.Booking;
import com.handy.portal.data.DataManager;
import com.handy.portal.event.Event;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

public class BookingManager
{
    private final Bus bus;
    private final DataManager dataManager;

    //we are always providing in the context of our current user

    private Map<String, Booking> cachedBookings; //booking ID to booking

    // will change type when we want access to bookings for a specific day, right now, we're just dumping all
    private final Cache<String, List<BookingSummary>> bookingSummariesCache;
    private static final String AVAILABLE_BOOKINGS_CACHE_KEY = "available_bookings";
    private static final String SCHEDULED_BOOKINGS_CACHE_KEY = "scheduled_summaries";

    @Inject
    BookingManager(final Bus bus, final DataManager dataManager)
    {
        this.bus = bus;
        this.bus.register(this);
        this.dataManager = dataManager;

        this.cachedBookings = new HashMap<String, Booking>();

        this.bookingSummariesCache = CacheBuilder.newBuilder()
                .maximumSize(10000)
                .expireAfterWrite(1, TimeUnit.MINUTES)
                .build();
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
                bus.post(new Event.BookingsDetailsRetrievedEvent(booking, true));
            }

            @Override
            public void onError(DataManager.DataManagerError error)
            {
                System.err.println("Failed to get booking details " + error);
                bus.post(new Event.BookingsDetailsRetrievedEvent(null, false));
            }
        });
    }

    @Subscribe
    public void onRequestAvailableBookings(Event.RequestAvailableBookingsEvent event)
    {
        final List<BookingSummary> cachedBookingSummaries = bookingSummariesCache.getIfPresent(AVAILABLE_BOOKINGS_CACHE_KEY);
        if (cachedBookingSummaries != null)
        {
            bus.post(new Event.BookingsRetrievedEvent(cachedBookingSummaries, true));
        }
        else
        {
            dataManager.getAvailableBookings(
                    new DataManager.Callback<List<BookingSummary>>()
                    {
                        @Override
                        public void onSuccess(final List<BookingSummary> bookingSummaries)
                        {
                            bookingSummariesCache.put(AVAILABLE_BOOKINGS_CACHE_KEY, bookingSummaries);
                            bus.post(new Event.BookingsRetrievedEvent(bookingSummaries, true));
                        }

                        @Override
                        public void onError(final DataManager.DataManagerError error)
                        {
                            System.err.println("Failed to get available bookings " + error);
                            bus.post(new Event.BookingsRetrievedEvent(null, false));
                        }
                    }
            );
        }
    }

    @Subscribe
    public void onRequestScheduledBookings(Event.RequestScheduledBookingsEvent event)
    {
        dataManager.getScheduledBookings(
                new DataManager.Callback<List<BookingSummary>>()
                {
                    @Override
                    public void onSuccess(final List<BookingSummary> bookingSummaries)
                    {
                        bookingSummariesCache.put(SCHEDULED_BOOKINGS_CACHE_KEY, bookingSummaries);
                        bus.post(new Event.BookingsRetrievedEvent(bookingSummaries, true));
                    }

                    @Override
                    public void onError(final DataManager.DataManagerError error)
                    {
                        System.err.println("Failed to get scheduled bookings " + error);
                        bus.post(new Event.BookingsRetrievedEvent(null, false));
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

}
