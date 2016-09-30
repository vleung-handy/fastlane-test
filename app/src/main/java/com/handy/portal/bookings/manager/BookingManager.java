package com.handy.portal.bookings.manager;

import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.handy.portal.bookings.BookingEvent;
import com.handy.portal.bookings.model.Booking;
import com.handy.portal.bookings.model.Booking.BookingType;
import com.handy.portal.bookings.model.BookingClaimDetails;
import com.handy.portal.bookings.model.BookingsListWrapper;
import com.handy.portal.bookings.model.BookingsWrapper;
import com.handy.portal.bookings.model.PostCheckoutInfo;
import com.handy.portal.bookings.model.ScheduledBookingFindJob;
import com.handy.portal.constant.LocationKey;
import com.handy.portal.constant.ProviderKey;
import com.handy.portal.data.DataManager;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.library.util.DateTimeUtils;
import com.handy.portal.model.LocationData;
import com.handy.portal.model.TypeSafeMap;
import com.handy.portal.onboarding.model.claim.JobClaimResponse;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

public class BookingManager
{
    public static final int REQUESTED_JOBS_NUM_DAYS_IN_ADVANCE = 14; // TODO: Make this a config param
    public static final String JOBS_COUNT_KEY = "count";

    private final EventBus mBus;
    private final DataManager mDataManager;

    private final Cache<Date, BookingsWrapper> availableBookingsCache;
    private final Cache<Date, BookingsWrapper> scheduledBookingsCache;
    private final Cache<Date, BookingsWrapper> complementaryBookingsCache;
    private final Cache<Date, BookingsWrapper> requestedBookingsCache;


    /*
    keys used in QueryMap requests
     */
    private final class BookingRequestKeys
    {
        private static final String IS_PROVIDER_REQUESTED = "is_requested";
    }

    @Inject
    public BookingManager(final EventBus bus, final DataManager dataManager)
    {
        mBus = bus;
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

        this.requestedBookingsCache = CacheBuilder.newBuilder()
                .maximumSize(100)
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .build();
    }

