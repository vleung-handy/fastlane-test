package com.handy.portal.manager;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.handy.portal.constant.LocationKey;
import com.handy.portal.constant.NoShowKey;
import com.handy.portal.data.DataManager;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.model.Booking;
import com.handy.portal.model.BookingsListWrapper;
import com.handy.portal.model.BookingsWrapper;
import com.handy.portal.model.LocationData;
import com.handy.portal.model.TypeSafeMap;
import com.handy.portal.util.DateTimeUtils;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

public class BookingManager
{
    private final Bus bus;
    private final DataManager dataManager;

    private final Cache<Date, List<Booking>> availableBookingsCache;
    private final Cache<Date, List<Booking>> scheduledBookingsCache;
    private final Cache<Date, List<Booking>> complementaryBookingsCache;

    @Inject
    public BookingManager(final Bus bus, final DataManager dataManager)
    {
        this.bus = bus;
        this.bus.register(this);
        this.dataManager = dataManager;

        this.availableBookingsCache = CacheBuilder.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(2, TimeUnit.MINUTES)
                .build();

        this.scheduledBookingsCache = CacheBuilder.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(2, TimeUnit.MINUTES)
                .build();

        this.complementaryBookingsCache = CacheBuilder.newBuilder()
                .maximumSize(100)
                .expireAfterWrite(2, TimeUnit.MINUTES)
                .build();
    }

    //all communication will be done through the bus
    //booking manager
    //requests and caches data about bookings
    //responds to requests for data about bookings or lists of bookings
    //listens and responds to requests to claim / cancel

