package com.handy.portal.bookings.manager;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
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
import com.handy.portal.bookings.model.CheckoutRequest;
import com.handy.portal.bookings.model.PostCheckoutInfo;
import com.handy.portal.core.constant.LocationKey;
import com.handy.portal.core.constant.ProviderKey;
import com.handy.portal.core.event.HandyEvent;
import com.handy.portal.core.model.LocationData;
import com.handy.portal.core.model.TypeSafeMap;
import com.handy.portal.data.DataManager;
import com.handy.portal.library.util.DateTimeUtils;
import com.handy.portal.onboarding.model.claim.JobClaimRequest;
import com.handy.portal.onboarding.model.claim.JobClaimResponse;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

public class BookingManager {
    public static final int REQUESTED_JOBS_NUM_DAYS_IN_ADVANCE = 14; // TODO: Make this a config param
    public static final String UNREAD_JOB_REQUESTS_COUNT = "unread_requests_count";

    public static final String DISMISSAL_REASON_UNSPECIFIED = "unspecified";
    public static final String DISMISSAL_REASON_BLOCK_CUSTOMER = "do_not_want_this_customer";
    @StringDef({DISMISSAL_REASON_UNSPECIFIED, DISMISSAL_REASON_BLOCK_CUSTOMER})
    public @interface DismissalReason {}

    private final EventBus mBus;
    private final DataManager mDataManager;

    private final Cache<Date, BookingsWrapper> availableBookingsCache;
    private final Cache<Date, BookingsWrapper> scheduledBookingsCache;
    private final Cache<Date, BookingsWrapper> requestedBookingsCache;
    private Integer mLastUnreadRequestsCount = null;


    /*
    keys used in QueryMap requests
     */
    private final class BookingRequestKeys {
        private static final String IS_PROVIDER_REQUESTED = "is_requested";
    }

    @Inject
    public BookingManager(final EventBus bus, final DataManager dataManager) {
        mBus = bus;
        mDataManager = dataManager;

        this.availableBookingsCache = CacheBuilder.newBuilder()
                .maximumSize(100)
                .expireAfterWrite(2, TimeUnit.MINUTES)
                .build();

        this.scheduledBookingsCache = CacheBuilder.newBuilder()
                .maximumSize(100)
                .expireAfterWrite(2, TimeUnit.MINUTES)
                .build();

        this.requestedBookingsCache = CacheBuilder.newBuilder()
                .maximumSize(100)
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .build();
    }

    public void clearCache() {
        availableBookingsCache.invalidateAll();
        scheduledBookingsCache.invalidateAll();
        requestedBookingsCache.invalidateAll();
    }

    //booking manager
    //requests and caches data about bookings
    //responds to requests for data about bookings or lists of bookings
    //listens and responds to requests to claim / cancel

    public void requestBookingDetails(final String bookingId, final BookingType type, @Nullable final Date date) {
        mDataManager.getBookingDetails(bookingId, type, new DataManager.Callback<Booking>() {
            @Override
            public void onSuccess(Booking booking) {
                mBus.post(new HandyEvent.ReceiveBookingDetailsSuccess(booking));
            }

            @Override
            public void onError(DataManager.DataManagerError error) {
                mBus.post(new HandyEvent.ReceiveBookingDetailsError(error));
                if (date != null && error.getType() != DataManager.DataManagerError.Type.NETWORK) {
                    Date day = DateTimeUtils.getDateWithoutTime(date);
                    invalidateCachesForDay(day);
                }
            }
        });
    }

