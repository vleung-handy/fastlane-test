package com.handy.portal.data;

import com.handy.portal.constant.LocationKey;
import com.handy.portal.constant.NoShowKey;
import com.handy.portal.model.Booking;
import com.handy.portal.model.Booking.BookingType;
import com.handy.portal.model.BookingsListWrapper;
import com.handy.portal.model.BookingsWrapper;
import com.handy.portal.model.BookingClaimDetails;
import com.handy.portal.model.ConfigParams;
import com.handy.portal.model.HelpNodeWrapper;
import com.handy.portal.model.LoginDetails;
import com.handy.portal.model.PinRequestDetails;
import com.handy.portal.model.Provider;
import com.handy.portal.model.TermsDetails;
import com.handy.portal.model.TypeSafeMap;
import com.handy.portal.model.UpdateDetails;
import com.handy.portal.retrofit.HandyRetrofitCallback;
import com.handy.portal.retrofit.HandyRetrofitEndpoint;
import com.handy.portal.retrofit.HandyRetrofitService;

import org.json.JSONObject;

import java.util.Date;
import java.util.Map;

import javax.inject.Inject;

import retrofit.mime.TypedInput;

public final class BaseDataManager extends DataManager
{
    private final HandyRetrofitService service;
    private final HandyRetrofitEndpoint endpoint;

    @Inject
    public BaseDataManager(final HandyRetrofitService service, final HandyRetrofitEndpoint endpoint)
    {
        this.service = service;
        this.endpoint = endpoint;
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
    public final void getBookingDetails(String bookingId, BookingType type, final Callback<Booking> cb)
    {
        service.getBookingDetails(bookingId, type.toString().toLowerCase(), new BookingHandyRetroFitCallback(cb));
    }

    @Override
    public final void notifyOnMyWayBooking(String bookingId, TypeSafeMap<LocationKey> locationParams, final Callback<Booking> cb)
    {
        service.notifyOnMyWay(bookingId, locationParams.toStringMap(), new BookingHandyRetroFitCallback(cb));
    }

    @Override
    public final void notifyCheckInBooking(String bookingId, TypeSafeMap<LocationKey> locationParams, final Callback<Booking> cb)
    {
        service.checkIn(bookingId, locationParams.toStringMap(), new BookingHandyRetroFitCallback(cb));
    }

    @Override
    public final void notifyCheckOutBooking(String bookingId, TypeSafeMap<LocationKey> locationParams, final Callback<Booking> cb)
    {
        service.checkOut(bookingId, locationParams.toStringMap(), new BookingHandyRetroFitCallback(cb));
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
    public void checkForTerms(final Callback<TermsDetails> cb)
    {
        service.checkTerms(new TermsDetailsResponseHandyRetroFitCallback(cb));
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
    public void createHelpCase(TypedInput body, final Callback<Void> cb)
    {
        service.createHelpCase(body, new EmptyHandyRetroFitCallback(cb));
    }
    //********End Help Center********
}
