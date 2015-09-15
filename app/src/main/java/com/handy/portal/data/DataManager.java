package com.handy.portal.data;

import com.handy.portal.constant.LocationKey;
import com.handy.portal.constant.NoShowKey;
import com.handy.portal.model.Booking;
import com.handy.portal.model.Booking.BookingType;
import com.handy.portal.model.BookingClaimDetails;
import com.handy.portal.model.BookingsListWrapper;
import com.handy.portal.model.BookingsWrapper;
import com.handy.portal.model.ConfigParams;
import com.handy.portal.model.HelpNodeWrapper;
import com.handy.portal.model.LoginDetails;
import com.handy.portal.model.PinRequestDetails;
import com.handy.portal.model.Provider;
import com.handy.portal.model.SuccessWrapper;
import com.handy.portal.model.TermsDetailsGroup;
import com.handy.portal.model.TypeSafeMap;
import com.handy.portal.model.UpdateDetails;
import com.handy.portal.model.payments.AnnualPaymentSummaries;
import com.handy.portal.model.payments.PaymentBatches;

import java.util.Date;
import java.util.Map;

import retrofit.mime.TypedInput;

public abstract class DataManager
{
    //Portal
    public abstract void checkForUpdates(String appFlavor, int versionCode, Callback<UpdateDetails> cb);

    public abstract void checkForAllPendingTerms(Callback<TermsDetailsGroup> cb);

    public abstract void acceptTerms(String termsCode, Callback<Void> cb);

    public abstract void getConfigParams(String[] keys, Callback<ConfigParams> cb);

    public abstract void sendVersionInformation(Map<String, String> info);

    public abstract void getAvailableBookings(Date[] date, Callback<BookingsListWrapper> cb);

    public abstract void getScheduledBookings(Date[] date, Callback<BookingsListWrapper> cb);

    public abstract void claimBooking(String bookingId, BookingType type, Callback<BookingClaimDetails> cb);

    public abstract void removeBooking(String bookingId, BookingType type, Callback<Booking> cb);

    public abstract void getSendIncomeVerification(String providerId, Callback<SuccessWrapper> cb);

    public abstract void getBookingDetails(String bookingId, BookingType type, Callback<Booking> cb);

    public abstract void notifyOnMyWayBooking(String bookingId, TypeSafeMap<LocationKey> locationParams, Callback<Booking> cb);

    public abstract void notifyCheckInBooking(String bookingId, TypeSafeMap<LocationKey> locationParams, Callback<Booking> cb);

    public abstract void notifyCheckOutBooking(String bookingId, TypeSafeMap<LocationKey> locationParams, Callback<Booking> cb);

    public abstract void notifyUpdateArrivalTimeBooking(String bookingId, Booking.ArrivalTimeOption arrivalTimeOption, Callback<Booking> cb);

    public abstract void reportNoShow(String bookingId, TypeSafeMap<NoShowKey> params, Callback<Booking> cb);

    //Login
    public abstract void requestPinCode(String phoneNumber, Callback<PinRequestDetails> cb);

    public abstract void requestLogin(String phoneNumber, String pinCode, Callback<LoginDetails> cb);

    public abstract void getHelpInfo(String nodeId,
                                     String bookingId,
                                     final DataManager.Callback<HelpNodeWrapper> cb);

    public abstract void getHelpBookingsInfo(String nodeId,
                                             String bookingId,
                                             final DataManager.Callback<HelpNodeWrapper> cb);

    public abstract void createHelpCase(TypedInput body, final Callback<Void> cb);

    public abstract void getProviderInfo(Callback<Provider> cb);

    public abstract String getBaseUrl();

    public abstract void getComplementaryBookings(String bookingId, BookingType type, Callback<BookingsWrapper> callback);

    public abstract void getPaymentBatches(Date startDate, Date endDate, Callback<PaymentBatches> callback);

    public abstract void getAnnualPaymentSummaries(Callback<AnnualPaymentSummaries> callback);

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

        public DataManagerError(final Type type)
        {
            this.type = type;
            this.message = null;
        }

        public DataManagerError(final Type type, final String message)
        {
            this.type = type;
            this.message = message;
        }

        final String[] getInvalidInputs()
        {
            return invalidInputs;
        }

        public final void setInvalidInputs(final String[] inputs)
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