    @Subscribe
    public void onRequestBookingDetails(final HandyEvent.RequestBookingDetails event)
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
                if (event.date != null && error.getType() != DataManager.DataManagerError.Type.NETWORK)
                {
                    Date day = DateTimeUtils.getDateWithoutTime(event.date);
                    invalidateCachesForDay(day);
                }
            }
        });
    }

    @Subscribe
    public void onRequestAvailableBookings(final HandyEvent.RequestAvailableBookings event)
    {
        final List<Date> datesToRequest = new ArrayList<>();
        for (Date date : event.dates)
        {
            final Date day = DateTimeUtils.getDateWithoutTime(date);
            final List<Booking> cachedBookings = availableBookingsCache.getIfPresent(day);
            if (cachedBookings != null)
            {
                bus.post(new HandyEvent.ReceiveAvailableBookingsSuccess(cachedBookings, day));
            }
            else
            {
                datesToRequest.add(day);
            }
        }

        if (!datesToRequest.isEmpty())
        {
            dataManager.getAvailableBookings(datesToRequest.toArray(new Date[datesToRequest.size()]),
                    new DataManager.Callback<BookingsListWrapper>()
                    {
                        @Override
                        public void onSuccess(final BookingsListWrapper bookingsListWrapper)
                        {
                            for (BookingsWrapper bookingsWrapper : bookingsListWrapper.getBookingsWrappers())
                            {
                                Date day = DateTimeUtils.getDateWithoutTime(bookingsWrapper.getDate());
                                List<Booking> bookings = bookingsWrapper.getBookings();
                                availableBookingsCache.put(day, bookings);
                                bus.post(new HandyEvent.ReceiveAvailableBookingsSuccess(bookings, day));
                            }
                        }

                        @Override
                        public void onError(final DataManager.DataManagerError error)
                        {
                            bus.post(new HandyEvent.ReceiveAvailableBookingsError(error, datesToRequest));
                        }
                    }
            );
        }
    }

    @Subscribe
    public void onRequestScheduledBookings(HandyEvent.RequestScheduledBookings event)
    {
        final List<Date> datesToRequest = new ArrayList<>();
        for (Date date : event.dates)
        {
            final Date day = DateTimeUtils.getDateWithoutTime(date);
            final List<Booking> cachedBookings = scheduledBookingsCache.getIfPresent(day);
            if (cachedBookings != null)
            {
                bus.post(new HandyEvent.ReceiveScheduledBookingsSuccess(cachedBookings, day));
            }
            else
            {
                datesToRequest.add(day);
            }
        }

        if (!datesToRequest.isEmpty())
        {
            dataManager.getScheduledBookings(datesToRequest.toArray(new Date[datesToRequest.size()]),
                    new DataManager.Callback<BookingsListWrapper>()
                    {
                        @Override
                        public void onSuccess(final BookingsListWrapper bookingsListWrapper)
                        {
                            for (BookingsWrapper bookingsWrapper : bookingsListWrapper.getBookingsWrappers())
                            {
                                Date day = DateTimeUtils.getDateWithoutTime(bookingsWrapper.getDate());
                                List<Booking> bookings = bookingsWrapper.getBookings();
                                scheduledBookingsCache.put(day, bookings);
                                bus.post(new HandyEvent.ReceiveScheduledBookingsSuccess(bookings, day));
                            }
                        }

                        @Override
                        public void onError(final DataManager.DataManagerError error)
                        {
                            bus.post(new HandyEvent.ReceiveScheduledBookingsError(error, datesToRequest));
                        }
                    }
            );
        }
    }

    @Subscribe
    public void onRequestComplementaryBookings(HandyEvent.RequestComplementaryBookings event)
    {
        final Date day = DateTimeUtils.getDateWithoutTime(event.booking.getStartDate());
        List<Booking> cachedComplementaryBookings = complementaryBookingsCache.getIfPresent(day);
        if (cachedComplementaryBookings != null)
        {
            bus.post(new HandyEvent.ReceiveComplementaryBookingsSuccess(cachedComplementaryBookings));
        }
        else
        {
            dataManager.getComplementaryBookings(event.bookingId, new DataManager.Callback<BookingsWrapper>()
            {
                @Override
                public void onSuccess(BookingsWrapper bookingsWrapper)
                {
                    List<Booking> bookings = bookingsWrapper.getBookings();
                    bus.post(new HandyEvent.ReceiveComplementaryBookingsSuccess(bookings));
                }

                @Override
                public void onError(DataManager.DataManagerError error)
                {
                    bus.post(new HandyEvent.ReceiveComplementaryBookingsError());
                }
            });
        }
    }

    @Subscribe
    public void onRequestClaimJob(HandyEvent.RequestClaimJob event)
    {
        String bookingId = event.booking.getId();
        final Date day = DateTimeUtils.getDateWithoutTime(event.booking.getStartDate());

        dataManager.claimBooking(bookingId, new DataManager.Callback<Booking>()
        {
            @Override
            public void onSuccess(Booking booking)
            {
                invalidateCachesForDay(day);
                bus.post(new HandyEvent.ReceiveClaimJobSuccess(booking));
            }

            @Override
            public void onError(DataManager.DataManagerError error)
            {
                //still need to invalidate so we don't allow them to click on same booking
                invalidateCachesForDay(day);
                bus.post(new HandyEvent.ReceiveClaimJobError(error));
            }
        });
    }

    @Subscribe
    public void onRequestRemoveJob(HandyEvent.RequestRemoveJob event)
    {
        String bookingId = event.booking.getId();
        final Date day = DateTimeUtils.getDateWithoutTime(event.booking.getStartDate());

        dataManager.removeBooking(bookingId, new DataManager.Callback<Booking>()
        {
            @Override
            public void onSuccess(Booking booking)
            {
                invalidateCachesForDay(day);
                bus.post(new HandyEvent.ReceiveRemoveJobSuccess(booking));
            }

            @Override
            public void onError(DataManager.DataManagerError error)
            {
                //still need to invalidate so we don't allow them to click on same booking
                invalidateCachesForDay(day);
                bus.post(new HandyEvent.ReceiveRemoveJobError(error));
            }
        });
    }

    @Subscribe
    public void onRequestNotifyOnMyWay(HandyEvent.RequestNotifyJobOnMyWay event)
    {
        LocationData locationData = event.locationData;

        dataManager.notifyOnMyWayBooking(event.bookingId, locationData.getLocationMap(), new DataManager.Callback<Booking>()
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
        LocationData locationData = event.locationData;

        dataManager.notifyCheckInBooking(event.bookingId, locationData.getLocationMap(), new DataManager.Callback<Booking>()
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
        LocationData locationData = event.locationData;

        dataManager.notifyCheckOutBooking(event.bookingId, locationData.getLocationMap(), new DataManager.Callback<Booking>()
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
        Booking.ArrivalTimeOption arrivalTimeOption = event.arrivalTimeOption;

        dataManager.notifyUpdateArrivalTimeBooking(event.bookingId, arrivalTimeOption, new DataManager.Callback<Booking>()
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

    @Subscribe
    public void onRequestReportNoShow(HandyEvent.RequestReportNoShow event)
    {
        dataManager.reportNoShow(event.bookingId, getNoShowParams(true, event.locationData), new DataManager.Callback<Booking>()
        {
            @Override
            public void onSuccess(Booking booking)
            {
                bus.post(new HandyEvent.ReceiveReportNoShowSuccess(booking));
            }

            @Override
            public void onError(DataManager.DataManagerError error)
            {
                bus.post(new HandyEvent.ReceiveReportNoShowError(error));
            }
        });
    }

    @Subscribe
    public void onRequestCancelNoShow(HandyEvent.RequestCancelNoShow event)
    {
        dataManager.reportNoShow(event.bookingId, getNoShowParams(false, event.locationData), new DataManager.Callback<Booking>()
        {
            @Override
            public void onSuccess(Booking booking)
            {
                bus.post(new HandyEvent.ReceiveCancelNoShowSuccess(booking));
            }

            @Override
            public void onError(DataManager.DataManagerError error)
            {
                bus.post(new HandyEvent.ReceiveCancelNoShowError(error));
            }
        });
    }

    private TypeSafeMap<NoShowKey> getNoShowParams(boolean active, LocationData locationData)
    {
        TypeSafeMap<NoShowKey> noShowParams = new TypeSafeMap<>();
        TypeSafeMap<LocationKey> locationParamsMap = locationData.getLocationMap();
        noShowParams.put(NoShowKey.LATITUDE, locationParamsMap.get(LocationKey.LATITUDE));
        noShowParams.put(NoShowKey.LONGITUDE, locationParamsMap.get(LocationKey.LONGITUDE));
        noShowParams.put(NoShowKey.ACCURACY, locationParamsMap.get(LocationKey.ACCURACY));
        noShowParams.put(NoShowKey.ACTIVE, Boolean.toString(active));
        return noShowParams;
    }

    private void invalidateCachesForDay(Date day)
    {
        availableBookingsCache.invalidate(day);
        scheduledBookingsCache.invalidate(day);
    }
}
