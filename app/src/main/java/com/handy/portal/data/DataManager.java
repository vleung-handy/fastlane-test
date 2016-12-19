package com.handy.portal.data;

import com.google.gson.JsonObject;
import com.handy.portal.bookings.model.Booking;
import com.handy.portal.bookings.model.Booking.BookingType;
import com.handy.portal.bookings.model.BookingClaimDetails;
import com.handy.portal.bookings.model.BookingsListWrapper;
import com.handy.portal.bookings.model.BookingsWrapper;
import com.handy.portal.bookings.model.CheckoutRequest;
import com.handy.portal.bookings.model.PostCheckoutInfo;
import com.handy.portal.constant.LocationKey;
import com.handy.portal.constant.ProviderKey;
import com.handy.portal.dashboard.model.ProviderEvaluation;
import com.handy.portal.dashboard.model.ProviderFeedback;
import com.handy.portal.dashboard.model.ProviderRating;
import com.handy.portal.library.util.IDVerificationUtils;
import com.handy.portal.location.model.LocationBatchUpdate;
import com.handy.portal.location.scheduler.model.LocationScheduleStrategies;
import com.handy.portal.logger.handylogger.model.EventLogResponse;
import com.handy.portal.model.ConfigurationResponse;
import com.handy.portal.model.LoginDetails;
import com.handy.portal.model.Provider;
import com.handy.portal.model.ProviderProfile;
import com.handy.portal.model.ProviderProfileResponse;
import com.handy.portal.model.ProviderSettings;
import com.handy.portal.model.SuccessWrapper;
import com.handy.portal.model.TypeSafeMap;
import com.handy.portal.model.ZipClusterPolygons;
import com.handy.portal.notification.model.NotificationMessages;
import com.handy.portal.onboarding.model.claim.JobClaimRequest;
import com.handy.portal.onboarding.model.claim.JobClaimResponse;
import com.handy.portal.payments.model.AnnualPaymentSummaries;
import com.handy.portal.payments.model.BookingTransactions;
import com.handy.portal.payments.model.CreateDebitCardResponse;
import com.handy.portal.payments.model.PaymentBatches;
import com.handy.portal.payments.model.PaymentFlow;
import com.handy.portal.payments.model.PaymentOutstandingFees;
import com.handy.portal.payments.model.RequiresPaymentInfoUpdate;
import com.handy.portal.payments.model.StripeTokenResponse;
import com.handy.portal.retrofit.DynamicEndpoint;
import com.handy.portal.retrofit.DynamicEndpointService;
import com.handy.portal.retrofit.HandyRetrofitCallback;
import com.handy.portal.retrofit.HandyRetrofitEndpoint;
import com.handy.portal.retrofit.HandyRetrofitService;
import com.handy.portal.retrofit.stripe.StripeRetrofitService;
import com.handy.portal.setup.SetupData;
import com.handy.portal.updater.model.UpdateDetails;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import retrofit.mime.TypedFile;

public class DataManager
{
    private final HandyRetrofitService mService;
    private final HandyRetrofitEndpoint mEndpoint;

    private final StripeRetrofitService mStripeService; // should refactor and move somewhere else?
    private final DynamicEndpoint mDynamicEndpoint;
    private final DynamicEndpointService mDynamicEndpointService;

    @Inject
    public DataManager(final HandyRetrofitService service,
                       final HandyRetrofitEndpoint endpoint,
                       final StripeRetrofitService stripeService,
                       final DynamicEndpoint dynamicEndpoint,
                       final DynamicEndpointService dynamicEndpointService)
    {
        mService = service;
        mEndpoint = endpoint;
        mStripeService = stripeService;
        mDynamicEndpoint = dynamicEndpoint;
        mDynamicEndpointService = dynamicEndpointService;
    }

