package com.handy.portal.data;

import com.handy.portal.core.BookingSummaryResponse;
import com.handy.portal.core.LoginDetails;
import com.handy.portal.core.LoginManager;
import com.handy.portal.core.PinRequestDetails;
import com.handy.portal.core.SimpleResponse;
import com.handy.portal.core.TermsDetails;
import com.handy.portal.core.UpdateDetails;
import com.handy.portal.core.booking.Booking;
import com.securepreferences.SecurePreferences;

import org.json.JSONObject;

import java.util.Map;

import javax.inject.Inject;

public final class BaseDataManager extends DataManager
{
    private final HandyRetrofitService service;
    private final HandyRetrofitEndpoint endpoint;
    private final SecurePreferences prefs;

    @Inject
    public BaseDataManager(final HandyRetrofitService service, final HandyRetrofitEndpoint endpoint, final SecurePreferences prefs)
    {
        this.service = service;
        this.endpoint = endpoint;
        this.prefs = prefs;
    }

    @Override
    public String getBaseUrl()
    {
        return endpoint.getBaseUrl();
    }

    @Override
    public final void getAvailableBookings(final Callback<BookingSummaryResponse> cb)
    {
        service.getAvailableBookings(getProviderId(), new BookingSummaryResponseHandyRetroFitCallback(cb));
    }

    @Override
    public final void getScheduledBookings(final Callback<BookingSummaryResponse> cb)
    {
        service.getScheduledBookings(getProviderId(), new BookingSummaryResponseHandyRetroFitCallback(cb));
    }

    @Override
    public final void claimBooking(String bookingId, final Callback<Booking> cb)
    {
        service.claimBooking(getProviderId(), bookingId, new BookingHandyRetroFitCallback(cb));
    }

    @Override
    public final void removeBooking(String bookingId, final Callback<Booking> cb)
    {
        service.removeBooking(getProviderId(), bookingId, new BookingHandyRetroFitCallback(cb));
    }

    @Override
    public final void getBookingDetails(String bookingId, final Callback<Booking> cb)
    {
        service.getBookingDetails(getProviderId(), bookingId, new BookingHandyRetroFitCallback(cb));
    }

    @Override
    public final void notifyOnMyWayBooking(String bookingId, Map<String,String> locationParams, final Callback<Booking> cb)
    {
        service.notifyOnMyWay(getProviderId(), bookingId, locationParams, new BookingHandyRetroFitCallback(cb));
    }

    @Override
    public final void notifyCheckInBooking(String bookingId, Map<String,String> locationParams, final Callback<Booking> cb)
    {
        service.checkIn(getProviderId(), bookingId, locationParams, new BookingHandyRetroFitCallback(cb));
    }

    @Override
    public final void notifyCheckOutBooking(String bookingId, Map<String,String> locationParams, final Callback<Booking> cb)
    {
        service.checkOut(getProviderId(), bookingId, locationParams, new BookingHandyRetroFitCallback(cb));
    }

    @Override
    public final void notifyUpdateArrivalTimeBooking(String bookingId, Booking.ArrivalTimeOption arrivalTimeOption, final Callback<Booking> cb)
    {
        service.updateArrivalTime(getProviderId(), bookingId, arrivalTimeOption.getValue(), new BookingHandyRetroFitCallback(cb));
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
    public final void checkForUpdates(String appFlavor, int versionCode, final Callback<UpdateDetails> cb)
    {
        service.checkUpdates(appFlavor, versionCode, new UpdateDetailsResponseHandyRetroFitCallback(cb));
    }

    @Override
    public void checkForTerms(final Callback<TermsDetails> cb)
    {
        service.checkTerms(getProviderId(), new TermsDetailsResponseHandyRetroFitCallback(cb));
    }

    @Override
    public void acceptTerms(String termsCode, final Callback<Void> cb)
    {
        service.acceptTerms(getProviderId(), termsCode, new HandyRetrofitCallback(cb)
        {
            @Override
            void success(JSONObject response)
            {
                cb.onSuccess(null);
            }
        });
    }

    @Override
    public void getConfigParams(String[] keys, Callback<Map<String, Integer>> cb)
    {
        service.getConfigParams(getProviderId(), keys, new ConfigParamResponseHandyRetroFitCallback(cb));
    }

    public final void sendVersionInformation(Map<String, String> versionInfo, final Callback<SimpleResponse> cb)
    {
        service.sendVersionInformation(versionInfo, new SimpleResponseHandyRetroFitCallback(cb));
    }

    public String getProviderId()
    {
        String id = prefs.getString(LoginManager.USER_CREDENTIALS_ID_KEY, null);
        if (id == null)
        {
            System.err.println("ID not found");
        }
        return id;
    }
}
