package com.handy.portal.data;

import com.google.gson.JsonObject;
import com.handy.portal.bookings.model.Booking;
import com.handy.portal.bookings.model.Booking.BookingType;
import com.handy.portal.bookings.model.BookingClaimDetails;
import com.handy.portal.bookings.model.BookingsListWrapper;
import com.handy.portal.bookings.model.BookingsWrapper;
import com.handy.portal.bookings.model.CheckoutRequest;
import com.handy.portal.constant.LocationKey;
import com.handy.portal.constant.ProviderKey;
import com.handy.portal.location.model.LocationBatchUpdate;
import com.handy.portal.location.scheduler.model.LocationScheduleStrategies;
import com.handy.portal.logger.handylogger.model.EventLogResponse;
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
import com.handy.portal.model.ZipClusterPolygons;
import com.handy.portal.model.dashboard.ProviderEvaluation;
import com.handy.portal.model.dashboard.ProviderFeedback;
import com.handy.portal.model.dashboard.ProviderRating;
import com.handy.portal.notification.model.NotificationMessages;
import com.handy.portal.onboarding.model.JobClaimRequest;
import com.handy.portal.onboarding.model.JobClaimResponse;
import com.handy.portal.payments.model.AnnualPaymentSummaries;
import com.handy.portal.payments.model.BookingTransactions;
import com.handy.portal.payments.model.CreateDebitCardResponse;
import com.handy.portal.payments.model.PaymentBatches;
import com.handy.portal.payments.model.PaymentFlow;
import com.handy.portal.payments.model.PaymentOutstandingFees;
import com.handy.portal.payments.model.RequiresPaymentInfoUpdate;
import com.handy.portal.payments.model.StripeTokenResponse;
import com.handy.portal.retrofit.HandyRetrofitCallback;
import com.handy.portal.retrofit.HandyRetrofitEndpoint;
import com.handy.portal.retrofit.HandyRetrofitService;
import com.handy.portal.retrofit.stripe.StripeRetrofitService;
import com.handy.portal.updater.model.UpdateDetails;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

public class DataManager
{
    private final HandyRetrofitService mService;
    private final HandyRetrofitEndpoint mEndpoint;

    private final StripeRetrofitService mStripeService; // should refactor and move somewhere else?

    @Inject
    public DataManager(final HandyRetrofitService service,
                       final HandyRetrofitEndpoint endpoint,
                       final StripeRetrofitService stripeService)
    {
        mService = service;
        mEndpoint = endpoint;
        mStripeService = stripeService;
    }

    public void getLocationStrategies(String providerId, Callback<LocationScheduleStrategies> cb)
    {
        mService.getLocationStrategies(providerId, new GetLocationScheduleRetrofitCallback(cb));
    }

    public void sendGeolocation(String providerId, LocationBatchUpdate locationBatchUpdate, Callback<SuccessWrapper> cb)
    {
        mService.sendGeolocation(providerId, locationBatchUpdate, new SuccessWrapperRetroFitCallback(cb));
    }

    public String getBaseUrl()
    {
        return mEndpoint.getBaseUrl();
    }

    public void getAvailableBookings(Date[] dates, final Callback<BookingsListWrapper> cb)
    {
        mService.getAvailableBookings(dates, new BookingsListWrapperHandyRetroFitCallback(cb));
    }

    public void getOnboardingJobs(final Callback<BookingsListWrapper> cb)
    {
        mService.getOnboardingJobs(new BookingsListWrapperHandyRetroFitCallback(cb));
    }

    public void getScheduledBookings(Date[] dates, final Callback<BookingsListWrapper> cb)
    {
        mService.getScheduledBookings(dates, new BookingsListWrapperHandyRetroFitCallback(cb));
    }

    public void getNearbyBookings(
            int regionId, double latitude, double longitude, final Callback<BookingsWrapper> cb)
    {
        mService.getNearbyBookings(
                regionId, latitude, longitude, new BookingsWrapperRetroFitCallback(cb));
    }

    public void getComplementaryBookings(String bookingId, BookingType type, Callback<BookingsWrapper> cb)
    {
        mService.getComplementaryBookings(bookingId, type.toString().toLowerCase(), new BookingsWrapperRetroFitCallback(cb));
    }

    public void claimBooking(String bookingId, BookingType type, final Callback<BookingClaimDetails> cb)
    {
        mService.claimBooking(bookingId, type.toString().toLowerCase(), new BookingClaimHandyRetroFitCallback(cb));
    }

    public void claimBookings(JobClaimRequest jobClaimRequest, final Callback<JobClaimResponse> cb)
    {
        mService.claimBookings(jobClaimRequest, new BookingsClaimHandyRetroFitCallback(cb));
    }

