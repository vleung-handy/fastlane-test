package com.handy.portal.data;

import com.google.gson.JsonObject;
import com.handy.portal.constant.LocationKey;
import com.handy.portal.constant.NoShowKey;
import com.handy.portal.helpcenter.model.HelpNodeWrapper;
import com.handy.portal.location.model.LocationBatchUpdate;
import com.handy.portal.model.Booking;
import com.handy.portal.model.Booking.BookingType;
import com.handy.portal.model.BookingClaimDetails;
import com.handy.portal.model.BookingsListWrapper;
import com.handy.portal.model.BookingsWrapper;
import com.handy.portal.model.CheckoutRequest;
import com.handy.portal.model.ConfigurationResponse;
import com.handy.portal.model.LoginDetails;
import com.handy.portal.model.PinRequestDetails;
import com.handy.portal.model.Provider;
import com.handy.portal.model.ProviderPersonalInfo;
import com.handy.portal.model.ProviderProfile;
import com.handy.portal.model.ProviderSettings;
import com.handy.portal.model.SuccessWrapper;
import com.handy.portal.model.TermsDetailsGroup;
import com.handy.portal.model.TypeSafeMap;
import com.handy.portal.model.UpdateDetails;
import com.handy.portal.model.ZipClusterPolygons;
import com.handy.portal.model.dashboard.ProviderEvaluation;
import com.handy.portal.model.dashboard.ProviderFeedback;
import com.handy.portal.model.dashboard.ProviderRating;
import com.handy.portal.model.logs.EventLogResponse;
import com.handy.portal.model.notifications.NotificationMessages;
import com.handy.portal.model.payments.AnnualPaymentSummaries;
import com.handy.portal.model.payments.CreateDebitCardResponse;
import com.handy.portal.model.payments.PaymentBatches;
import com.handy.portal.model.payments.PaymentFlow;
import com.handy.portal.model.payments.RequiresPaymentInfoUpdate;
import com.handy.portal.model.payments.StripeTokenResponse;
import com.handy.portal.retrofit.HandyRetrofitCallback;
import com.handy.portal.retrofit.HandyRetrofitEndpoint;
import com.handy.portal.retrofit.HandyRetrofitService;
import com.handy.portal.retrofit.stripe.StripeRetrofitService;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import retrofit.mime.TypedInput;

public class DataManager
{
    private final HandyRetrofitService service;
    private final HandyRetrofitEndpoint endpoint;

    private final StripeRetrofitService stripeService; //TODO: should refactor and move somewhere else?

    @Inject
    public DataManager(final HandyRetrofitService service,
                       final HandyRetrofitEndpoint endpoint,
                       final StripeRetrofitService stripeService)
    {
        this.service = service;
        this.endpoint = endpoint;
        this.stripeService = stripeService;
    }

    public void sendGeolocation(int providerId, LocationBatchUpdate locationBatchUpdate, Callback<SuccessWrapper> cb)
    {
        service.sendGeolocation(providerId, locationBatchUpdate, new SuccessWrapperRetroFitCallback(cb));
    }

    public String getBaseUrl()
    {
        return endpoint.getBaseUrl();
    }

    public void getAvailableBookings(Date[] dates, final Callback<BookingsListWrapper> cb)
    {
        service.getAvailableBookings(dates, new BookingsListWrapperHandyRetroFitCallback(cb));
    }

    public void getScheduledBookings(Date[] dates, final Callback<BookingsListWrapper> cb)
    {
        service.getScheduledBookings(dates, new BookingsListWrapperHandyRetroFitCallback(cb));
    }

    public void getNearbyBookings(
            int regionId, double latitude, double longitude, final Callback<BookingsWrapper> cb)
    {
        service.getNearbyBookings(
                regionId, latitude, longitude, new BookingsWrapperRetroFitCallback(cb));
    }

    public void getComplementaryBookings(String bookingId, BookingType type, Callback<BookingsWrapper> cb)
    {
        service.getComplementaryBookings(bookingId, type.toString().toLowerCase(), new BookingsWrapperRetroFitCallback(cb));
    }

    public void claimBooking(String bookingId, BookingType type, final Callback<BookingClaimDetails> cb)
    {
        service.claimBooking(bookingId, type.toString().toLowerCase(), new BookingClaimHandyRetroFitCallback(cb));
    }

    public void removeBooking(String bookingId, BookingType type, final Callback<Booking> cb)
    {
        service.removeBooking(bookingId, type.toString().toLowerCase(), new BookingHandyRetroFitCallback(cb));
    }

