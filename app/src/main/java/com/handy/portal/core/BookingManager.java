package com.handy.portal.core;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.handy.portal.core.booking.Booking;
import com.handy.portal.data.DataManager;
import com.handy.portal.event.HandyEvent;
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
    public void onRequestBookingDetails(HandyEvent.RequestBookingDetails event)
    {
        String bookingId = event.bookingId;

        dataManager.getBookingDetails(bookingId, new DataManager.Callback<Booking>()
        {
            @Override
            public void onSuccess(Booking booking)
            {
                bus.post(new HandyEvent.ReceiveBookingDetailsSuccess(booking));
            }

            @Override
            public void onError(DataManager.DataManagerError error)
            {
                bus.post(new HandyEvent.ReceiveBookingDetailsError(error));

                //if not a network error invalidate the caches so we don't let them try to get details on the same booking
                if(error.getType() != DataManager.DataManagerError.Type.NETWORK)
                {
                    bookingsCache.invalidate(CacheKey.AVAILABLE_BOOKINGS);
                    bookingsCache.invalidate(CacheKey.SCHEDULED_BOOKINGS);
                }
            }
        });
    }

    @Subscribe
    public void onRequestAvailableBookings(HandyEvent.RequestAvailableBookings event)
    {
        final List<BookingSummary> cachedBookingSummaries = bookingsCache.getIfPresent(CacheKey.AVAILABLE_BOOKINGS);
        if (cachedBookingSummaries != null)
        {
            bus.post(new HandyEvent.ReceiveAvailableBookingsSuccess(cachedBookingSummaries));
        }
        else
        {
            dataManager.getAvailableBookings(
                    new DataManager.Callback<BookingSummaryResponse>()
                    {
                        @Override
                        public void onSuccess(final BookingSummaryResponse bookingSummaryResponse)
                        {
                            List<BookingSummary> bookingSummaries = bookingSummaryResponse.getBookingSummaries();
                            bookingsCache.put(CacheKey.AVAILABLE_BOOKINGS, bookingSummaries);
                            bus.post(new HandyEvent.ReceiveAvailableBookingsSuccess(bookingSummaries));
                        }

                        @Override
                        public void onError(final DataManager.DataManagerError error)
                        {
                            bus.post(new HandyEvent.ReceiveAvailableBookingsError(error));
                        }
                    }
            );
        }
    }

    @Subscribe
    public void onRequestScheduledBookings(HandyEvent.RequestScheduledBookings event)
    {
        final List<BookingSummary> cachedBookingSummaries = bookingsCache.getIfPresent(CacheKey.SCHEDULED_BOOKINGS);
        if (cachedBookingSummaries != null)
        {
            bus.post(new HandyEvent.ReceiveScheduledBookingsSuccess(cachedBookingSummaries));
        }
        else
        {
            dataManager.getScheduledBookings(
                    new DataManager.Callback<BookingSummaryResponse>()
                    {
                        @Override
                        public void onSuccess(final BookingSummaryResponse bookingSummaryResponse)
                        {
                            List<BookingSummary> bookingSummaries = bookingSummaryResponse.getBookingSummaries();
                            bookingsCache.put(CacheKey.SCHEDULED_BOOKINGS, bookingSummaries);
                            bus.post(new HandyEvent.ReceiveScheduledBookingsSuccess(bookingSummaries));
                        }

                        @Override
                        public void onError(final DataManager.DataManagerError error)
                        {
                            bus.post(new HandyEvent.ReceiveScheduledBookingsError(error));
                        }
                    }
            );
        }
    }

    @Subscribe
    public void onRequestClaimJob(HandyEvent.RequestClaimJob event)
    {
        String bookingId = event.bookingId;

        dataManager.claimBooking(bookingId, new DataManager.Callback<Booking>()
        {
            @Override
            public void onSuccess(Booking booking)
            {
                bookingsCache.invalidate(CacheKey.AVAILABLE_BOOKINGS);
                bookingsCache.invalidate(CacheKey.SCHEDULED_BOOKINGS);
                bus.post(new HandyEvent.ReceiveClaimJobSuccess(booking));
            }

            @Override
            public void onError(DataManager.DataManagerError error)
            {
                //still need to invalidate so we don't allow them to click on same booking
                bookingsCache.invalidate(CacheKey.AVAILABLE_BOOKINGS);
                bookingsCache.invalidate(CacheKey.SCHEDULED_BOOKINGS);
                bus.post(new HandyEvent.ReceiveClaimJobError(error));
            }
        });
    }

    @Subscribe
    public void onRequestRemoveJob(HandyEvent.RequestRemoveJob event)
    {
        String bookingId = event.bookingId;

        dataManager.removeBooking(bookingId, new DataManager.Callback<Booking>()
        {
            @Override
            public void onSuccess(Booking booking)
            {
                bookingsCache.invalidate(CacheKey.AVAILABLE_BOOKINGS);
                bookingsCache.invalidate(CacheKey.SCHEDULED_BOOKINGS);
                bus.post(new HandyEvent.ReceiveRemoveJobSuccess(booking));
            }

            @Override
            public void onError(DataManager.DataManagerError error)
            {
                //still need to invalidate so we don't allow them to click on same booking
                bookingsCache.invalidate(CacheKey.AVAILABLE_BOOKINGS);
                bookingsCache.invalidate(CacheKey.SCHEDULED_BOOKINGS);
                bus.post(new HandyEvent.ReceiveRemoveJobError(error));
            }
        });
    }

    @Subscribe
    public void onRequestNotifyOnMyWay(HandyEvent.RequestNotifyJobOnMyWay event)
    {
        String bookingId = event.bookingId;
        LocationData locationData = event.locationData;

        dataManager.notifyOnMyWayBooking(bookingId, locationData.getLocationParamsMap(), new DataManager.Callback<Booking>()
        {
            @Override
            public void onSuccess(Booking booking)
            {
                bus.post(new HandyEvent.ReceiveNotifyJobOnMyWaySuccess(booking));
            }

            @Override
            public void onError(DataManager.DataManagerError error)
            {
                bus.post(new HandyEvent.ReceiveNotifyJobOnMyWayError(error));
            }
        });
    }

    @Subscribe
    public void onRequestNotifyCheckIn(HandyEvent.RequestNotifyJobCheckIn event)
    {
        String bookingId = event.bookingId;
        LocationData locationData = event.locationData;

        dataManager.notifyCheckInBooking(bookingId, locationData.getLocationParamsMap(), new DataManager.Callback<Booking>()
        {
            @Override
            public void onSuccess(Booking booking)
            {
                bus.post(new HandyEvent.ReceiveNotifyJobCheckInSuccess(booking));
            }

            @Override
            public void onError(DataManager.DataManagerError error)
            {
                //still need to invalidate so we don't allow them to click on same booking
                bus.post(new HandyEvent.ReceiveNotifyJobCheckInError(error));
            }
        });
    }

    @Subscribe
    public void onRequestNotifyCheckOut(HandyEvent.RequestNotifyJobCheckOut event)
    {
        String bookingId = event.bookingId;
        LocationData locationData = event.locationData;

        dataManager.notifyCheckOutBooking(bookingId, locationData.getLocationParamsMap(), new DataManager.Callback<Booking>()
        {
            @Override
            public void onSuccess(Booking booking)
            {
                bus.post(new HandyEvent.ReceiveNotifyJobCheckoutSuccess(booking));
            }

            @Override
            public void onError(DataManager.DataManagerError error)
            {
                bus.post(new HandyEvent.ReceiveNotifyJobCheckoutError(error));
            }
        });
    }

    @Subscribe
    public void onRequestNotifyUpdateArrivalTime(HandyEvent.RequestNotifyJobUpdateArrivalTime event)
    {
        String bookingId = event.bookingId;
        Booking.ArrivalTimeOption arrivalTimeOption = event.arrivalTimeOption;

        dataManager.notifyUpdateArrivalTimeBooking(bookingId, arrivalTimeOption, new DataManager.Callback<Booking>()
        {
            @Override
            public void onSuccess(Booking booking)
            {
                bus.post(new HandyEvent.ReceiveNotifyJobUpdateArrivalTimeSuccess(booking));
            }

            @Override
            public void onError(DataManager.DataManagerError error)
            {
                bus.post(new HandyEvent.ReceiveNotifyJobUpdateArrivalTimeError(error));
            }
        });
    }

}