    public void requestAvailableBookings(@NonNull final List<Date> dates, final boolean useCachedIfPresent) {
        final List<Date> datesToRequest = new ArrayList<>();
        for (Date date : dates) {
            final Date day = DateTimeUtils.getDateWithoutTime(date);
            if (useCachedIfPresent) {
                final BookingsWrapper cachedBookings = availableBookingsCache.getIfPresent(day);
                if (cachedBookings != null) {
                    mBus.post(new HandyEvent.ReceiveAvailableBookingsSuccess(cachedBookings, day));
                }
                else {
                    datesToRequest.add(day);
                }
            }
            else {
                datesToRequest.add(day);
            }
        }

        if (!datesToRequest.isEmpty()) {
            mDataManager.getAvailableBookings(
                    datesToRequest.toArray(new Date[datesToRequest.size()]),
                    null,
                    new DataManager.Callback<BookingsListWrapper>() {
                        @Override
                        public void onSuccess(final BookingsListWrapper bookingsListWrapper) {
                            if (bookingsListWrapper != null && bookingsListWrapper.getBookingsWrappers() != null) {
                                for (BookingsWrapper bookingsWrapper : bookingsListWrapper.getBookingsWrappers()) {
                                    Date day = DateTimeUtils.getDateWithoutTime(bookingsWrapper.getDate());
                                    Crashlytics.log("Received available bookings for " + day);
                                    availableBookingsCache.put(day, bookingsWrapper);
                                    mBus.post(new HandyEvent.ReceiveAvailableBookingsSuccess(bookingsWrapper, day));
                                }
                            }
                            else {
                                mBus.post(new HandyEvent.ReceiveAvailableBookingsError(null, datesToRequest));
                            }
                        }

                        @Override
                        public void onError(final DataManager.DataManagerError error) {
                            mBus.post(new HandyEvent.ReceiveAvailableBookingsError(error, datesToRequest));
                        }
                    }
            );
        }
    }

    public void requestOnboardingJobs(final Date startDate, final ArrayList<String> preferredZipclusterIds) {
        mDataManager.getOnboardingJobs(startDate, preferredZipclusterIds,
                new DataManager.Callback<BookingsListWrapper>() {
                    @Override
                    public void onSuccess(final BookingsListWrapper bookingsListWrapper) {
                        mBus.post(new HandyEvent.ReceiveOnboardingJobsSuccess(bookingsListWrapper));
                    }

                    @Override
                    public void onError(final DataManager.DataManagerError error) {
                        mBus.post(new HandyEvent.ReceiveOnboardingJobsError(error));
                    }
                }
        );
    }

    public void requestProRequestedJobsCount() {
        final Map<String, Object> options = new HashMap<>();
        final List<Date> dates = DateTimeUtils.getDateWithoutTimeList(new Date(),
                BookingManager.REQUESTED_JOBS_NUM_DAYS_IN_ADVANCE);
        options.put(BookingRequestKeys.IS_PROVIDER_REQUESTED, true);
        mDataManager.getJobsCount(dates, options,
                new DataManager.Callback<HashMap<String, Object>>() {
                    @Override
                    public void onSuccess(final HashMap<String, Object> response) {
                        final int count = (int) (double) response.get(UNREAD_JOB_REQUESTS_COUNT);
                        mLastUnreadRequestsCount = count;
                        mBus.post(new BookingEvent.ReceiveProRequestedJobsCountSuccess(count));
                    }

                    @Override
                    public void onError(final DataManager.DataManagerError error) {
                        // Ignore
                    }
                });
    }

    @Nullable
    public Integer getLastUnreadRequestsCount() {
        return mLastUnreadRequestsCount;
    }

