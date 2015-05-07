package com.handy.portal.core;

import com.handy.portal.core.booking.Booking;
import com.handy.portal.core.booking.BookingCalendarDay;
import com.handy.portal.data.DataManager;
import com.handy.portal.data.SecurePreferences;
import com.handy.portal.event.BookingsRetrievedEvent;
import com.handy.portal.event.RequestAvailableBookingsEvent;
import com.handy.portal.event.RequestScheduledBookingsEvent;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import javax.inject.Inject;

public final class BookingManager implements Observer {
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

    //private List<Booking> updatedBookings; //we will need to send out a list of updated bookings after each action completes?



    @Inject
    BookingManager(final Bus bus, final SecurePreferences prefs, final DataManager dataManager) {
        this.securePrefs = prefs;
        this.bus = bus;
        this.bus.register(this);
        this.dataManager = dataManager;

        this.cachedBookings = new HashMap<String, Booking>();
        this.cachedBookingSummaries = new HashMap<BookingCalendarDay, BookingSummary>();
    }

    //all communication will be done through the bus
    //booking manager
    //requests and caches data about bookings
    //responds to requests for data about bookings or lists of bookings
    //listens and responds to requests to claim / cancel

    public void onRequestBookingDetails(String bookingId)
    {
        if(cachedBookings.containsKey(bookingId))
        {
            //send out the booking details from the cache
        }
        else
        {
            //put in a web request for the booking details
                //send out updated booking when received
        }

    }

    @Subscribe
    public void onRequestAvailableBookings(RequestAvailableBookingsEvent event)
    {
        String providerId = event.providerId;
        dataManager.getAvailableBookings(providerId, new DataManager.Callback<List<BookingSummary>>() {
                    @Override
                    public void onSuccess(final List<BookingSummary> bookingSummaries) {
                        onBookingSummariesReceived(bookingSummaries);
                    }

                    @Override
                    public void onError(final DataManager.DataManagerError error) {
                        System.err.println("Failed to get available bookings " + error);
                    }
                }
        );
    }

    @Subscribe
    public void onRequestScheduledBookings(RequestScheduledBookingsEvent event)
    {
        String providerId = event.providerId;
        dataManager.getScheduledBookings(providerId, new DataManager.Callback<List<BookingSummary>>() {
                    @Override
                    public void onSuccess(final List<BookingSummary> bookingSummaries) {
                        onBookingSummariesReceived(bookingSummaries);
                    }

                    @Override
                    public void onError(final DataManager.DataManagerError error) {
                        System.err.println("Failed to get available bookings " + error);
                    }
                }
        );
    }

    private void updateBookingsCache(Booking booking)
    {
        cachedBookings.put(booking.getId(), booking);
    }

    public void onBookingSummariesReceived(final List<BookingSummary> bookingSummaries)
    {
        if(bookingSummaries == null)
        {
            System.err.println("No booking summaries from server");
            return;
        }

        //update the cache
        //send out the relevant cache data to fulfill the request
        System.out.println("Got some booking summaries in : " + bookingSummaries.size());

        //cachedBookingSummaries = new HashMap<BookingCalendarDay, BookingSummary>();

        //extract all of the bookings and update our local cache
        for(BookingSummary bs : bookingSummaries)
        {
            for(Booking b : bs.getBookings())
            {
                updateBookingsCache(b);
            }
        }


        //update the summaries cache

        updateSummariesCache(bookingSummaries);


//        for(BookingSummary bs : bookingSummaries)
//        {
//            BookingCalendarDay bcd = new BookingCalendarDay(bs.getDate());
//            System.out.println("Adding summary for : " + bcd + " : num bookings " + bs.getBookings().size());
//            cachedBookingSummaries.put(bcd, bs);
//        }


        System.out.println("Send out the update event");

        //just passing this through as a test
        bus.post(new BookingsRetrievedEvent(cachedBookingSummaries));





    }

    private void updateSummariesCache(final List<BookingSummary> bookingSummaries)
    {
        cachedBookingSummaries = new HashMap<BookingCalendarDay, BookingSummary>();
        for(BookingSummary bs : bookingSummaries)
        {
            BookingCalendarDay bcd = new BookingCalendarDay(bs.getDate());
            System.out.println("Adding summary for : " + bcd + " : num bookings " + bs.getBookings().size());
            cachedBookingSummaries.put(bcd, bs);
        }
    }