    public void clearCache()
    {
        availableBookingsCache.invalidateAll();
        scheduledBookingsCache.invalidateAll();
        complementaryBookingsCache.invalidateAll();
        requestedBookingsCache.invalidateAll();
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
    public void onRequestAvailableBookingsFiltered(final HandyEvent.RequestAvailableBookingsFiltered event) {
        mDataManager.getAvailableBookingsFiltered(event.getBookingFindJob(),
                new DataManager.Callback<BookingsListWrapper>()
                {
                    @Override
                    public void onSuccess(final BookingsListWrapper bookingsListWrapper)
                    {
                        if (bookingsListWrapper != null && bookingsListWrapper.getBookingsWrappers() != null)
                        {
                            for (BookingsWrapper bookingsWrapper : bookingsListWrapper.getBookingsWrappers())
                            {
                                Date day = DateTimeUtils.getDateWithoutTime(bookingsWrapper.getDate());
                                Crashlytics.log("Received available bookings for " + day);
                                availableBookingsCache.put(day, bookingsWrapper);
                                mBus.post(new HandyEvent.ReceiveAvailableBookingsSuccess(bookingsWrapper, day));
                            }
                        }
                        else
                        {
                            onError(null);
                        }
                    }

                    @Override
                    public void onError(final DataManager.DataManagerError error)
                    {
                        List<Date> dateList = new ArrayList<>();
                        dateList.add(DateTimeUtils.getDateWithoutTime(event.getBookingFindJob().getAvailableStartTime()));
                        mBus.post(new HandyEvent.ReceiveAvailableBookingsError(error, dateList));
                    }
                }
        );
    }

    @Subscribe
    public void onRequestAvailableBookings(final HandyEvent.RequestAvailableBookings event)
    {
        final List<Date> datesToRequest = new ArrayList<>();
        for (Date date : event.getDates())
        {
            final Date day = DateTimeUtils.getDateWithoutTime(date);
            if (event.useCachedIfPresent)
            {
                final BookingsWrapper cachedBookings = availableBookingsCache.getIfPresent(day);
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
                    null,
                    new DataManager.Callback<BookingsListWrapper>()
                    {
                        @Override
                        public void onSuccess(final BookingsListWrapper bookingsListWrapper)
                        {
                            if (bookingsListWrapper != null && bookingsListWrapper.getBookingsWrappers() != null)
                            {
                                for (BookingsWrapper bookingsWrapper : bookingsListWrapper.getBookingsWrappers())
                                {
                                    Date day = DateTimeUtils.getDateWithoutTime(bookingsWrapper.getDate());
                                    Crashlytics.log("Received available bookings for " + day);
                                    availableBookingsCache.put(day, bookingsWrapper);
                                    mBus.post(new HandyEvent.ReceiveAvailableBookingsSuccess(bookingsWrapper, day));
                                }
                            }
                            else
                            {
                                mBus.post(new HandyEvent.ReceiveAvailableBookingsError(null, datesToRequest));
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

    @Subscribe
    public void onRequestOnboardingJobs(HandyEvent.RequestOnboardingJobs event)
    {
        mDataManager.getOnboardingJobs(event.getStartDate(), event.getPreferredZipclusterIds(),
                new DataManager.Callback<BookingsListWrapper>()
                {
                    @Override
                    public void onSuccess(final BookingsListWrapper bookingsListWrapper)
                    {
                        mBus.post(new HandyEvent.ReceiveOnboardingJobsSuccess(bookingsListWrapper));
                    }

                    @Override
                    public void onError(final DataManager.DataManagerError error)
                    {
                        mBus.post(new HandyEvent.ReceiveOnboardingJobsError(error));
                    }
                }
        );
    }

    @Subscribe
    public void onRequestProRequestedJobsCount(
            final BookingEvent.RequestProRequestedJobsCount event)
    {
        final Map<String, Object> options = new HashMap<>();
        final List<Date> dates = DateTimeUtils.getDateWithoutTimeList(new Date(),
                BookingManager.REQUESTED_JOBS_NUM_DAYS_IN_ADVANCE);
        options.put(BookingRequestKeys.IS_PROVIDER_REQUESTED, true);
        mDataManager.getJobsCount(dates, options,
                new DataManager.Callback<HashMap<String, Object>>()
                {
                    @Override
                    public void onSuccess(final HashMap<String, Object> response)
                    {
                        int count = (int) ((double) response.get(JOBS_COUNT_KEY));
                        mBus.post(new BookingEvent.ReceiveProRequestedJobsCountSuccess(count));
                    }

                    @Override
                    public void onError(final DataManager.DataManagerError error)
                    {
                        // Ignore
                    }
                });
    }

    @Subscribe
    public void onRequestProRequestedJobs(BookingEvent.RequestProRequestedJobs event)
    {
        List<Date> datesForBookings = event.getDatesForBookings();
        if (datesForBookings == null)
        {
            datesForBookings = DateTimeUtils.getDateWithoutTimeList(new Date(), BookingManager.REQUESTED_JOBS_NUM_DAYS_IN_ADVANCE);
        }

        boolean matchingCache = false;

        if (event.useCachedIfPresent())
        {
            matchingCache = true; //assume true until broken
            List<BookingsWrapper> bookingsListWrapper = new ArrayList<>();
            //check our cache to see if we have a hit for the dates, do not need to check options since they are always the same for this request
            //not going to be smart and assemble stuff now, just see if everything matches, otherwise ignor
            for (Date date : datesForBookings)
            {
                final BookingsWrapper bookingsWrapper = requestedBookingsCache.getIfPresent(date);
                //cut out early if something doesn't fit, then just go do request
                if (bookingsWrapper != null)
                {
                    bookingsListWrapper.add(bookingsWrapper);
                }
                else
                {
                    matchingCache = false;
                    break;
                }
            }

            //full match, send the cached data
            if (matchingCache)
            {
                mBus.post(new BookingEvent.ReceiveProRequestedJobsSuccess(bookingsListWrapper));
            }
        }

        //We don't want to use the cache or the cache was not an exact match
        if (!matchingCache)
        {
            Map<String, Object> options = new HashMap<>();
            options.put(BookingRequestKeys.IS_PROVIDER_REQUESTED, true);
            mDataManager.getAvailableBookings(datesForBookings.toArray(new Date[datesForBookings.size()]),
                    options,
                    new DataManager.Callback<BookingsListWrapper>()
                    {
                        @Override
                        public void onSuccess(final BookingsListWrapper bookingsListWrapper)
                        {
                            if (bookingsListWrapper != null && bookingsListWrapper.getBookingsWrappers() != null)
                            {
                                for (BookingsWrapper bookingsWrapper : bookingsListWrapper.getBookingsWrappers())
                                {
                                    Date day = DateTimeUtils.getDateWithoutTime(bookingsWrapper.getDate());
                                    Crashlytics.log("Received requested bookings for " + day);
                                    requestedBookingsCache.put(day, bookingsWrapper);
                                }
                                mBus.post(new BookingEvent.ReceiveProRequestedJobsSuccess(bookingsListWrapper.getBookingsWrappers()));
                            }
                            else
                            {
                                mBus.post(new BookingEvent.ReceiveProRequestedJobsError(null));
                            }
                        }

                        @Override
                        public void onError(final DataManager.DataManagerError error)
                        {
                            mBus.post(new BookingEvent.ReceiveProRequestedJobsError(error));
                        }
                    }
            );
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
                final BookingsWrapper cachedBookings = scheduledBookingsCache.getIfPresent(day);
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
                                scheduledBookingsCache.put(day, bookingsWrapper);
                                mBus.post(new HandyEvent.ReceiveScheduledBookingsSuccess(bookingsWrapper, day));
                            }

                            /*
                            this complements the original request event.

                            this is required because some components need to get notified
                            (just once, which is why we can't use the above event)
                            that the original request was responded to
                             */
                            mBus.post(new HandyEvent.ReceiveScheduledBookingsBatchSuccess());
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
        final BookingsWrapper cachedComplementaryBookings = complementaryBookingsCache.getIfPresent(day);
        if (cachedComplementaryBookings != null)
        {
            mBus.post(new HandyEvent.ReceiveComplementaryBookingsSuccess(cachedComplementaryBookings.getBookings()));
        }
        else
        {
            mDataManager.getComplementaryBookings(event.bookingId, event.type, new DataManager.Callback<BookingsWrapper>()
            {
                @Override
                public void onSuccess(BookingsWrapper bookingsWrapper)
                {
                    List<Booking> bookings = bookingsWrapper.getBookings();
                    complementaryBookingsCache.put(day, bookingsWrapper);
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
        String claimSwitchJobId = null;
        BookingType claimSwitchJobType = null;
        if (event.booking.canSwap())
        {
            claimSwitchJobId = event.booking.getSwappableBooking().getId();
            claimSwitchJobType = event.booking.getSwappableBooking().getType();
        }
        final Date day = DateTimeUtils.getDateWithoutTime(event.booking.getStartDate());

        mDataManager.claimBooking(bookingId, bookingType, claimSwitchJobId, claimSwitchJobType,
                new DataManager.Callback<BookingClaimDetails>()
        {
            @Override
            public void onSuccess(BookingClaimDetails bookingClaimDetails)
            {
                //have to invalidate cache for all days because we need updated priority access info
                //TODO investigate something better
                invalidateCachesForAllDays();

                mBus.post(new HandyEvent.ReceiveClaimJobSuccess(bookingClaimDetails, event.source));
            }

            @Override
            public void onError(DataManager.DataManagerError error)
            {
                //still need to invalidate so we don't allow them to click on same booking
                invalidateCachesForDay(day);
                mBus.post(new HandyEvent.ReceiveClaimJobError(event.booking, event.source, error));
            }
        });
    }


    @Subscribe
    public void onRequestClaimJobs(final HandyEvent.RequestClaimJobs event)
    {
        mDataManager.claimBookings(event.mJobClaimRequest, new DataManager.Callback<JobClaimResponse>()
        {
            @Override
            public void onSuccess(JobClaimResponse response)
            {
                mBus.post(new HandyEvent.ReceiveClaimJobsSuccess(response));
            }

            @Override
            public void onError(DataManager.DataManagerError error)
            {
                mBus.post(new HandyEvent.ReceiveClaimJobsError(error));
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
                //have to invalidate cache for all days because we need updated priority access info
                //TODO investigate something better
                invalidateCachesForAllDays();

                mBus.post(new HandyEvent.ReceiveRemoveJobSuccess(booking));
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
                invalidateScheduledBookingCache(
                        DateTimeUtils.getDateWithoutTime(booking.getStartDate()));
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

        mDataManager.notifyCheckInBooking(event.bookingId, locationData.getLocationMap(), new DataManager.Callback<Booking>()
        {
            @Override
            public void onSuccess(Booking booking)
            {
                mBus.post(new HandyEvent.ReceiveNotifyJobCheckInSuccess(booking));
            }

            @Override
            public void onError(DataManager.DataManagerError error)
            {
                //still need to invalidate so we don't allow them to click on same booking
                mBus.post(new HandyEvent.ReceiveNotifyJobCheckInError(error));
            }
        });
    }

    @Subscribe
    public void onRequestNotifyCheckOut(final HandyEvent.RequestNotifyJobCheckOut event)
    {
        mDataManager.notifyCheckOutBooking(event.bookingId, event.checkoutRequest, new DataManager.Callback<Booking>()
        {
            @Override
            public void onSuccess(Booking booking)
            {
                mBus.post(new HandyEvent.ReceiveNotifyJobCheckOutSuccess(booking));
                invalidateScheduledBookingCache(
                        DateTimeUtils.getDateWithoutTime(booking.getStartDate()));
            }

            @Override
            public void onError(DataManager.DataManagerError error)
            {
                mBus.post(new HandyEvent.ReceiveNotifyJobCheckOutError(error));
            }
        });
    }

    @Subscribe
    public void onRequestPostCheckoutInfo(final BookingEvent.RequestPostCheckoutInfo event)
    {
        mDataManager.requestPostCheckoutInfo(event.getBookingId(), new DataManager.Callback<PostCheckoutInfo>()
        {
            @Override
            public void onSuccess(final PostCheckoutInfo postCheckoutInfo)
            {
                mBus.post(new BookingEvent.ReceivePostCheckoutInfoSuccess(postCheckoutInfo));
            }

            @Override
            public void onError(final DataManager.DataManagerError error)
            {
                mBus.post(new BookingEvent.ReceivePostCheckoutInfoError());
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
    public void onRateCustomer(BookingEvent.RateCustomer event)
    {
        mDataManager.rateCustomer(event.bookingId, event.rating, event.reviewText, new DataManager.Callback<Void>()
        {
            @Override
            public void onSuccess(Void response)
            {
                mBus.post(new BookingEvent.RateCustomerSuccess());
            }

            @Override
            public void onError(DataManager.DataManagerError error)
            {
                mBus.post(new BookingEvent.RateCustomerError(error));
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

    private TypeSafeMap<ProviderKey> getNoShowParams(boolean active, LocationData locationData)
    {
        TypeSafeMap<ProviderKey> noShowParams = new TypeSafeMap<>();
        TypeSafeMap<LocationKey> locationParamsMap = locationData.getLocationMap();
        noShowParams.put(ProviderKey.LATITUDE, locationParamsMap.get(LocationKey.LATITUDE));
        noShowParams.put(ProviderKey.LONGITUDE, locationParamsMap.get(LocationKey.LONGITUDE));
        noShowParams.put(ProviderKey.ACCURACY, locationParamsMap.get(LocationKey.ACCURACY));
        noShowParams.put(ProviderKey.ACTIVE, Boolean.toString(active));
        return noShowParams;
    }

    private void invalidateCachesForAllDays()
    {
        availableBookingsCache.invalidateAll();
        scheduledBookingsCache.invalidateAll();
        complementaryBookingsCache.invalidateAll();
        requestedBookingsCache.invalidateAll();
        // We want to get requested jobs count again because forcing cache invalidation implies
        // claiming or removing a job which will affect requested jobs count.
        onRequestProRequestedJobsCount(new BookingEvent.RequestProRequestedJobsCount());
    }

    private void invalidateCachesForDay(Date day)
    {
        availableBookingsCache.invalidate(day);
        scheduledBookingsCache.invalidate(day);
        complementaryBookingsCache.invalidate(day);
        requestedBookingsCache.invalidate(day);
    }

    private void invalidateScheduledBookingCache(Date date)
    {
        scheduledBookingsCache.invalidate(date);
    }
}