    public void requestProRequestedJobs(@Nullable List<Date> datesForBookings, final boolean useCachedIfPresent) {
        if (datesForBookings == null) {
            datesForBookings = DateTimeUtils.getDateWithoutTimeList(new Date(), BookingManager.REQUESTED_JOBS_NUM_DAYS_IN_ADVANCE);
        }

        boolean matchingCache = false;

        if (useCachedIfPresent) {
            matchingCache = true; //assume true until broken
            List<BookingsWrapper> bookingsListWrapper = new ArrayList<>();
            //check our cache to see if we have a hit for the dates, do not need to check options since they are always the same for this request
            //not going to be smart and assemble stuff now, just see if everything matches, otherwise ignor
            for (Date date : datesForBookings) {
                final BookingsWrapper bookingsWrapper = requestedBookingsCache.getIfPresent(date);
                //cut out early if something doesn't fit, then just go do request
                if (bookingsWrapper != null) {
                    bookingsListWrapper.add(bookingsWrapper);
                }
                else {
                    matchingCache = false;
                    break;
                }
            }

            //full match, send the cached data
            if (matchingCache) {
                mBus.post(new BookingEvent.ReceiveProRequestedJobsSuccess(bookingsListWrapper));
            }
        }

        //We don't want to use the cache or the cache was not an exact match
        if (!matchingCache) {
            Map<String, Object> options = new HashMap<>();
            requestProRequestedJobsCount(); // also pull requested jobs count
            options.put(BookingRequestKeys.IS_PROVIDER_REQUESTED, true);
            mDataManager.getAvailableBookings(datesForBookings.toArray(new Date[datesForBookings.size()]),
                    options,
                    new DataManager.Callback<BookingsListWrapper>() {
                        @Override
                        public void onSuccess(final BookingsListWrapper bookingsListWrapper) {
                            if (bookingsListWrapper != null && bookingsListWrapper.getBookingsWrappers() != null) {
                                for (BookingsWrapper bookingsWrapper : bookingsListWrapper.getBookingsWrappers()) {
                                    Date day = DateTimeUtils.getDateWithoutTime(bookingsWrapper.getDate());
                                    Crashlytics.log("Received requested bookings for " + day);
                                    requestedBookingsCache.put(day, bookingsWrapper);
                                }
                                mBus.post(new BookingEvent.ReceiveProRequestedJobsSuccess(bookingsListWrapper.getBookingsWrappers()));
                            }
                            else {
                                mBus.post(new BookingEvent.ReceiveProRequestedJobsError(null));
                            }
                        }

                        @Override
                        public void onError(final DataManager.DataManagerError error) {
                            mBus.post(new BookingEvent.ReceiveProRequestedJobsError(error));
                        }
                    }
            );
        }
    }