    public void sendIncomeVerification(String providerId, Callback<SuccessWrapper> cb)
    {
        service.sendIncomeVerification(providerId, new SuccessWrapperRetroFitCallback(cb));
    }

    public void getProviderProfile(String providerId, Callback<ProviderProfile> cb)
    {
        service.getProviderProfile(providerId, new ProviderProfileRetrofitCallback(cb));
    }

    public void updateProviderProfile(String providerId, TypeSafeMap<NoShowKey> params, Callback<ProviderPersonalInfo> cb)
    {
        service.updateProviderProfile(providerId, params.toStringMap(), new ProviderPersonalInfoHandyRetroFitCallback(cb));
    }

    public void getProviderSettings(String providerId, Callback<ProviderSettings> cb)
    {
        service.getProviderSettings(providerId, new GetProviderSettingsRetrofitCallback(cb));
    }

    public void putUpdateProviderSettings(String providerId, ProviderSettings providerSettings, Callback<ProviderSettings> cb)
    {
        service.putUpdateProviderSettings(providerId, providerSettings, new UpdateProviderSettingsRetroFitCallback(cb));
    }

    public void getResupplyKit(String providerId, Callback<ProviderProfile> cb)
    {
        service.getResupplyKit(providerId, new ResupplyInfoRetrofitCallback(cb));
    }

    public void getBookingDetails(String bookingId, BookingType type, final Callback<Booking> cb)
    {
        service.getBookingDetails(bookingId, type.toString().toLowerCase(), new BookingHandyRetroFitCallback(cb));
    }

    public void getPaymentBatches(Date startDate, Date endDate, final Callback<PaymentBatches> cb)
    {
        service.getPaymentBatches(startDate, endDate, new PaymentBatchesRetroFitCallback(cb));
    }

    public void getAnnualPaymentSummaries(final Callback<AnnualPaymentSummaries> cb)
    {
        service.getAnnualPaymentSummaries(new AnnualPaymentSummariesRetroFitCallback(cb));
    }

    public void getNeedsToUpdatePaymentInfo(Callback<RequiresPaymentInfoUpdate> cb)
    {
        service.getNeedsToUpdatePaymentInfo(new NeedsToUpdatePaymentInfoRetroFitCallback(cb));
    }

    public void notifyOnMyWayBooking(String bookingId, TypeSafeMap<LocationKey> locationParams, final Callback<Booking> cb)
    {
        service.notifyOnMyWay(bookingId, locationParams.toStringMap(), new BookingHandyRetroFitCallback(cb));
    }

    public void notifyCheckInBooking(String bookingId, boolean isAuto, TypeSafeMap<LocationKey> locationParams, final Callback<Booking> cb)
    {
        service.checkIn(bookingId, isAuto, locationParams.toStringMap(), new BookingHandyRetroFitCallback(cb));
    }

    public void notifyCheckOutBooking(String bookingId, boolean isAuto, CheckoutRequest request, final Callback<Booking> cb)
    {
        service.checkOut(bookingId, isAuto, request, new BookingHandyRetroFitCallback(cb));
    }

    public void notifyUpdateArrivalTimeBooking(String bookingId, Booking.ArrivalTimeOption arrivalTimeOption, final Callback<Booking> cb)
    {
        service.updateArrivalTime(bookingId, arrivalTimeOption.getValue(), new BookingHandyRetroFitCallback(cb));
    }

    public void reportNoShow(String bookingId, TypeSafeMap<NoShowKey> params, Callback<Booking> cb)
    {
        service.reportNoShow(bookingId, params.toStringMap(), new BookingHandyRetroFitCallback(cb));
    }

    public void requestPinCode(String phoneNumber, final Callback<PinRequestDetails> cb)
    {
        service.requestPinCode(phoneNumber, new PinRequestDetailsResponseHandyRetroFitCallback(cb));
    }


    public void requestLogin(String phoneNumber, String pinCode, final Callback<LoginDetails> cb)
    {
        service.requestLogin(phoneNumber, pinCode, new LoginDetailsResponseHandyRetroFitCallback(cb));
    }

    public void getProviderInfo(Callback<Provider> cb)
    {
        service.getProviderInfo(new ProviderResponseHandyRetroFitCallback(cb));
    }

    public void checkForUpdates(String appFlavor, int versionCode, final Callback<UpdateDetails> cb)
    {
        service.checkUpdates(appFlavor, versionCode, new UpdateDetailsResponseHandyRetroFitCallback(cb));
    }

    public void checkForAllPendingTerms(final Callback<TermsDetailsGroup> cb)
    {
        service.checkAllPendingTerms(new TermsDetailsGroupResponseHandyRetroFitCallback(cb));
    }

