package com.handy.portal.data;

import com.handy.portal.constant.LocationKey;
import com.handy.portal.constant.NoShowKey;
import com.handy.portal.model.Booking;
import com.handy.portal.model.Booking.BookingType;
import com.handy.portal.model.BookingClaimDetails;
import com.handy.portal.model.BookingsListWrapper;
import com.handy.portal.model.BookingsWrapper;
import com.handy.portal.model.CheckoutRequest;
import com.handy.portal.model.ConfigurationResponse;
import com.handy.portal.helpcenter.model.HelpNodeWrapper;
import com.handy.portal.model.LoginDetails;
import com.handy.portal.model.PinRequestDetails;
import com.handy.portal.model.Provider;
import com.handy.portal.model.ProviderPersonalInfo;
import com.handy.portal.model.ProviderProfile;
import com.handy.portal.model.ProviderSettings;
import com.handy.portal.model.SuccessWrapper;
import com.handy.portal.model.TermsDetailsGroup;
import com.handy.portal.model.TypeSafeMap;
import com.handy.portal.model.TypedJsonString;
import com.handy.portal.model.UpdateDetails;
import com.handy.portal.model.ZipClusterPolygons;
import com.handy.portal.model.logs.EventLogResponse;
import com.handy.portal.model.notifications.NotificationMessages;
import com.handy.portal.model.payments.AnnualPaymentSummaries;
import com.handy.portal.model.payments.CreateDebitCardResponse;
import com.handy.portal.model.payments.PaymentBatches;
import com.handy.portal.model.payments.PaymentFlow;
import com.handy.portal.model.payments.RequiresPaymentInfoUpdate;
import com.handy.portal.model.payments.StripeTokenResponse;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import retrofit.mime.TypedInput;

public abstract class DataManager
{
    //Portal
    public abstract void checkForUpdates(String appFlavor, int versionCode, Callback<UpdateDetails> cb);

    public abstract void checkForAllPendingTerms(Callback<TermsDetailsGroup> cb);

    public abstract void acceptTerms(String termsCode, Callback<Void> cb);

    public abstract void sendVersionInformation(Map<String, String> info);

    public abstract void getAvailableBookings(Date[] date, Callback<BookingsListWrapper> cb);

    public abstract void getScheduledBookings(Date[] date, Callback<BookingsListWrapper> cb);

    public abstract void getNearbyBookings(int regionId, double latitude, double longitude,
                                           final Callback<BookingsWrapper> cb);

    public abstract void claimBooking(String bookingId, BookingType type, Callback<BookingClaimDetails> cb);

    public abstract void removeBooking(String bookingId, BookingType type, Callback<Booking> cb);

    public abstract void sendIncomeVerification(String providerId, Callback<SuccessWrapper> cb);

    public abstract void getProviderProfile(String providerId, Callback<ProviderProfile> cb);

    public abstract void updateProviderProfile(String providerId, TypeSafeMap<NoShowKey> params, Callback<ProviderPersonalInfo> cb);

    public abstract void getProviderSettings(final String providerId, final Callback<ProviderSettings> callback);

    public abstract void putUpdateProviderSettings(String providerId, ProviderSettings providerSettings, Callback<ProviderSettings> cb);

    public abstract void getResupplyKit(String providerId, Callback<ProviderProfile> callback);

    public abstract void getBookingDetails(String bookingId, BookingType type, Callback<Booking> cb);

    public abstract void notifyOnMyWayBooking(String bookingId, TypeSafeMap<LocationKey> locationParams, Callback<Booking> cb);

    public abstract void notifyCheckInBooking(String bookingId, boolean isAuto, TypeSafeMap<LocationKey> locationParams, Callback<Booking> cb);

    public abstract void notifyCheckOutBooking(String bookingId, boolean isAuto, CheckoutRequest request, Callback<Booking> cb);

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

    public abstract void getHelpPaymentsInfo(final DataManager.Callback<HelpNodeWrapper> cb);

    public abstract void createHelpCase(TypedInput body, final Callback<Void> cb);

    public abstract void getProviderInfo(Callback<Provider> cb);

    public abstract String getBaseUrl();

    public abstract void getComplementaryBookings(String bookingId, BookingType type, Callback<BookingsWrapper> callback);

    public abstract void getPaymentBatches(Date startDate, Date endDate, Callback<PaymentBatches> callback);

    public abstract void getAnnualPaymentSummaries(Callback<AnnualPaymentSummaries> callback);

    public abstract void getNeedsToUpdatePaymentInfo(Callback<RequiresPaymentInfoUpdate> callback);

    public abstract void createBankAccount(Map<String, String> params, Callback<SuccessWrapper> callback);

    public abstract void createDebitCardRecipient(Map<String, String> params, Callback<SuccessWrapper> callback);

    public abstract void createDebitCardForCharge(String stripeToken, Callback<CreateDebitCardResponse> callback);

    public abstract void getPaymentFlow(String providerId, Callback<PaymentFlow> callback);

    public abstract void getZipClusterPolygons(String providerId, final Callback<ZipClusterPolygons> cb);

    public abstract void getStripeToken(Map<String, String> params, Callback<StripeTokenResponse> callback);

    public abstract void postLogs(TypedJsonString params, Callback<EventLogResponse> callback);

    public abstract void getConfiguration(Callback<ConfigurationResponse> callback);

    public abstract void getNotifications(String providerId, Integer sinceId, Integer untilId, Integer count, Callback<NotificationMessages> callback);

    public abstract void postMarkNotificationsAsRead(String providerId, ArrayList<Integer> notificationIds, Callback<NotificationMessages> cb);

    //TODO: refactor. should this be here?
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
