package com.handy.portal.data;

import com.handy.portal.constant.PrefsKey;
import com.handy.portal.manager.PrefsManager;
import com.handy.portal.model.Booking;
import com.crashlytics.android.Crashlytics;
import com.handy.portal.model.BookingSummaryResponse;
import com.handy.portal.model.ConfigParams;
import com.handy.portal.model.LoginDetails;
import com.handy.portal.model.PinRequestDetails;
import com.handy.portal.model.SimpleResponse;
import com.handy.portal.model.TermsDetails;
import com.handy.portal.model.UpdateDetails;
import com.handy.portal.retrofit.HandyRetrofitCallback;
import com.handy.portal.retrofit.HandyRetrofitEndpoint;
import com.handy.portal.retrofit.HandyRetrofitService;

import org.json.JSONObject;

import java.util.Map;

import javax.inject.Inject;

public final class BaseDataManager extends DataManager
{
    private final HandyRetrofitService service;
    private final HandyRetrofitEndpoint endpoint;
    private final PrefsManager prefsManager;

    @Inject
    public BaseDataManager(final HandyRetrofitService service, final HandyRetrofitEndpoint endpoint, final PrefsManager prefsManager)
    {
        this.service = service;
        this.endpoint = endpoint;
        this.prefsManager = prefsManager;
    }

    @Override
    public String getBaseUrl()
    {
        return endpoint.getBaseUrl();
    }

    @Override
    public final void getAvailableBookings(final Callback<BookingSummaryResponse> cb)
    {
        service.getAvailableBookings(getUserId(), new BookingSummaryResponseHandyRetroFitCallback(cb));
    }

    @Override
    public final void getScheduledBookings(final Callback<BookingSummaryResponse> cb)
    {
        service.getScheduledBookings(getUserId(), new BookingSummaryResponseHandyRetroFitCallback(cb));
    }

    @Override
    public final void claimBooking(String bookingId, final Callback<Booking> cb)
    {
        service.claimBooking(getUserId(), bookingId, new BookingHandyRetroFitCallback(cb));
    }

    @Override
    public final void removeBooking(String bookingId, final Callback<Booking> cb)
    {
        service.removeBooking(getUserId(), bookingId, new BookingHandyRetroFitCallback(cb));
    }

    @Override
    public final void getBookingDetails(String bookingId, final Callback<Booking> cb)
    {
        service.getBookingDetails(getUserId(), bookingId, new BookingHandyRetroFitCallback(cb));
    }

    @Override
    public final void notifyOnMyWayBooking(String bookingId, Map<String,String> locationParams, final Callback<Booking> cb)
    {
        service.notifyOnMyWay(getUserId(), bookingId, locationParams, new BookingHandyRetroFitCallback(cb));
    }

    @Override
    public final void notifyCheckInBooking(String bookingId, Map<String,String> locationParams, final Callback<Booking> cb)
    {
        service.checkIn(getUserId(), bookingId, locationParams, new BookingHandyRetroFitCallback(cb));
    }

    @Override
    public final void notifyCheckOutBooking(String bookingId, Map<String,String> locationParams, final Callback<Booking> cb)
    {
        service.checkOut(getUserId(), bookingId, locationParams, new BookingHandyRetroFitCallback(cb));
    }

    @Override
    public final void notifyUpdateArrivalTimeBooking(String bookingId, Booking.ArrivalTimeOption arrivalTimeOption, final Callback<Booking> cb)
    {
        service.updateArrivalTime(getUserId(), bookingId, arrivalTimeOption.getValue(), new BookingHandyRetroFitCallback(cb));
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
        service.checkTerms(getUserId(), new TermsDetailsResponseHandyRetroFitCallback(cb));
    }

    @Override
    public void acceptTerms(String termsCode, final Callback<Void> cb)
    {
        service.acceptTerms(getUserId(), termsCode, new HandyRetrofitCallback(cb)
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
        service.getConfigParams(getUserId(), keys, new ConfigParamResponseHandyRetroFitCallback(cb));
    }

    public final void sendVersionInformation(Map<String, String> versionInfo, final Callback<SimpleResponse> cb)
    {
        service.sendVersionInformation(versionInfo, new SimpleResponseHandyRetroFitCallback(cb));
    }

    private String getUserId()
    {
        String id = prefsManager.getString(PrefsKey.USER_CREDENTIALS_ID_KEY);
        if (id.isEmpty())
        {
            Crashlytics.log("ID not found");
        }
        return id;
    }
}
