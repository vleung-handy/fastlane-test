package com.handy.portal.data;

import com.handy.portal.core.BookingSummary;
import com.handy.portal.core.LoginDetails;
import com.handy.portal.core.UpdateDetails;
import com.handy.portal.core.PinRequestDetails;
import com.handy.portal.core.Service;
import com.handy.portal.core.User;
import com.handy.portal.core.booking.Booking;
import com.squareup.otto.Bus;

import java.util.List;

public abstract class DataManager
{
    private final Bus bus;

    DataManager(final Bus bus)
    {
        this.bus = bus;
    }

    public abstract void getServices(CacheResponse<List<Service>> cache, Callback<List<Service>> cb);

    public abstract void authUser(String email, String password, Callback<User> cb);

    public abstract void getUser(String userId, String authToken, Callback<User> cb);

    public abstract void getUser(String email, Callback<String> cb);

    public abstract void updateUser(User user, Callback<User> cb);

    public abstract void checkForUpdates(Callback<UpdateDetails> cb);

    //Portal
    public abstract void getAvailableBookings(Callback<List<BookingSummary>> cb);

    public abstract void getScheduledBookings(Callback<List<BookingSummary>> cb);

    public abstract void claimBooking(String bookingId, Callback<Booking> cb);

    public abstract void getBookingDetails(String bookingId, Callback<Booking> cb);

    //Login
    public abstract void requestPinCode(String phoneNumber, Callback<PinRequestDetails> cb);

    public abstract void requestLogin(String phoneNumber, String pinCode, Callback<LoginDetails> cb);

    public abstract String getBaseUrl();

    public interface Callback<T>
    {
        void onSuccess(T response);

        void onError(DataManagerError error);
    }

    public interface CacheResponse<T>
    {
        void onResponse(T response);
    }

    enum Type
    {
        OTHER, SERVER, CLIENT, NETWORK
    }

    public static final class DataManagerError
    {
        private final Type type;
        private final String message;
        private String[] invalidInputs;

        DataManagerError(final Type type)
        {
            this.type = type;
            this.message = null;
        }

        DataManagerError(final Type type, final String message)
        {
            this.type = type;
            this.message = message;
        }

        final String[] getInvalidInputs()
        {
            return invalidInputs;
        }

        final void setInvalidInputs(final String[] inputs)
        {
            this.invalidInputs = inputs;
        }

        final String getMessage()
        {
            return message;
        }

        final Type getType()
        {
            return type;
        }
    }
}