    public void onBookingsReceived()
    {
        //update the cache
        //send out the relevant cache data to fulfill the request





    }

    public void onForceCacheUpdate()
    {

    }

















    public final BookingRequest getCurrentRequest() {
        if (request != null) return request;
        else {
            if ((request = BookingRequest.fromJson(securePrefs.getString("BOOKING_REQ"))) != null)
                request.addObserver(this);
            return request;
        }
    }

    public final void setCurrentRequest(final BookingRequest newRequest) {
        if (request != null) request.deleteObserver(this);

        if (newRequest == null) {
            request = null;
            securePrefs.put("BOOKING_REQ", null);
            return;
        }

        request = newRequest;
        request.addObserver(this);
        securePrefs.put("BOOKING_REQ", request.toJson());
    }

    public final BookingQuote getCurrentQuote() {
        if (quote != null) return quote;
        else {
            if ((quote = BookingQuote.fromJson(securePrefs.getString("BOOKING_QUOTE"))) != null)
                quote.addObserver(this);
            return quote;
        }
    }

    public final void setCurrentQuote(final BookingQuote newQuote) {
        if (quote != null) quote.deleteObserver(this);

        if (newQuote == null) {
            quote = null;
            securePrefs.put("BOOKING_QUOTE", null);
            return;
        }

        quote = newQuote;
        quote.addObserver(this);
        securePrefs.put("BOOKING_QUOTE", quote.toJson());
    }

    public final BookingTransaction getCurrentTransaction() {
        if (transaction != null) return transaction;
        else {
            if ((transaction = BookingTransaction
                    .fromJson(securePrefs.getString("BOOKING_TRANS"))) != null)
                transaction.addObserver(this);
            return transaction;
        }
    }

    public final void setCurrentTransaction(final BookingTransaction newTransaction) {
        if (transaction != null) transaction.deleteObserver(this);

        if (newTransaction == null) {
            transaction = null;
            securePrefs.put("BOOKING_TRANS", null);
            return;
        }

        transaction = newTransaction;
        transaction.addObserver(this);
        securePrefs.put("BOOKING_TRANS", transaction.toJson());
    }

    public final BookingPostInfo getCurrentPostInfo() {
        if (postInfo != null) return postInfo;
        else {
            if ((postInfo = BookingPostInfo
                    .fromJson(securePrefs.getString("BOOKING_POST"))) != null)
                postInfo.addObserver(this);
            return postInfo;
        }
    }

    public final void setCurrentPostInfo(final BookingPostInfo newInfo) {
        if (postInfo != null) postInfo.deleteObserver(this);

        if (newInfo == null) {
            postInfo = null;
            securePrefs.put("BOOKING_POST", null);
            return;
        }

        postInfo = newInfo;
        postInfo.addObserver(this);
        securePrefs.put("BOOKING_POST", postInfo.toJson());
    }

    public final void setPromoTabCoupon(final String code) {
        securePrefs.put("BOOKING_PROMO_TAB_COUPON", code);
    }

    public final String getPromoTabCoupon() {
        return securePrefs.getString("BOOKING_PROMO_TAB_COUPON");
    }

    @Override
    public void update(final Observable observable, final Object data) {
        if (observable instanceof BookingRequest) setCurrentRequest((BookingRequest)observable);
        if (observable instanceof BookingQuote) setCurrentQuote((BookingQuote)observable);

        if (observable instanceof BookingTransaction)
            setCurrentTransaction((BookingTransaction)observable);

        if (observable instanceof BookingPostInfo)
            setCurrentPostInfo((BookingPostInfo)observable);
    }

    public void clear() {
        setCurrentRequest(null);
        setCurrentQuote(null);
        setCurrentTransaction(null);
        setCurrentPostInfo(null);
        securePrefs.put("STATE_BOOKING_CLEANING_EXTRAS_SEL", null);
        //bus.post(new BookingFlowClearedEvent());
    }
    public void clearAll() {
        securePrefs.put("BOOKING_PROMO_TAB_COUPON", null);
        clear();
    }

//    @Subscribe
//    public final void environmentUpdated(final EnvironmentUpdatedEvent event) {
//        if (event.getEnvironment() != event.getPrevEnvironment()) clearAll();
//    }
//
//    @Subscribe
//    public final void userAuthUpdated(final UserLoggedInEvent event) {
//        if (!event.isLoggedIn()) clearAll();
//    }
}