    public void acceptTerms(String termsCode, final Callback<Void> cb)
    {
        service.acceptTerms(termsCode, new HandyRetrofitCallback(cb)
        {

            public void success(JSONObject response)
            {
                cb.onSuccess(null);
            }
        });
    }

    public void sendVersionInformation(Map<String, String> versionInfo)
    {
        service.sendVersionInformation(versionInfo, new EmptyHandyRetroFitCallback(null));
    }

    //********Help Center********
    public void getHelpInfo(String nodeId,
                            String bookingId,
                            final Callback<HelpNodeWrapper> cb)
    {
        service.getHelpInfo(nodeId, bookingId, new HelpNodeResponseHandyRetroFitCallback(cb));
    }

    public void getHelpBookingsInfo(String nodeId,
                                    String bookingId,
                                    final Callback<HelpNodeWrapper> cb)
    {
        service.getHelpBookingsInfo(nodeId, bookingId, new HelpNodeResponseHandyRetroFitCallback(cb));
    }

    public void getHelpPaymentsInfo(final Callback<HelpNodeWrapper> cb)
    {
        service.getHelpPayments(new HelpNodeResponseHandyRetroFitCallback(cb));
    }

    public void createHelpCase(TypedInput body, final Callback<Void> cb)
    {
        service.createHelpCase(body, new EmptyHandyRetroFitCallback(cb));
    }
    //********End Help Center********

    public void createBankAccount(Map<String, String> params, final Callback<SuccessWrapper> cb)
    {
        service.createBankAccount(params, new CreateBankAccountRetroFitCallback(cb));
    }

    public void createDebitCardRecipient(Map<String, String> params, final Callback<SuccessWrapper> cb)
    {
        service.createDebitCardRecipient(params, new CreateDebitCardRecipientRetroFitCallback(cb));
    }

    public void createDebitCardForCharge(String stripeToken, final Callback<CreateDebitCardResponse> cb)
    {
        service.createDebitCardForCharge(stripeToken, new CreateDebitCardRetroFitCallback(cb));
    }


    public void getPaymentFlow(String providerId, final Callback<PaymentFlow> cb)
    {
        service.getPaymentFlow(providerId, new GetPaymentFlowRetroFitCallback(cb));
    }

    public void getZipClusterPolygons(String providerId, final Callback<ZipClusterPolygons> cb)
    {
        service.getZipClusterPolygon(providerId, new GetZipClusterPolygonRetroFitCallback(cb));
    }

    //Stripe
    public void getStripeToken(Map<String, String> params, final Callback<StripeTokenResponse> cb)
    {
        stripeService.getStripeToken(params, new StripeTokenRetroFitCallback(cb));
    }

    //Eventual replacement for direct access to config params
    public void getConfiguration(final Callback<ConfigurationResponse> cb)
    {
        service.getConfiguration(new ConfigurationResponseHandyRetroFitCallback(cb));
    }

    //Log Events
    public void postLogs(final JsonObject eventLogBundle, final Callback<EventLogResponse> cb)
    {
        service.postLogs(eventLogBundle, new LogEventsRetroFitCallback(cb));
    }

    // Notifications
    public void getNotifications(String providerId, Integer sinceId, Integer untilId, Integer count, Callback<NotificationMessages> cb)
    {
        service.getNotifications(providerId, sinceId, untilId, count, new NotificationMessagesHandyRetroFitCallback(cb));
    }

    public void postMarkNotificationsAsRead(String providerId, ArrayList<Integer> notificationIds, Callback<NotificationMessages> cb)
    {
        service.postMarkNotificationsAsRead(providerId, notificationIds, new NotificationMessagesHandyRetroFitCallback(cb));
    }

    public void getProviderEvaluation(final String providerId, final Callback<ProviderEvaluation> cb)
    {
        service.getProviderEvaluation(providerId, new GetProviderEvaluationRetrofitCallback(cb));
    }

    public void getProviderFiveStarRatings(final String providerId, final String minStar, final Callback<List<ProviderRating>> cb)
    {
        service.getProviderFiveStarRatings(providerId, minStar, new GetProviderFiveStarRatingsRetrofitCallback(cb));
    }

    public void getProviderFeedback(final String providerId, final Callback<List<ProviderFeedback>> cb)
    {
        service.getProviderFeedback(providerId, new GetProviderFeedbackRetrofitCallback(cb));
    }

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

        public void setInvalidInputs(final String[] inputs)
        {
            this.invalidInputs = inputs;
        }

        public String getMessage()
        {
            return message;
        }

        public Type getType()
        {
            return type;
        }
    }
}
