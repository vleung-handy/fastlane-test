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
import com.handy.portal.model.ProviderProfile;
import com.handy.portal.model.ResupplyInfo;
import com.handy.portal.model.SuccessWrapper;
import com.handy.portal.model.TermsDetailsGroup;
import com.handy.portal.model.TypeSafeMap;
import com.handy.portal.model.UpdateDetails;
import com.handy.portal.model.payments.AnnualPaymentSummaries;
import com.handy.portal.model.payments.CreateDebitCardResponse;
import com.handy.portal.model.payments.PaymentBatches;
import com.handy.portal.model.payments.PaymentFlowResponse;
import com.handy.portal.model.payments.RequiresPaymentInfoUpdate;
import com.handy.portal.model.payments.StripeTokenResponse;
import com.handy.portal.retrofit.HandyRetrofitCallback;
import com.handy.portal.retrofit.HandyRetrofitEndpoint;
import com.handy.portal.retrofit.HandyRetrofitService;
import com.handy.portal.retrofit.stripe.StripeRetrofitService;

import org.json.JSONObject;

import java.util.Date;
import java.util.Map;

import javax.inject.Inject;

import retrofit.mime.TypedInput;

public final class BaseDataManager extends DataManager
{
    private final HandyRetrofitService service;
    private final HandyRetrofitEndpoint endpoint;

    private final StripeRetrofitService stripeService; //TODO: should refactor and move somewhere else?

    @Inject
    public BaseDataManager(final HandyRetrofitService service, final HandyRetrofitEndpoint endpoint,
                           final StripeRetrofitService stripeService)
    {
        this.service = service;
        this.endpoint = endpoint;
        this.stripeService = stripeService;
    }

    @Override
    public String getBaseUrl()
    {
        return endpoint.getBaseUrl();
    }

    @Override
    public final void getAvailableBookings(Date[] dates, final Callback<BookingsListWrapper> cb)
    {
        service.getAvailableBookings(dates, new BookingsListWrapperHandyRetroFitCallback(cb));
    }

    @Override
    public final void getScheduledBookings(Date[] dates, final Callback<BookingsListWrapper> cb)
    {
        service.getScheduledBookings(dates, new BookingsListWrapperHandyRetroFitCallback(cb));
    }

    @Override
    public final void getComplementaryBookings(String bookingId, BookingType type, Callback<BookingsWrapper> cb)
    {
        service.getComplementaryBookings(bookingId, type.toString().toLowerCase(), new BookingsWrapperRetroFitCallback(cb));
    }

    @Override
    public final void claimBooking(String bookingId, BookingType type, final Callback<BookingClaimDetails> cb)
    {
        service.claimBooking(bookingId, type.toString().toLowerCase(), new BookingClaimHandyRetroFitCallback(cb));
    }

    @Override
    public final void removeBooking(String bookingId, BookingType type, final Callback<Booking> cb)
    {
        service.removeBooking(bookingId, type.toString().toLowerCase(), new BookingHandyRetroFitCallback(cb));
    }

    @Override
    public void sendIncomeVerification(String providerId, Callback<SuccessWrapper> cb)
    {
        service.sendIncomeVerification(providerId, new SuccessWrapperRetroFitCallback(cb));
    }

    @Override
    public void getProviderProfile(String providerId, Callback<ProviderProfile> cb)
    {
        service.getProviderProfile(providerId, new ProviderProfileRetrofitCallback(cb));
    }

    @Override
    public void getResupplyKit(String providerId, Callback<ResupplyInfo> cb)
    {
        service.getResupplyKit(providerId, "", new ResupplyInfoRetrofitCallback(cb));
    }

    @Override
    public final void getBookingDetails(String bookingId, BookingType type, final Callback<Booking> cb)
    {
        service.getBookingDetails(bookingId, type.toString().toLowerCase(), new BookingHandyRetroFitCallback(cb));
    }

    @Override
    public final void getPaymentBatches(Date startDate, Date endDate, final Callback<PaymentBatches> cb)
    {
        service.getPaymentBatches(startDate, endDate, new PaymentBatchesRetroFitCallback(cb));
    }

    @Override
    public final void getAnnualPaymentSummaries(final Callback<AnnualPaymentSummaries> cb)
    {
        service.getAnnualPaymentSummaries(new AnnualPaymentSummariesRetroFitCallback(cb));
    }

    @Override
    public void getNeedsToUpdatePaymentInfo(Callback<RequiresPaymentInfoUpdate> cb)
    {
        service.getNeedsToUpdatePaymentInfo(new NeedsToUpdatePaymentInfoRetroFitCallback(cb));
    }

    @Override
    public final void notifyOnMyWayBooking(String bookingId, TypeSafeMap<LocationKey> locationParams, final Callback<Booking> cb)
    {
        service.notifyOnMyWay(bookingId, locationParams.toStringMap(), new BookingHandyRetroFitCallback(cb));
    }