    public void getSetupData(final Callback<SetupData> cb)
    {
        mService.getSetupData(new SetupDataRetrofitCallback(cb));
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

    public void getJobsCount(final List<Date> dates,
                             final Map<String, Object> options,
                             final Callback<HashMap<String, Object>> cb)
    {
        mService.getJobsCount(dates, options, new JobsCountHandyRetroFitCallback(cb));
    }

    public void getAvailableBookings(Date[] dates, Map<String, Object> additionalOptions, final Callback<BookingsListWrapper> cb)
    {

        mService.getAvailableBookings(dates, additionalOptions, new BookingsListWrapperHandyRetroFitCallback(cb));
    }

    public void getOnboardingJobs(final Date startDate,
                                  final ArrayList<String> preferredZipclusterIds,
                                  final Callback<BookingsListWrapper> cb)
    {
        mService.getOnboardingJobs(startDate, preferredZipclusterIds,
                new BookingsListWrapperHandyRetroFitCallback(cb));
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

    public void claimBooking(String bookingId, BookingType type, String claimSwitchJobId, BookingType claimSwitchJobType, final Callback<BookingClaimDetails> cb)
    {
        mService.claimBooking(
                bookingId,
                type.toString().toLowerCase(),
                claimSwitchJobId,
                claimSwitchJobType != null ? claimSwitchJobType.toString().toLowerCase() : null,
                new BookingClaimHandyRetroFitCallback(cb));
    }

    public void claimBookings(JobClaimRequest jobClaimRequest, final Callback<JobClaimResponse> cb)
    {
        mService.claimBookings(jobClaimRequest, new BookingsClaimHandyRetroFitCallback(cb));
    }

    public void removeBooking(String bookingId, BookingType type, final Callback<Booking> cb)
    {
        mService.removeBooking(bookingId, type.toString().toLowerCase(), new BookingHandyRetroFitCallback(cb));
    }

    public void dismissJob(final String bookingId,
                           final BookingType bookingType,
                           final String reasonMachineName,
                           final Callback<Void> cb)
    {
        mService.dismissJob(bookingId, bookingType.toString().toLowerCase(), reasonMachineName,
                new EmptyHandyRetroFitCallback(cb));
    }

    public void sendIncomeVerification(String providerId, Callback<SuccessWrapper> cb)
    {
        mService.sendIncomeVerification(providerId, new SuccessWrapperRetroFitCallback(cb));
    }

    public void getProviderProfile(String providerId, Callback<ProviderProfile> cb)
    {
        mService.getProviderProfile(providerId, new ProviderProfileRetrofitCallback(cb));
    }

    public void updateProviderProfile(String providerId, TypeSafeMap<ProviderKey> params, Callback<ProviderProfileResponse> cb)
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

    public void requestPostCheckoutInfo(final String bookingId, final Callback<PostCheckoutInfo> cb)
    {
        mService.requestPostCheckoutInfo(bookingId, new PostCheckoutInfoHandyRetrofitCallback(cb));
    }

    public void notifyUpdateArrivalTimeBooking(String bookingId, Booking.ArrivalTimeOption arrivalTimeOption, final Callback<Booking> cb)
    {
        mService.updateArrivalTime(bookingId, arrivalTimeOption.getValue(), new BookingHandyRetroFitCallback(cb));
    }

    public void reportNoShow(String bookingId, TypeSafeMap<ProviderKey> params, Callback<Booking> cb)
    {
        mService.reportNoShow(bookingId, params.toStringMap(), new BookingHandyRetroFitCallback(cb));
    }

    public void rateCustomer(String bookingId, int rating, String reviewText, Callback<Void> cb)
    {
        mService.rateCustomer(bookingId, rating, reviewText, new EmptyHandyRetroFitCallback(cb));
    }

    public void requestPinCode(String phoneNumber, final Callback<SuccessWrapper> cb)
    {
        mService.requestPinCode(phoneNumber, new SuccessWrapperRetroFitCallback(cb));
    }

    public void requestSlt(String phoneNumber, final Callback<SuccessWrapper> cb)
    {
        mService.requestSlt(phoneNumber, new SuccessWrapperRetroFitCallback(cb));
    }

    public void requestLoginWithSlt(String n, String sig, String slt, final Callback<LoginDetails> cb)
    {
        mService.requestLoginWithSlt(n, sig, slt, new LoginDetailsResponseHandyRetroFitCallback(cb));
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

    public void postMarkNotificationsAsInteracted(String providerId, ArrayList<Integer> notificationIds, Callback<NotificationMessages> cb)
    {
        mService.postMarkNotificationsAsInteracted(providerId, notificationIds, new NotificationMessagesHandyRetroFitCallback(cb));
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

    public void beforeStartIdVerification(final String beforeIdVerificationStartUrl,
                                          final Callback<HashMap<String, String>> cb)
    {
        mService.beforeStartIdVerification(beforeIdVerificationStartUrl, new HashMap<String, String>(), new FinishIDVerificationCallback(cb));
    }

    public void finishIdVerification(final String afterIdVerificationFinish,
                                     final String scanReference,
                                     @IDVerificationUtils.IdVerificationStatus final String status,
                                     final Callback<HashMap<String, String>> cb)
    {
        mService.finishIdVerification(afterIdVerificationFinish, scanReference,
                status, new FinishIDVerificationCallback(cb));
    }

    public void requestPhotoUploadUrl(final String providerId,
                                      final String imageMimeType,
                                      final Callback<HashMap<String, String>> cb)
    {
        mService.requestPhotoUploadUrl(providerId, imageMimeType,
                new RequestPhotoUploadUrlCallback(cb));
    }

    public void uploadPhoto(final String url,
                            final TypedFile file,
                            final Callback<Void> cb)
    {
        mDynamicEndpoint.setUrl(url);
        mDynamicEndpointService.uploadImage(file, new EmptyHandyRetroFitCallback(cb));
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
