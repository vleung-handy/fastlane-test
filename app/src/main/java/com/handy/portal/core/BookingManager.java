package com.handy.portal.core;

import com.handy.portal.data.SecurePreferences;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

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

    @Inject
    BookingManager(final Bus bus, final SecurePreferences prefs) {
        this.securePrefs = prefs;
        this.bus = bus;
        this.bus.register(this);
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