    public void requestScheduledBookings(@NonNull final List<Date> dates, final boolean useCachedIfPresent) {
        final List<Date> datesToRequest = new ArrayList<>();
        for (Date date : dates) {
            final Date day = DateTimeUtils.getDateWithoutTime(date);
            if (useCachedIfPresent) {
                final BookingsWrapper cachedBookings = scheduledBookingsCache.getIfPresent(day);
                if (cachedBookings != null) {
                    Log.d(getClass().getName(), "received scheduled bookings: " + day.toString());
                    mBus.post(new HandyEvent.ReceiveScheduledBookingsSuccess(cachedBookings, day));
                }
                else {
                    datesToRequest.add(day);
                }
            }
            else {
                datesToRequest.add(day);
            }
        }

        if (!datesToRequest.isEmpty()) {
            mDataManager.getScheduledBookings(datesToRequest.toArray(new Date[datesToRequest.size()]),
                    new DataManager.Callback<BookingsListWrapper>() {
                        @Override
                        public void onSuccess(final BookingsListWrapper bookingsListWrapper) {
                            for (BookingsWrapper bookingsWrapper : bookingsListWrapper.getBookingsWrappers()) {
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
                        public void onError(final DataManager.DataManagerError error) {
                            mBus.post(new HandyEvent.ReceiveScheduledBookingsError(error, datesToRequest));
                        }
                    }
            );
        }
    }

    public void requestClaimJob(@NonNull final Booking booking, @Nullable final String source) {
        String bookingId = booking.getId();
        BookingType bookingType = booking.getType();
        String claimSwitchJobId = null;
        BookingType claimSwitchJobType = null;
        if (booking.canSwap()) {
            claimSwitchJobId = booking.getSwappableBooking().getId();
            claimSwitchJobType = booking.getSwappableBooking().getType();
        }
        final Date day = DateTimeUtils.getDateWithoutTime(booking.getStartDate());

        mDataManager.claimBooking(bookingId, bookingType, claimSwitchJobId, claimSwitchJobType,
                new DataManager.Callback<BookingClaimDetails>() {
                    @Override
                    public void onSuccess(BookingClaimDetails bookingClaimDetails) {
                        //have to invalidate cache for all days because we need updated priority access info
                        //TODO investigate something better
                        invalidateCachesForAllDays();
                        mBus.post(new HandyEvent.ReceiveClaimJobSuccess(booking, bookingClaimDetails, source));
                    }

                    @Override
                    public void onError(DataManager.DataManagerError error) {
                        //still need to invalidate so we don't allow them to click on same booking
                        invalidateCachesForDay(day);
                        mBus.post(new HandyEvent.ReceiveClaimJobError(booking, source, error));
                    }
                });
    }


    public void requestClaimJobs(final JobClaimRequest jobClaimRequest) {
        mDataManager.claimBookings(jobClaimRequest, new DataManager.Callback<JobClaimResponse>() {
            @Override
            public void onSuccess(JobClaimResponse response) {
                mBus.post(new HandyEvent.ReceiveClaimJobsSuccess(response));
            }

            @Override
            public void onError(DataManager.DataManagerError error) {
                mBus.post(new HandyEvent.ReceiveClaimJobsError(error));
            }
        });

    }

    public void requestRemoveJob(@NonNull final Booking booking) {
        String bookingId = booking.getId();
        BookingType bookingType = booking.getType();
        final Date day = DateTimeUtils.getDateWithoutTime(booking.getStartDate());

        mDataManager.removeBooking(bookingId, bookingType, new DataManager.Callback<Booking>() {
            @Override
            public void onSuccess(Booking booking) {
                //have to invalidate cache for all days because we need updated priority access info
                //TODO investigate something better
                invalidateCachesForAllDays();

                mBus.post(new HandyEvent.ReceiveRemoveJobSuccess(booking));
            }

            @Override
            public void onError(DataManager.DataManagerError error) {
                //still need to invalidate so we don't allow them to click on same booking
                invalidateCachesForDay(day);
                mBus.post(new HandyEvent.ReceiveRemoveJobError(error));
            }
        });
    }

    public void requestDismissJob(@NonNull final Booking booking,
                                  @DismissalReason final String dismissalReason) {
        mDataManager.dismissJob(booking.getId(), booking.getType(), dismissalReason,
                new DataManager.Callback<Void>() {
                    @Override
                    public void onSuccess(final Void response) {
                        mBus.post(new HandyEvent.ReceiveDismissJobSuccess(booking, dismissalReason));
                        requestedBookingsCache.invalidateAll();
                    }

                    @Override
                    public void onError(final DataManager.DataManagerError error) {
                        mBus.post(new HandyEvent.ReceiveDismissJobError(booking, error));
                    }
                });
    }

    public void requestNotifyOnMyWay(final String bookingId, @NonNull final LocationData locationData) {

        mDataManager.notifyOnMyWayBooking(bookingId, locationData.getLocationMap(), new DataManager.Callback<Booking>() {
            @Override
            public void onSuccess(Booking booking) {
                mBus.post(new HandyEvent.ReceiveNotifyJobOnMyWaySuccess(booking));
                invalidateScheduledBookingCache(
                        DateTimeUtils.getDateWithoutTime(booking.getStartDate()));
            }

            @Override
            public void onError(DataManager.DataManagerError error) {
                mBus.post(new HandyEvent.ReceiveNotifyJobOnMyWayError(error));
            }
        });
    }

    public void requestNotifyCheckIn(final String bookingId, @NonNull final LocationData locationData) {
        mDataManager.notifyCheckInBooking(bookingId, locationData.getLocationMap(), new DataManager.Callback<Booking>() {
            @Override
            public void onSuccess(Booking booking) {
                mBus.post(new HandyEvent.ReceiveNotifyJobCheckInSuccess(booking));
            }

            @Override
            public void onError(DataManager.DataManagerError error) {
                //still need to invalidate so we don't allow them to click on same booking
                mBus.post(new HandyEvent.ReceiveNotifyJobCheckInError(error));
            }
        });
    }

    public void requestNotifyCheckOut(final String bookingId, final CheckoutRequest checkoutRequest) {
        mDataManager.notifyCheckOutBooking(bookingId, checkoutRequest, new DataManager.Callback<Booking>() {
            @Override
            public void onSuccess(Booking booking) {
                mBus.post(new HandyEvent.ReceiveNotifyJobCheckOutSuccess(booking));
                invalidateScheduledBookingCache(
                        DateTimeUtils.getDateWithoutTime(booking.getStartDate()));
            }

            @Override
            public void onError(DataManager.DataManagerError error) {
                mBus.post(new HandyEvent.ReceiveNotifyJobCheckOutError(error));
            }
        });
    }

    public void requestPostCheckoutInfo(final String bookingId) {
        mDataManager.requestPostCheckoutInfo(bookingId, new DataManager.Callback<PostCheckoutInfo>() {
            @Override
            public void onSuccess(final PostCheckoutInfo postCheckoutInfo) {
                mBus.post(new BookingEvent.ReceivePostCheckoutInfoSuccess(postCheckoutInfo));
            }

            @Override
            public void onError(final DataManager.DataManagerError error) {
                mBus.post(new BookingEvent.ReceivePostCheckoutInfoError());
            }
        });
    }

    public void requestNotifyUpdateArrivalTime(final String bookingId, final Booking.ArrivalTimeOption arrivalTimeOption) {
        mDataManager.notifyUpdateArrivalTimeBooking(bookingId, arrivalTimeOption, new DataManager.Callback<Booking>() {
            @Override
            public void onSuccess(Booking booking) {
                mBus.post(new HandyEvent.ReceiveNotifyJobUpdateArrivalTimeSuccess(booking));
            }

            @Override
            public void onError(DataManager.DataManagerError error) {
                mBus.post(new HandyEvent.ReceiveNotifyJobUpdateArrivalTimeError(error));
            }
        });
    }

    public void requestReportNoShow(final String bookingId, @NonNull final LocationData locationData) {
        mDataManager.reportNoShow(bookingId, getNoShowParams(true, locationData), new DataManager.Callback<Booking>() {
            @Override
            public void onSuccess(Booking booking) {
                mBus.post(new HandyEvent.ReceiveReportNoShowSuccess(booking));
            }

            @Override
            public void onError(DataManager.DataManagerError error) {
                mBus.post(new HandyEvent.ReceiveReportNoShowError(error));
            }
        });
    }

    public void rateCustomer(final String bookingId, final int rating, final String reviewText) {
        mDataManager.rateCustomer(bookingId, rating, reviewText, new DataManager.Callback<Void>() {
            @Override
            public void onSuccess(Void response) {
                mBus.post(new BookingEvent.RateCustomerSuccess());
            }

            @Override
            public void onError(DataManager.DataManagerError error) {
                mBus.post(new BookingEvent.RateCustomerError(error));
            }
        });
    }

    public void requestCancelNoShow(final String bookingId, @NonNull final LocationData locationData) {
        mDataManager.reportNoShow(bookingId, getNoShowParams(false, locationData), new DataManager.Callback<Booking>() {
            @Override
            public void onSuccess(Booking booking) {
                mBus.post(new HandyEvent.ReceiveCancelNoShowSuccess(booking));
            }

            @Override
            public void onError(DataManager.DataManagerError error) {
                mBus.post(new HandyEvent.ReceiveCancelNoShowError(error));
            }
        });
    }

    private TypeSafeMap<ProviderKey> getNoShowParams(boolean active, LocationData locationData) {
        TypeSafeMap<ProviderKey> noShowParams = new TypeSafeMap<>();
        TypeSafeMap<LocationKey> locationParamsMap = locationData.getLocationMap();
        noShowParams.put(ProviderKey.LATITUDE, locationParamsMap.get(LocationKey.LATITUDE));
        noShowParams.put(ProviderKey.LONGITUDE, locationParamsMap.get(LocationKey.LONGITUDE));
        noShowParams.put(ProviderKey.ACCURACY, locationParamsMap.get(LocationKey.ACCURACY));
        noShowParams.put(ProviderKey.ACTIVE, Boolean.toString(active));
        return noShowParams;
    }

    private void invalidateCachesForAllDays() {
        availableBookingsCache.invalidateAll();
        scheduledBookingsCache.invalidateAll();
        requestedBookingsCache.invalidateAll();
        // We want to get requested jobs count again because forcing cache invalidation implies
        // claiming or removing a job which will affect requested jobs count.
        requestProRequestedJobsCount();
    }

    private void invalidateCachesForDay(Date day) {
        availableBookingsCache.invalidate(day);
        scheduledBookingsCache.invalidate(day);
        requestedBookingsCache.invalidate(day);
    }

    private void invalidateScheduledBookingCache(Date date) {
        scheduledBookingsCache.invalidate(date);
    }
}