    @Override
    public final void notifyCheckInBooking(String bookingId, boolean isAuto, TypeSafeMap<LocationKey> locationParams, final Callback<Booking> cb)
    {
        service.checkIn(bookingId, isAuto, locationParams.toStringMap(), new BookingHandyRetroFitCallback(cb));
    }

    @Override
    public final void notifyCheckOutBooking(String bookingId, boolean isAuto, TypeSafeMap<LocationKey> locationParams, final Callback<Booking> cb)
    {
        service.checkOut(bookingId, isAuto, locationParams.toStringMap(), new BookingHandyRetroFitCallback(cb));
    }

    @Override
    public final void notifyUpdateArrivalTimeBooking(String bookingId, Booking.ArrivalTimeOption arrivalTimeOption, final Callback<Booking> cb)
    {
        service.updateArrivalTime(bookingId, arrivalTimeOption.getValue(), new BookingHandyRetroFitCallback(cb));
    }

    @Override
    public void reportNoShow(String bookingId, TypeSafeMap<NoShowKey> params, Callback<Booking> cb)
    {
        service.reportNoShow(bookingId, params.toStringMap(), new BookingHandyRetroFitCallback(cb));
    }

    @Override
    public final void requestPinCode(String phoneNumber, final Callback<PinRequestDetails> cb)
    {
        service.requestPinCode(phoneNumber, new PinRequestDetailsResponseHandyRetroFitCallback(cb));
    }

    @Override
    public final void requestLogin(String phoneNumber, String pinCode, final Callback<LoginDetails> cb)
    {
        service.requestLogin(phoneNumber, pinCode, new LoginDetailsResponseHandyRetroFitCallback(cb));
    }

    @Override
    public void getProviderInfo(Callback<Provider> cb)
    {
        service.getProviderInfo(new ProviderResponseHandyRetroFitCallback(cb));
    }

    @Override
    public final void checkForUpdates(String appFlavor, int versionCode, final Callback<UpdateDetails> cb)
    {
        service.checkUpdates(appFlavor, versionCode, new UpdateDetailsResponseHandyRetroFitCallback(cb));
    }

    @Override
    public void checkForAllPendingTerms(final Callback<TermsDetailsGroup> cb)
    {
        service.checkAllPendingTerms(new TermsDetailsGroupResponseHandyRetroFitCallback(cb));
    }

    @Override
    public void acceptTerms(String termsCode, final Callback<Void> cb)
    {
        service.acceptTerms(termsCode, new HandyRetrofitCallback(cb)
        {
            @Override
            public void success(JSONObject response)
            {
                cb.onSuccess(null);
            }
        });
    }

    @Override
    public void getConfigParams(String[] keys, Callback<ConfigParams> cb)
    {
        service.getConfigParams(keys, new ConfigParamResponseHandyRetroFitCallback(cb));
    }

    @Override
    public final void sendVersionInformation(Map<String, String> versionInfo)
    {
        service.sendVersionInformation(versionInfo, new EmptyHandyRetroFitCallback(null));
    }

    //********Help Center********
    @Override
    public void getHelpInfo(String nodeId,
                            String bookingId,
                            final Callback<HelpNodeWrapper> cb)
    {
        service.getHelpInfo(nodeId, bookingId, new HelpNodeResponseHandyRetroFitCallback(cb));
    }

    @Override
    public void getHelpBookingsInfo(String nodeId,
                                    String bookingId,
                                    final Callback<HelpNodeWrapper> cb)
    {
        service.getHelpBookingsInfo(nodeId, bookingId, new HelpNodeResponseHandyRetroFitCallback(cb));
    }

    @Override
    public void getHelpPaymentsInfo(final Callback<HelpNodeWrapper> cb)
    {
        service.getHelpPayments(new HelpNodeResponseHandyRetroFitCallback(cb));
    }

    @Override
    public void createHelpCase(TypedInput body, final Callback<Void> cb)
    {
        service.createHelpCase(body, new EmptyHandyRetroFitCallback(cb));
    }
    //********End Help Center********

    @Override
    public void createBankAccount(Map<String, String> params, final Callback<SuccessWrapper> cb)
    {
        service.createBankAccount(params, new CreateBankAccountRetroFitCallback(cb));
    }

    @Override
    public void createDebitCardRecipient(Map<String, String> params, final Callback<SuccessWrapper> cb)
    {
        service.createDebitCardRecipient(params, new CreateDebitCardRecipientRetroFitCallback(cb));
    }

    @Override
    public void createDebitCardForCharge(String stripeToken, final Callback<CreateDebitCardResponse> cb)
    {
        service.createDebitCardForCharge(stripeToken, new CreateDebitCardRetroFitCallback(cb));
    }

    @Override
    public void getPaymentFlow(String providerId, final Callback<PaymentFlowResponse> cb)
    {
        service.getPaymentFlow(providerId, new GetPaymentFlowRetroFitCallback(cb));
    }

    //Stripe
    @Override
    public void getStripeToken(Map<String, String> params, final Callback<StripeTokenResponse> cb)
    {
        stripeService.getStripeToken(params, new StripeTokenRetroFitCallback(cb));
    }

}