    public void removeBooking(String bookingId, BookingType type, final Callback<Booking> cb)
    {
        mService.removeBooking(bookingId, type.toString().toLowerCase(), new BookingHandyRetroFitCallback(cb));
    }

    public void sendIncomeVerification(String providerId, Callback<SuccessWrapper> cb)
    {
        mService.sendIncomeVerification(providerId, new SuccessWrapperRetroFitCallback(cb));
    }

    public void getProviderProfile(String providerId, Callback<ProviderProfile> cb)
    {
        mService.getProviderProfile(providerId, new ProviderProfileRetrofitCallback(cb));
    }

    public void updateProviderProfile(String providerId, TypeSafeMap<ProviderKey> params, Callback<ProviderPersonalInfo> cb)
    {
        mService.updateProviderProfile(providerId, params.toStringMap(), new ProviderPersonalInfoHandyRetroFitCallback(cb));
    }

    public void getProviderSettings(String providerId, Callback<ProviderSettings> cb)
    {
        mService.getProviderSettings(providerId, new GetProviderSettingsRetrofitCallback(cb));
    }

    public void putUpdateProviderSettings(String providerId, ProviderSettings providerSettings, Callback<ProviderSettings> cb)
    {
        mService.putUpdateProviderSettings(providerId, providerSettings, new UpdateProviderSettingsRetroFitCallback(cb));
    }

    public void getResupplyKit(String providerId, Callback<ProviderProfile> cb)
    {
        mService.getResupplyKit(providerId, new ResupplyInfoRetrofitCallback(cb));
    }

    public void getBookingDetails(String bookingId, BookingType type, final Callback<Booking> cb)
    {
        mService.getBookingDetails(bookingId, type.toString().toLowerCase(), new BookingHandyRetroFitCallback(cb));
    }

    public void getPaymentBatches(Date startDate, Date endDate, final Callback<PaymentBatches> cb)
    {
        mService.getPaymentBatches(startDate, endDate, new PaymentBatchesRetroFitCallback(cb));
    }

    public void getAnnualPaymentSummaries(final Callback<AnnualPaymentSummaries> cb)
    {
        mService.getAnnualPaymentSummaries(new AnnualPaymentSummariesRetroFitCallback(cb));
    }

    public void getPaymentOutstandingFees(final Callback<PaymentOutstandingFees> cb)
    {
        mService.getPaymentOutstandingFees(new PaymentOutstandingFeesRetroFitCallback(cb));
    }

    public void getNeedsToUpdatePaymentInfo(Callback<RequiresPaymentInfoUpdate> cb)
    {
        mService.getNeedsToUpdatePaymentInfo(new NeedsToUpdatePaymentInfoRetroFitCallback(cb));
    }

    public void getBookingTransactions(String bookingId, String bookingType, Callback<BookingTransactions> cb)
    {
        mService.getBookingTransactions(bookingId, bookingType, new BookingTransactionsRetroFitCallback(cb));
    }

    public void notifyOnMyWayBooking(String bookingId, TypeSafeMap<LocationKey> locationParams, final Callback<Booking> cb)
    {
        mService.notifyOnMyWay(bookingId, locationParams.toStringMap(), new BookingHandyRetroFitCallback(cb));
    }

    public void notifyCheckInBooking(String bookingId, TypeSafeMap<LocationKey> locationParams, final Callback<Booking> cb)
    {
        mService.checkIn(bookingId, locationParams.toStringMap(), new BookingHandyRetroFitCallback(cb));
    }

    public void notifyCheckOutBooking(String bookingId, CheckoutRequest request, final Callback<Booking> cb)
    {
        mService.checkOut(bookingId, request, new BookingHandyRetroFitCallback(cb));
    }

    public void notifyUpdateArrivalTimeBooking(String bookingId, Booking.ArrivalTimeOption arrivalTimeOption, final Callback<Booking> cb)
    {
        mService.updateArrivalTime(bookingId, arrivalTimeOption.getValue(), new BookingHandyRetroFitCallback(cb));
    }

    public void reportNoShow(String bookingId, TypeSafeMap<ProviderKey> params, Callback<Booking> cb)
    {
        mService.reportNoShow(bookingId, params.toStringMap(), new BookingHandyRetroFitCallback(cb));
    }

    public void requestPinCode(String phoneNumber, final Callback<PinRequestDetails> cb)
    {
        mService.requestPinCode(phoneNumber, new PinRequestDetailsResponseHandyRetroFitCallback(cb));
    }

    public void requestLogin(String phoneNumber, String pinCode, final Callback<LoginDetails> cb)
    {
        mService.requestLogin(phoneNumber, pinCode, new LoginDetailsResponseHandyRetroFitCallback(cb));
    }

