package com.handy.portal.data;

import com.handy.portal.core.BookingSummaryResponse;
import com.handy.portal.core.LoginDetails;
import com.handy.portal.core.PinRequestDetails;
import com.handy.portal.core.SimpleResponse;
import com.handy.portal.core.TermsDetails;
import com.handy.portal.core.UpdateDetails;
import com.handy.portal.core.booking.Booking;

import java.util.Map;

public abstract class DataManager
{
    //Portal
    public abstract void checkForUpdates(String appFlavor, int versionCode, Callback<UpdateDetails> cb);

    public abstract void checkForTerms(Callback<TermsDetails> cb);

    public abstract void acceptTerms(String termsCode, Callback<Void> cb);

    public abstract void sendVersionInformation(Map<String,String> info, Callback<SimpleResponse> cb);

    public abstract void getAvailableBookings(Callback<BookingSummaryResponse> cb);

    public abstract void getScheduledBookings(Callback<BookingSummaryResponse> cb);

    public abstract void claimBooking(String bookingId, Callback<Booking> cb);

    public abstract void getBookingDetails(String bookingId, Callback<Booking> cb);

    public abstract void removeBooking(String bookingId, Callback<Booking> cb);

    public abstract void notifyOnMyWayBooking(String bookingId, Callback<Booking> cb);

    public abstract void notifyCheckInBooking(String bookingId, Callback<Booking> cb);

    public abstract void notifyCheckOutBooking(String bookingId, Callback<Booking> cb);

    public abstract void notifyUpdateArrivalTimeBooking(String bookingId, Booking.ArrivalTimeOption arrivalTimeOption, Callback<Booking> cb);

    //Login
    public abstract void requestPinCode(String phoneNumber, Callback<PinRequestDetails> cb);

    public abstract void requestLogin(String phoneNumber, String pinCode, Callback<LoginDetails> cb);

    public abstract String getBaseUrl();

    public abstract String getProviderId();

    public interface Callback<T>
    {
        void onSuccess(T response);

        void onError(DataManagerError error);
    }

    public interface CacheResponse<T>
    {
        void onResponse(T response);
    }

    public static class DataManagerError
    {
        public enum Type
        {
            OTHER, SERVER, CLIENT, NETWORK
        }

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

        public final String getMessage()
        {
            return message;
        }

        public final Type getType()
        {
            return type;
        }
    }
}
