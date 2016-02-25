package com.handy.portal.manager;

import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.handy.portal.constant.LocationKey;
import com.handy.portal.constant.NoShowKey;
import com.handy.portal.data.DataManager;
import com.handy.portal.event.BookingEvent;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.event.LogEvent;
import com.handy.portal.model.Booking;
import com.handy.portal.model.Booking.BookingType;
import com.handy.portal.model.BookingClaimDetails;
import com.handy.portal.model.BookingsListWrapper;
import com.handy.portal.model.BookingsWrapper;
import com.handy.portal.model.LocationData;
import com.handy.portal.model.TypeSafeMap;
import com.handy.portal.model.logs.EventLogFactory;
import com.handy.portal.util.DateTimeUtils;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

public class BookingManager
{
    private final Bus mBus;
    private EventLogFactory mEventLogFactory;
    private final DataManager mDataManager;

    private final Cache<Date, List<Booking>> availableBookingsCache;
    private final Cache<Date, List<Booking>> scheduledBookingsCache;
    private final Cache<Date, List<Booking>> complementaryBookingsCache;

    @Inject
    public BookingManager(final Bus bus, final DataManager dataManager,
                          final EventLogFactory eventLogFactory)
    {
        mBus = bus;
        mEventLogFactory = eventLogFactory;
        mBus.register(this);
        mDataManager = dataManager;

        this.availableBookingsCache = CacheBuilder.newBuilder()
                .maximumSize(100)
                .expireAfterWrite(2, TimeUnit.MINUTES)
                .build();

        this.scheduledBookingsCache = CacheBuilder.newBuilder()
                .maximumSize(100)
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
        BookingType type = event.type;

        mDataManager.getBookingDetails(bookingId, type, new DataManager.Callback<Booking>()
        {
            @Override
            public void onSuccess(Booking booking)
            {
                mBus.post(new HandyEvent.ReceiveBookingDetailsSuccess(booking));
            }

            @Override
            public void onError(DataManager.DataManagerError error)
            {
                mBus.post(new HandyEvent.ReceiveBookingDetailsError(error));
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
            if (event.useCachedIfPresent)
            {
                final List<Booking> cachedBookings = availableBookingsCache.getIfPresent(day);
                if (cachedBookings != null)
                {
                    mBus.post(new HandyEvent.ReceiveAvailableBookingsSuccess(cachedBookings, day));
                }
                else
                {
                    datesToRequest.add(day);
                }
            }
            else
            {
                datesToRequest.add(day);
            }
        }

        if (!datesToRequest.isEmpty())
        {
            mDataManager.getAvailableBookings(datesToRequest.toArray(new Date[datesToRequest.size()]),
                    new DataManager.Callback<BookingsListWrapper>()
                    {
                        @Override
                        public void onSuccess(final BookingsListWrapper bookingsListWrapper)
                        {
                            for (BookingsWrapper bookingsWrapper : bookingsListWrapper.getBookingsWrappers())
                            {
                                Date day = DateTimeUtils.getDateWithoutTime(bookingsWrapper.getDate());
                                Crashlytics.log("Received available bookings for " + day);
                                List<Booking> bookings = bookingsWrapper.getBookings();
                                availableBookingsCache.put(day, bookings);
                                mBus.post(new HandyEvent.ReceiveAvailableBookingsSuccess(bookings, day));
                            }
                        }

                        @Override
                        public void onError(final DataManager.DataManagerError error)
                        {
                            mBus.post(new HandyEvent.ReceiveAvailableBookingsError(error, datesToRequest));
                        }
                    }
            );
        }
    }

    /**
     * TODO: need to detect when pull to fresh happens, and make sure this is called again
     * unlike onRequestScheduledBookings, this will not fire events containing bookings for a specific date,
     * but rather for a batch of dates, not in any particular order. currently used by location scheduler manager
     * @param event
     */
    @Subscribe
    public void onRequestScheduledBookingsBatch(final HandyEvent.RequestScheduledBookingsBatch event)
    {
        final Map<Date, List<Booking>> resultMap = new HashMap<>();
        final List<Date> datesToRequest = new ArrayList<>();
        for (Date date : event.dates)
        {
            final Date day = DateTimeUtils.getDateWithoutTime(date);
            if (event.useCachedIfPresent)
            {
                final List<Booking> cachedBookings = scheduledBookingsCache.getIfPresent(day);
                if (cachedBookings != null)
                {
                    Log.d(getClass().getName(), "received scheduled bookings: " + day.toString());
                    resultMap.put(day, cachedBookings);
                }
                else
                {
                    datesToRequest.add(day);
                }
            }
            else
            {
                datesToRequest.add(day);
            }
        }

        if (!datesToRequest.isEmpty())
        {
            mDataManager.getScheduledBookings(datesToRequest.toArray(new Date[datesToRequest.size()]),
                    new DataManager.Callback<BookingsListWrapper>()
                    {
                        @Override
                        public void onSuccess(final BookingsListWrapper bookingsListWrapper)
                        {
                            for (BookingsWrapper bookingsWrapper : bookingsListWrapper.getBookingsWrappers())
                            {
                                Date day = DateTimeUtils.getDateWithoutTime(bookingsWrapper.getDate());
                                Log.d(getClass().getName(), "batch received scheduled bookings: " + day.toString());
                                Crashlytics.log("Received scheduled bookings for " + day);
                                List<Booking> bookings = bookingsWrapper.getBookings();
                                scheduledBookingsCache.put(day, bookings);
                                resultMap.put(day, bookings);
                            }
                            mBus.post(new HandyEvent.ReceiveScheduledBookingsBatchSuccess(resultMap));
                        }

                        @Override
                        public void onError(final DataManager.DataManagerError error)
                        {
                            mBus.post(new HandyEvent.ReceiveScheduledBookingsError(error, datesToRequest));
                        }
                    }
            );
        }
        else if(!resultMap.isEmpty())
        {
            mBus.post(new HandyEvent.ReceiveScheduledBookingsBatchSuccess(resultMap));
        }
    }

    @Subscribe
    public void onRequestScheduledBookings(final HandyEvent.RequestScheduledBookings event)
    {
        final List<Date> datesToRequest = new ArrayList<>();
        for (Date date : event.dates)
        {
            final Date day = DateTimeUtils.getDateWithoutTime(date);
            if (event.useCachedIfPresent)
            {
                final List<Booking> cachedBookings = scheduledBookingsCache.getIfPresent(day);
                if (cachedBookings != null)
                {
                    Log.d(getClass().getName(), "received scheduled bookings: " + day.toString());
                    mBus.post(new HandyEvent.ReceiveScheduledBookingsSuccess(cachedBookings, day));
                }
                else
                {
                    datesToRequest.add(day);
                }
            }
            else
            {
                datesToRequest.add(day);
            }
        }

        if (!datesToRequest.isEmpty())
        {
            mDataManager.getScheduledBookings(datesToRequest.toArray(new Date[datesToRequest.size()]),
                    new DataManager.Callback<BookingsListWrapper>()
                    {
                        @Override
                        public void onSuccess(final BookingsListWrapper bookingsListWrapper)
                        {
                            for (BookingsWrapper bookingsWrapper : bookingsListWrapper.getBookingsWrappers())
                            {
                                Date day = DateTimeUtils.getDateWithoutTime(bookingsWrapper.getDate());
                                Log.d(getClass().getName(), "received scheduled bookings: " + day.toString());
                                Crashlytics.log("Received scheduled bookings for " + day);
                                List<Booking> bookings = bookingsWrapper.getBookings();
                                scheduledBookingsCache.put(day, bookings);
                                mBus.post(new HandyEvent.ReceiveScheduledBookingsSuccess(bookings, day));
                            }

                            /*
                            this will be true when the user pulls to refresh

                            BUT it is also true onResume()! need to compensate by not firing this event on claim/cancel
                            TODO
                             */
                            if(!event.useCachedIfPresent)
                            {
                                mBus.post(new HandyEvent.BookingChangedOrCreated());
                            }
                        }

                        @Override
                        public void onError(final DataManager.DataManagerError error)
                        {
                            mBus.post(new HandyEvent.ReceiveScheduledBookingsError(error, datesToRequest));
                        }
                    }
            );
        }
    }

    @Subscribe
    public void onRequestNearbyBookings(BookingEvent.RequestNearbyBookings event)
    {
        mDataManager.getNearbyBookings(event.getRegionId(), event.getLatitude(), event.getLongitude(),
                new DataManager.Callback<BookingsWrapper>()
                {
                    @Override
                    public void onSuccess(final BookingsWrapper response)
                    {
                        mBus.post(new BookingEvent.ReceiveNearbyBookingsSuccess(response.getBookings()));
                    }

                    @Override
                    public void onError(final DataManager.DataManagerError error)
                    {
                        mBus.post(new BookingEvent.ReceiveNearbyBookingsError(error));
                    }
                });
    }

    @Subscribe
    public void onRequestComplementaryBookings(HandyEvent.RequestComplementaryBookings event)
    {
        final Date day = DateTimeUtils.getDateWithoutTime(event.date);
        final List<Booking> cachedComplementaryBookings = complementaryBookingsCache.getIfPresent(day);
        if (cachedComplementaryBookings != null)
        {
            mBus.post(new HandyEvent.ReceiveComplementaryBookingsSuccess(cachedComplementaryBookings));
        }
        else
        {
            mDataManager.getComplementaryBookings(event.bookingId, event.type, new DataManager.Callback<BookingsWrapper>()
            {
                @Override
                public void onSuccess(BookingsWrapper bookingsWrapper)
                {
                    List<Booking> bookings = bookingsWrapper.getBookings();
                    complementaryBookingsCache.put(day, bookings);
                    mBus.post(new HandyEvent.ReceiveComplementaryBookingsSuccess(bookings));
                }

                @Override
                public void onError(DataManager.DataManagerError error)
                {
                    mBus.post(new HandyEvent.ReceiveComplementaryBookingsError(error));
                }
            });
        }
    }

    @Subscribe
    public void onRequestClaimJob(final HandyEvent.RequestClaimJob event)
    {
        String bookingId = event.booking.getId();
        BookingType bookingType = event.booking.getType();
        final Date day = DateTimeUtils.getDateWithoutTime(event.booking.getStartDate());

        mDataManager.claimBooking(bookingId, bookingType, new DataManager.Callback<BookingClaimDetails>()
        {
            @Override
            public void onSuccess(BookingClaimDetails bookingClaimDetails)
            {
                invalidateCachesForDay(day);
                mBus.post(new LogEvent.AddLogEvent(
                        mEventLogFactory.createAvailableJobClaimSuccessLog(
                                bookingClaimDetails.getBooking(),
                                event.source)));
                mBus.post(new HandyEvent.ReceiveClaimJobSuccess(bookingClaimDetails, event.source));

                /*
                NOTE: not firing BookingChangedOrCreated event because immediately after this,
                the booking fragment's onResume() is called and retrieves new schedules!
                TODO: i feel uncomfortable relying on onResume() to retrieve new schedules when a booking is updated
                 */

            }

            @Override
            public void onError(DataManager.DataManagerError error)
            {
                //still need to invalidate so we don't allow them to click on same booking
                invalidateCachesForDay(day);
                mBus.post(new LogEvent.AddLogEvent(
                        mEventLogFactory.createAvailableJobClaimErrorLog(event.booking,
                                event.source)));
                mBus.post(new HandyEvent.ReceiveClaimJobError(event.booking, event.source, error));
            }
        });
    }

    @Subscribe
    public void onRequestRemoveJob(HandyEvent.RequestRemoveJob event)
    {
        String bookingId = event.booking.getId();
        BookingType bookingType = event.booking.getType();
        final Date day = DateTimeUtils.getDateWithoutTime(event.booking.getStartDate());

        mDataManager.removeBooking(bookingId, bookingType, new DataManager.Callback<Booking>()
        {
            @Override
            public void onSuccess(Booking booking)
            {
                invalidateCachesForDay(day);
                mBus.post(new HandyEvent.ReceiveRemoveJobSuccess(booking));

                /*
                NOTE: not firing BookingChangedOrCreated event because immediately after this,
                the booking fragment's onResume() is called and retrieves new schedules!
                TODO: i feel uncomfortable relying on onResume() to retrieve new schedules when a booking is updated
                 */
            }

            @Override
            public void onError(DataManager.DataManagerError error)
            {
                //still need to invalidate so we don't allow them to click on same booking
                invalidateCachesForDay(day);
                mBus.post(new HandyEvent.ReceiveRemoveJobError(error));
            }
        });
    }

    @Subscribe
    public void onRequestNotifyOnMyWay(HandyEvent.RequestNotifyJobOnMyWay event)
    {
        LocationData locationData = event.locationData;

        mDataManager.notifyOnMyWayBooking(event.bookingId, locationData.getLocationMap(), new DataManager.Callback<Booking>()
        {
            @Override
            public void onSuccess(Booking booking)
            {
                mBus.post(new HandyEvent.ReceiveNotifyJobOnMyWaySuccess(booking));
            }

            @Override
            public void onError(DataManager.DataManagerError error)
            {
                mBus.post(new HandyEvent.ReceiveNotifyJobOnMyWayError(error));
            }
        });
    }

    @Subscribe
    public void onRequestNotifyCheckIn(final HandyEvent.RequestNotifyJobCheckIn event)
    {
        LocationData locationData = event.locationData;

        mDataManager.notifyCheckInBooking(event.bookingId, event.isAuto, locationData.getLocationMap(), new DataManager.Callback<Booking>()
        {
            @Override
            public void onSuccess(Booking booking)
            {
                mBus.post(new HandyEvent.ReceiveNotifyJobCheckInSuccess(booking, event.isAuto));
            }

            @Override
            public void onError(DataManager.DataManagerError error)
            {
                //still need to invalidate so we don't allow them to click on same booking
                mBus.post(new HandyEvent.ReceiveNotifyJobCheckInError(error, event.isAuto));
            }
        });
    }

    @Subscribe
    public void onRequestNotifyCheckOut(final HandyEvent.RequestNotifyJobCheckOut event)
    {
        mDataManager.notifyCheckOutBooking(event.bookingId, event.isAuto, event.checkoutRequest, new DataManager.Callback<Booking>()
        {
            @Override
            public void onSuccess(Booking booking)
            {
                mBus.post(new HandyEvent.ReceiveNotifyJobCheckOutSuccess(booking, event.isAuto));
            }

            @Override
            public void onError(DataManager.DataManagerError error)
            {
                mBus.post(new HandyEvent.ReceiveNotifyJobCheckOutError(error, event.isAuto));
            }
        });
    }

    @Subscribe
    public void onRequestNotifyUpdateArrivalTime(HandyEvent.RequestNotifyJobUpdateArrivalTime event)
    {
        Booking.ArrivalTimeOption arrivalTimeOption = event.arrivalTimeOption;

        mDataManager.notifyUpdateArrivalTimeBooking(event.bookingId, arrivalTimeOption, new DataManager.Callback<Booking>()
        {
            @Override
            public void onSuccess(Booking booking)
            {
                mBus.post(new HandyEvent.ReceiveNotifyJobUpdateArrivalTimeSuccess(booking));
            }

            @Override
            public void onError(DataManager.DataManagerError error)
            {
                mBus.post(new HandyEvent.ReceiveNotifyJobUpdateArrivalTimeError(error));
            }
        });
    }

    @Subscribe
    public void onRequestReportNoShow(HandyEvent.RequestReportNoShow event)
    {
        mDataManager.reportNoShow(event.bookingId, getNoShowParams(true, event.locationData), new DataManager.Callback<Booking>()
        {
            @Override
            public void onSuccess(Booking booking)
            {
                mBus.post(new HandyEvent.ReceiveReportNoShowSuccess(booking));
            }

            @Override
            public void onError(DataManager.DataManagerError error)
            {
                mBus.post(new HandyEvent.ReceiveReportNoShowError(error));
            }
        });
    }

    @Subscribe
    public void onRequestCancelNoShow(HandyEvent.RequestCancelNoShow event)
    {
        mDataManager.reportNoShow(event.bookingId, getNoShowParams(false, event.locationData), new DataManager.Callback<Booking>()
        {
            @Override
            public void onSuccess(Booking booking)
            {
                mBus.post(new HandyEvent.ReceiveCancelNoShowSuccess(booking));
            }

            @Override
            public void onError(DataManager.DataManagerError error)
            {
                mBus.post(new HandyEvent.ReceiveCancelNoShowError(error));
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
        complementaryBookingsCache.invalidate(day);
    }
}
