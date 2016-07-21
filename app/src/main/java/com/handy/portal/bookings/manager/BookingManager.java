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
import com.handy.portal.constant.LocationKey;
import com.handy.portal.constant.ProviderKey;
import com.handy.portal.data.DataManager;
import com.handy.portal.event.HandyEvent;
import com.handy.portal.library.util.DateTimeUtils;
import com.handy.portal.manager.HandyConnectivityManager;
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
    private final EventBus mBus;
    private final DataManager mDataManager;
    private final HandyConnectivityManager mHandyConnectivityManager;

    //Active timed caches
    private final Cache<Date, BookingsWrapper> availableBookingsCache;
    private final Cache<Date, BookingsWrapper> scheduledBookingsCache;
    private final Cache<Date, BookingsWrapper> complementaryBookingsCache;
    private final Cache<Date, BookingsWrapper> requestedBookingsCache;

    //Offline eternal-ish caches
    private final Cache<Date, BookingsWrapper> offline_availableBookingsCache;
    private final Cache<Date, BookingsWrapper> offline_scheduledBookingsCache;
    private final Cache<Date, BookingsWrapper> offline_complementaryBookingsCache;
    private final Cache<Date, BookingsWrapper> offline_requestedBookingsCache;

    private final List<Cache<Date, BookingsWrapper>> offlineCaches;


    private Map<RequestType, List<List<Date>>> mOutboundsRequestsByType;

    //private List<List<Date>> mOutboundsRequests; //track what is currently outbound so we don't dupe requests


    private enum RequestType
    {
        AVAILABLE,
        SCHEDULED,
        REQUESTED,
        COMPLEMENTARY
    }


    /*
    keys used in QueryMap requests
     */
    private final class BookingRequestKeys
    {
        private static final String IS_PROVIDER_REQUESTED = "is_requested";
    }

    @Inject
    public BookingManager(final EventBus bus, final DataManager dataManager, final HandyConnectivityManager handyConnectivityManager)
    {
        mOutboundsRequestsByType = new HashMap<>();
        mOutboundsRequestsByType.put(RequestType.AVAILABLE, new ArrayList<List<Date>>());
        mOutboundsRequestsByType.put(RequestType.SCHEDULED, new ArrayList<List<Date>>());
        mOutboundsRequestsByType.put(RequestType.REQUESTED, new ArrayList<List<Date>>());
        mOutboundsRequestsByType.put(RequestType.COMPLEMENTARY, new ArrayList<List<Date>>());


        mBus = bus;
        mBus.register(this);
        mDataManager = dataManager;
        mHandyConnectivityManager = handyConnectivityManager;

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

        //Offline caches
        this.offline_availableBookingsCache = CacheBuilder.newBuilder()
                .maximumSize(100)
                .build();
        this.offline_scheduledBookingsCache = CacheBuilder.newBuilder()
                .maximumSize(100)
                .build();
        this.offline_complementaryBookingsCache = CacheBuilder.newBuilder()
                .maximumSize(100)
                .build();
        this.offline_requestedBookingsCache = CacheBuilder.newBuilder()
                .maximumSize(100)
                .build();

        offlineCaches = new ArrayList<Cache<Date, BookingsWrapper>>();
        offlineCaches.add(this.offline_availableBookingsCache);
        offlineCaches.add(this.offline_scheduledBookingsCache);
        offlineCaches.add(this.offline_complementaryBookingsCache);
        offlineCaches.add(this.offline_requestedBookingsCache);

    }


    private void testStuff()
    {
//hunt through the caches looking for it
        System.out.println("CSD testing stuff");
        for (Cache c : offlineCaches)
        {
            System.out.println("CSD see a cache");

            //Iterable<BookingsWrapper> keys = c.asMap().values();

            Iterable<BookingsWrapper> values = c.asMap().values();

            for (BookingsWrapper wrapper : values)
            {
                System.out.println("CSD see a wrapper");
                for (Booking b : wrapper.getBookings())
                {
                    System.out.println("CSD See a booking value " + b.getId());
                }
            }

            //Map<Integer, Integer> result = c.getAllPresent(keys);

            //c.getAllPresent();

//                Integer keyInL1 = Iterables.get( cache.asMap().keySet(), 0 );
//                127         Integer keyInL2 = Iterables.get( cacheService.getMap().keySet(), 0 );
//                128
//                129         Iterable<Integer> keys = Lists.newArrayList( keyInL1, keyInL2, 1000 );
//                130         Map<Integer, Integer> result = cache.getAllPresent( keys );

//                for (Map.Entry<Date, BookingsWrapper> entry : c.asMap().entrySet()) {
//                    Date key = entry.getKey();
//                    BookingsWrapper value = entry.getValue();
//                    value.getBookings()
//                }
//
//                for(Iterable i : c.getAllPresent())
//                {
//
//                }
        }


    }

    private void findSingleBookingInOfflineCaches(String bookingId)
    {
        for (Cache c : offlineCaches)
        {
            Iterable<BookingsWrapper> values = c.asMap().values();
            for (BookingsWrapper wrapper : values)
            {
                //System.out.println("CSD see a wrapper");
                for (Booking b : wrapper.getBookings())
                {
                    //System.out.println("CSD See a booking value " + b.getId());
                    if (b.getId().equals(bookingId))
                    {
                        System.out.println("Found a match! : " + bookingId);
                        mBus.post(new HandyEvent.ReceiveBookingDetailsSuccess(b));
                        return;
                    }
                }
            }
        }

        //couldn't find it in the cachces
        System.out.println("Sorry we couldn't find it in the caches :(");
        //TODO: say something about being in offline mode?
        mBus.post(new HandyEvent.ReceiveBookingDetailsError(new DataManager.DataManagerError(DataManager.DataManagerError.Type.NETWORK, "Go online to see booking details")));
    }


    private boolean isMatchingOutboundRequest(List<List<Date>> outboundRequests, List<Date> requestedDates)
    {
        System.out.println("CSD - Checking outbound");

        if (requestedDates == null || requestedDates.isEmpty())
        {
            return true;
        }

        for (List<Date> outboundDateList : outboundRequests)
        {
            if (outboundDateList != null && !outboundDateList.isEmpty())
            {
                System.out.println("CSD - checking for outbound match :  \n " + requestedDates.toString() + " \n " + outboundDateList.toString());

                //straight up match
                if (outboundDateList.equals(requestedDates))
                {
                    System.out.println("CSD - bam we got a straight up match, we are already in flight, just wait");
                    return true;
                }

                //or if our request is a subset
                if (outboundDateList.contains(requestedDates))
                {
                    System.out.println("CSD - the rare subset match! saving dat data");
                    return true;
                }

                //lastly, element by element check in case of ordering mismatch
                boolean elementMatch = true;
                for (Date date : requestedDates)
                {
                    if (!outboundDateList.contains(date))
                    {
                        elementMatch = false;
                    }
                }

                if (elementMatch)
                {
                    //by hand element check matches
                    System.out.println("CSD - by hand element check match!!!");
                    return true;
                }

            }
        }

        //sorry no match
        return false;
    }



    //all communication will be done through the bus
    //booking manager
    //requests and caches data about bookings
    //responds to requests for data about bookings or lists of bookings
    //listens and responds to requests to claim / cancel

    @Subscribe
    public void onRequestBookingDetails(final HandyEvent.RequestBookingDetails event)
    {
        if (event == null || event.bookingId == null || event.type == null)
        {
            mBus.post(new HandyEvent.ReceiveBookingDetailsError(new DataManager.DataManagerError(DataManager.DataManagerError.Type.CLIENT, "Invalid booking requested")));
            return;
        }

        String bookingId = event.bookingId;
        BookingType type = event.type;

        if (mHandyConnectivityManager.hasConnectivity())
        {
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
        else
        {
            //testStuff();
            findSingleBookingInOfflineCaches(bookingId);
        }


    }

    @Subscribe
    public void onRequestAvailableBookings(final HandyEvent.RequestAvailableBookings event)
    {

        if (event.dates == null || event.dates.isEmpty())
        {
            System.out.println("CSD - making a bad or empty request returning client error");
            mBus.post(new HandyEvent.ReceiveAvailableBookingsError(new DataManager.DataManagerError(DataManager.DataManagerError.Type.CLIENT), event.dates));
            return;
        }

        if (mHandyConnectivityManager.hasConnectivity())
        {
            final List<Date> datesToRequest = new ArrayList<>();

            for (Date date : event.dates)
            {
                final Date dateWithoutTime = DateTimeUtils.getDateWithoutTime(date);
                System.out.println("CSD - AVAIL - checking date for cached : " + date.toString() + " : " + dateWithoutTime);
                System.out.println("CSD - AVAIL - cache size is ? : " + availableBookingsCache.asMap().keySet().size() + " : hashcode : " + availableBookingsCache.hashCode());

                if (event.useCachedIfPresent)
                {
                    System.out.println("CSD - use cached if present is okay");

                    int manualCount = 0;
                    for (Date availableBookingsCacheKey : availableBookingsCache.asMap().keySet())
                    {
                        manualCount++;
                    }
                    System.out.println("CSD - manual count : " + manualCount + " : size check : " + availableBookingsCache.asMap().keySet().size());

                    //FIXME: WAIT, maybe not, something else is wrong....
                    //This isn't working because it is doing direct object check, not date comparison, oof. getIfPresent fail

                    boolean handCompareMatch = false;

                    for (Date availableBookingsCacheKey : availableBookingsCache.asMap().keySet())
                    {
                        System.out.println("CSD - Hand comparing : request : " + dateWithoutTime.toString() + " : cache :  " + availableBookingsCacheKey.toString());
                        if (availableBookingsCacheKey.equals(dateWithoutTime))
                        {
                            System.out.println("CSD - hand match iterator");
                            handCompareMatch = true;
                            break;
                        }
                    }

                    boolean handCompareMatch2 = false;
                    if (availableBookingsCache.asMap().keySet().contains(dateWithoutTime))
                    {
                        System.out.println("CSD - hand match using contains instead");
                        handCompareMatch2 = true;
                    }

//cachedbookings uses object comparison not value comparison
                    final BookingsWrapper cachedBookings = availableBookingsCache.getIfPresent(dateWithoutTime);

                    System.out.println("CSD Cache check : " + handCompareMatch + " : " + handCompareMatch2 + " : " + (cachedBookings != null));



                    if (cachedBookings != null)
                    {
                        System.out.println("CSD - avail - sending cache match for dateWithoutTime : " + dateWithoutTime);
                        mBus.post(new HandyEvent.ReceiveAvailableBookingsSuccess(cachedBookings, dateWithoutTime));

                    }
                    else
                    {
                        System.out.println("CSD - avail - not in cached bookings : " + dateWithoutTime);
                        datesToRequest.add(dateWithoutTime);
                    }
                }
                else
                {
                    System.out.println("CSD - avail - not going to use cache explicitly : " + dateWithoutTime);
                    datesToRequest.add(dateWithoutTime);
                }
            }

            if (!datesToRequest.isEmpty())
            {
                System.out.println("CSD - AVAIL non empty request list : " + datesToRequest.toString() + " : " + datesToRequest.size());

                boolean alreadyOutbound = isMatchingOutboundRequest(mOutboundsRequestsByType.get(RequestType.AVAILABLE), datesToRequest);
                if (!alreadyOutbound)
                {
                    System.out.println("CSD - AVAIL NEW STUFF, DO THE DATA : " + datesToRequest.toString() + " : " + datesToRequest.size());

                    //add to our outbound request
                    mOutboundsRequestsByType.get(RequestType.AVAILABLE).add(datesToRequest);

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

                                            System.out.println("CSD - Received available bookings for " + day);

                                            System.out.println("CSD cache size was : " + availableBookingsCache.size());
                                            availableBookingsCache.put(day, bookingsWrapper);
                                            System.out.println("CSD cache size now : " + availableBookingsCache.size());

                                            offline_availableBookingsCache.put(day, bookingsWrapper); //additive right now since not clearing

                                            mBus.post(new HandyEvent.ReceiveAvailableBookingsSuccess(bookingsWrapper, day));

                                        }

                                        //we got back out request, remove it from the outbound tracking
                                        System.out.println("Removing the outbound tracker for this request : " + datesToRequest.toString());
                                        mOutboundsRequestsByType.get(RequestType.AVAILABLE).remove(datesToRequest);

                                    }
                                    else
                                    {
                                        mBus.post(new HandyEvent.ReceiveAvailableBookingsError(new DataManager.DataManagerError(DataManager.DataManagerError.Type.SERVER), datesToRequest));

                                        //we got back out request, remove it from the outbound tracking
                                        System.out.println("Removing the outbound tracker for this request : " + datesToRequest.toString());
                                        mOutboundsRequestsByType.get(RequestType.AVAILABLE).remove(datesToRequest);
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
                else
                {
                    System.out.println("CSD AVAIL HOLD YOUR HORSES ALREADY RUNNING : " + datesToRequest.toString() + " : " + datesToRequest.size());
                }
            }
            else
            {
                //dates to request is empty, is there anyway we can get in a bad state because of this?
                System.out.println("CSD - avail - full cache match, already sent the cached data");
            }
        }
        else
        {
            //use the semi-eternal cache
            List<Date> missingDates = new ArrayList<>();
            boolean sentSomething = false;

            System.out.println("CSD - You are offline, using the offline cache which should be at worst the same as latest");
            for (Date date : event.dates)
            {
                final Date day = DateTimeUtils.getDateWithoutTime(date);
                final BookingsWrapper cachedBookings = offline_availableBookingsCache.getIfPresent(day);
                if (cachedBookings != null)
                {
                    sentSomething = true;
                    mBus.post(new HandyEvent.ReceiveAvailableBookingsSuccess(cachedBookings, day));
                }
                else
                {
                    missingDates.add(date);
                }
            }

            //we have some leftover dates but nothing to do about it :(
            if (!missingDates.isEmpty() && !sentSomething)
            {
                mBus.post(new HandyEvent.ReceiveAvailableBookingsError(
                        new DataManager.DataManagerError(DataManager.DataManagerError.Type.NETWORK, "offline availaa cache fail"),
                        missingDates));
            }

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
    public void onRequestProRequestedJobs(BookingEvent.RequestProRequestedJobs event)
    {
        /*
            FIXME: would rather use a serialized request model for the options
            but don't know how to pass it as a QueryMap
            without errors
         */

        if (event.getDatesForBookings() == null || event.getDatesForBookings().isEmpty())
        {
            System.out.println("making a bad or empty request, returning empty list");
        }


        if (mHandyConnectivityManager.hasConnectivity())
        {
            boolean matchingCache = false;

            final List<Date> requestedDates = event.getDatesForBookings();

            if (event.useCachedIfPresent())
            {
                matchingCache = true; //assume true until broken
                List<BookingsWrapper> bookingsListWrapper = new ArrayList<>();
                //check our cache to see if we have a hit for the dates, do not need to check options since they are always the same for this request
                //not going to be smart and assemble stuff now, just see if everything matches, otherwise ignore
                for (Date date : requestedDates)
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
                boolean alreadyOutbound = isMatchingOutboundRequest(mOutboundsRequestsByType.get(RequestType.REQUESTED), requestedDates);
                if (!alreadyOutbound)
                {
                    Map<String, Object> options = new HashMap<>();
                    options.put(BookingRequestKeys.IS_PROVIDER_REQUESTED, true);

                    mOutboundsRequestsByType.get(RequestType.REQUESTED).add(requestedDates);

                    mDataManager.getAvailableBookings(event.getDatesForBookings().toArray(new Date[event.getDatesForBookings().size()]),
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
                                            offline_requestedBookingsCache.put(day, bookingsWrapper);
                                        }
                                        mBus.post(new BookingEvent.ReceiveProRequestedJobsSuccess(bookingsListWrapper.getBookingsWrappers()));
                                    }
                                    else
                                    {
                                        mBus.post(new BookingEvent.ReceiveProRequestedJobsError(null));
                                    }

                                    mOutboundsRequestsByType.get(RequestType.REQUESTED).remove(requestedDates);
                                }

                                @Override
                                public void onError(final DataManager.DataManagerError error)
                                {
                                    mBus.post(new BookingEvent.ReceiveProRequestedJobsError(error));
                                    mOutboundsRequestsByType.get(RequestType.REQUESTED).remove(requestedDates);
                                }
                            }
                    );
                }
                else
                {
                    System.out.println("outbound already for requested matching outbound, just wait");
                }


            }
        }
        else //offline mode
        {
            //just send what we have since we can't make more requests
            List<BookingsWrapper> bookingsListWrapper = new ArrayList<>();
            for (Date date : event.getDatesForBookings())
            {
                final BookingsWrapper bookingsWrapper = offline_requestedBookingsCache.getIfPresent(date);
                //cut out early if something doesn't fit, then just go do request
                if (bookingsWrapper != null)
                {
                    bookingsListWrapper.add(bookingsWrapper);
                }
            }

            //best we've got even without a match, send it
            if (bookingsListWrapper.isEmpty())
            {
                mBus.post(new BookingEvent.ReceiveProRequestedJobsSuccess(bookingsListWrapper));
            }
            else
            {
                mBus.post(new BookingEvent.ReceiveProRequestedJobsError(
                        new DataManager.DataManagerError(DataManager.DataManagerError.Type.NETWORK,
                                "Offline mode requested jobs")));
            }
        }
    }

    @Subscribe
    public void onRequestScheduledBookings(final HandyEvent.RequestScheduledBookings event)
    {

        System.out.println("CSD getting scheduled : have connection? : " + mHandyConnectivityManager.hasConnectivity());

        if (event.dates == null || event.dates.isEmpty())
        {
            System.out.println("making a bad or empty request, returning empty list");
        }

        if (mHandyConnectivityManager.hasConnectivity())
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
                boolean alreadyOutbound = isMatchingOutboundRequest(mOutboundsRequestsByType.get(RequestType.SCHEDULED), datesToRequest);
                if (!alreadyOutbound)
                {
                    mOutboundsRequestsByType.get(RequestType.SCHEDULED).add(datesToRequest);

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
                                        offline_scheduledBookingsCache.put(day, bookingsWrapper);

                                        mBus.post(new HandyEvent.ReceiveScheduledBookingsSuccess(bookingsWrapper, day));
                                    }

                                    mOutboundsRequestsByType.get(RequestType.SCHEDULED).remove(datesToRequest);

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
                                    mOutboundsRequestsByType.get(RequestType.SCHEDULED).remove(datesToRequest);
                                }
                            }
                    );
                }
                else
                {
                    System.out.println("scheduled jobs matches outbound tracking");
                }
            }
            else
            {
                //can we end up in a bad state with no response?
                System.out.println("no dates left to request, cache should have covered it all or no requests actually made");
            }
        }
        else //offline mode
        {
            List<Date> missingDates = new ArrayList<>();
            boolean returnedSomething = false;
            for (Date date : event.dates)
            {
                final Date day = DateTimeUtils.getDateWithoutTime(date);
                final BookingsWrapper cachedBookings = offline_scheduledBookingsCache.getIfPresent(day);
                if (cachedBookings != null)
                {
                    Log.d(getClass().getName(), "using offline scheduled bookings: " + day.toString());
                    mBus.post(new HandyEvent.ReceiveScheduledBookingsSuccess(cachedBookings, day));
                    returnedSomething = true;
                }
                else
                {
                    missingDates.add(day);
                }
                //Since we are offline we do not send mBus.post(new HandyEvent.ReceiveScheduledBookingsBatchSuccess());
            }

            if (!missingDates.isEmpty() && !returnedSomething)
            {
                System.out.println("Returning error for the missing dates");
                mBus.post(new HandyEvent.ReceiveScheduledBookingsError(
                        new DataManager.DataManagerError(DataManager.DataManagerError.Type.NETWORK, "Offline mode"),
                        missingDates));
            }


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
                    offline_complementaryBookingsCache.put(day, bookingsWrapper);
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
        System.out.println("CSD - INVALID ALL CACHES");

        availableBookingsCache.invalidateAll();
        scheduledBookingsCache.invalidateAll();
        complementaryBookingsCache.invalidateAll();
        requestedBookingsCache.invalidateAll();

        offline_availableBookingsCache.invalidateAll();
        offline_scheduledBookingsCache.invalidateAll();
        offline_complementaryBookingsCache.invalidateAll();
        offline_requestedBookingsCache.invalidateAll();

    }

    private void invalidateCachesForDay(Date day)
    {
        System.out.println("CSD - INVALID CACHE FOR DAY : " + day.toString());

        availableBookingsCache.invalidate(day);
        scheduledBookingsCache.invalidate(day);
        complementaryBookingsCache.invalidate(day);
        requestedBookingsCache.invalidate(day);

        offline_availableBookingsCache.invalidate(day);
        offline_scheduledBookingsCache.invalidate(day);
        offline_complementaryBookingsCache.invalidate(day);
        offline_requestedBookingsCache.invalidate(day);
    }

    private void invalidateScheduledBookingCache(Date date)
    {
        scheduledBookingsCache.invalidate(date);

        offline_scheduledBookingsCache.invalidate(date);
    }

}
