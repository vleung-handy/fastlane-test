package com.handy.portal.core;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.handy.portal.core.booking.Booking;
import com.handy.portal.data.DataManager;
import com.handy.portal.event.Event;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

public class BookingManager
{
    private final Bus bus;
    private final DataManager dataManager;

    // will change type when we want access to bookings for a specific day, right now, we're just dumping all
    private final Cache<CacheKey, List<BookingSummary>> bookingsCache;

    private enum CacheKey {
        AVAILABLE_BOOKINGS,
        SCHEDULED_BOOKINGS
    }

    @Inject
    BookingManager(final Bus bus, final DataManager dataManager)
    {
        this.bus = bus;
        this.bus.register(this);
        this.dataManager = dataManager;

        this.bookingsCache = CacheBuilder.newBuilder()
                .weakKeys()
                .maximumSize(10000)
                .expireAfterWrite(1, TimeUnit.MINUTES)
                .build();
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
        final List<BookingSummary> cachedBookingSummaries = bookingsCache.getIfPresent(CacheKey.AVAILABLE_BOOKINGS);
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
                            bookingsCache.put(CacheKey.AVAILABLE_BOOKINGS, bookingSummaries);
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
                        bookingsCache.put(CacheKey.SCHEDULED_BOOKINGS, bookingSummaries);
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
                bookingsCache.invalidate(CacheKey.AVAILABLE_BOOKINGS);
                bookingsCache.invalidate(CacheKey.SCHEDULED_BOOKINGS);
                bus.post(new Event.ClaimJobRequestReceivedEvent(booking, true));
            }

            @Override
            public void onError(DataManager.DataManagerError error)
            {
                //still need to invalidate so we don't allow them to click on same booking
                bookingsCache.invalidate(CacheKey.AVAILABLE_BOOKINGS);
                bookingsCache.invalidate(CacheKey.SCHEDULED_BOOKINGS);
                bus.post(new Event.ClaimJobRequestReceivedEvent(null, false, error.getMessage()));
            }
        });
    }

}