    public void getProviderInfo(Callback<Provider> cb)
    {
        mService.getProviderInfo(new ProviderResponseHandyRetroFitCallback(cb));
    }

    public void checkForUpdates(String appFlavor, int versionCode, final Callback<UpdateDetails> cb)
    {
        mService.checkUpdates(appFlavor, versionCode, new UpdateDetailsResponseHandyRetroFitCallback(cb));
    }

    public void checkForAllPendingTerms(final Callback<TermsDetailsGroup> cb)
    {
        mService.checkAllPendingTerms(new TermsDetailsGroupResponseHandyRetroFitCallback(cb));
    }

    public void acceptTerms(String termsCode, final Callback<Void> cb)
    {
        mService.acceptTerms(termsCode, new HandyRetrofitCallback(cb)
        {

            public void success(JSONObject response)
            {
                cb.onSuccess(null);
            }
        });
    }

    public void createBankAccount(Map<String, String> params, final Callback<SuccessWrapper> cb)
    {
        mService.createBankAccount(params, new CreateBankAccountRetroFitCallback(cb));
    }

    public void createDebitCardRecipient(Map<String, String> params, final Callback<SuccessWrapper> cb)
    {
        mService.createDebitCardRecipient(params, new CreateDebitCardRecipientRetroFitCallback(cb));
    }

    public void createDebitCardForCharge(String stripeToken, final Callback<CreateDebitCardResponse> cb)
    {
        mService.createDebitCardForCharge(stripeToken, new CreateDebitCardRetroFitCallback(cb));
    }

    public void updateCreditCard(final String token, final Callback<SuccessWrapper> cb)
    {
        mService.updateCreditCard(token, new CreateBankAccountRetroFitCallback(cb));
    }

    public void getPaymentFlow(String providerId, final Callback<PaymentFlow> cb)
    {
        mService.getPaymentFlow(providerId, new GetPaymentFlowRetroFitCallback(cb));
    }

    public void getZipClusterPolygons(String providerId, final Callback<ZipClusterPolygons> cb)
    {
        mService.getZipClusterPolygon(providerId, new GetZipClusterPolygonRetroFitCallback(cb));
    }

    //Stripe
    public void getStripeToken(Map<String, String> params, final Callback<StripeTokenResponse> cb)
    {
        mStripeService.getStripeToken(params, new StripeTokenRetroFitCallback(cb));
    }

    //Eventual replacement for direct access to config params
    public void getConfiguration(final Callback<ConfigurationResponse> cb)
    {
        mService.getConfiguration(new ConfigurationResponseHandyRetroFitCallback(cb));
    }

    //Log Events
    public void postLogs(final JsonObject eventLogBundle, final Callback<EventLogResponse> cb)
    {
        mService.postLogs(eventLogBundle, new LogEventsRetroFitCallback(cb));
    }

    // Notifications
    public void getNotifications(String providerId, Integer sinceId, Integer untilId, Integer count, Callback<NotificationMessages> cb)
    {
        mService.getNotifications(providerId, sinceId, untilId, count, new NotificationMessagesHandyRetroFitCallback(cb));
    }

    public void postMarkNotificationsAsRead(String providerId, ArrayList<Integer> notificationIds, Callback<NotificationMessages> cb)
    {
        mService.postMarkNotificationsAsRead(providerId, notificationIds, new NotificationMessagesHandyRetroFitCallback(cb));
    }

    public void getNotificationsUnreadCount(final String providerId, final Callback<HashMap<String, Object>> cb)
    {
        mService.getNotificationsUnreadCount(providerId, new NotificationUnreadCountHandyRetroFitCallback(cb));
    }

    public void getProviderEvaluation(final String providerId, final Callback<ProviderEvaluation> cb)
    {
        mService.getProviderEvaluation(providerId, new GetProviderEvaluationRetrofitCallback(cb));
    }

    public void getProviderFiveStarRatings(final String providerId, final Integer minStar, final String untilBookingDate, final String sinceBookingDate, final Callback<HashMap<String, List<ProviderRating>>> cb)
    {
        mService.getProviderFiveStarRatings(providerId, minStar, untilBookingDate, sinceBookingDate, new GetProviderFiveStarRatingsRetrofitCallback(cb));
    }

    public void getProviderFeedback(final String providerId, final Callback<List<ProviderFeedback>> cb)
    {
        mService.getProviderFeedback(providerId, new GetProviderFeedbackRetrofitCallback(cb));
    }

    public void requestOnboardingSupplies(final String providerId,
                                          final boolean value,
                                          final Callback<SuccessWrapper> cb)
    {
        mService.requestOnboardingSupplies(providerId, value,
                new SuccessWrapperRetroFitCallback(cb));
    }


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
