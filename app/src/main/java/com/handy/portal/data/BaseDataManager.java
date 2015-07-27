package com.handy.portal.data;

import com.crashlytics.android.Crashlytics;
import com.handy.portal.constant.LocationKey;
import com.handy.portal.constant.NoShowKey;
import com.handy.portal.constant.PrefsKey;
import com.handy.portal.help.HelpNodeWrapper;
import com.handy.portal.manager.PrefsManager;
import com.handy.portal.model.Booking;
import com.handy.portal.model.BookingSummaryResponse;
import com.handy.portal.model.ConfigParams;
import com.handy.portal.model.LoginDetails;
import com.handy.portal.model.PinRequestDetails;
import com.handy.portal.model.TermsDetails;
import com.handy.portal.model.TypeSafeMap;
import com.handy.portal.model.UpdateDetails;
import com.handy.portal.retrofit.HandyRetrofitCallback;
import com.handy.portal.retrofit.HandyRetrofitEndpoint;
import com.handy.portal.retrofit.HandyRetrofitService;

import org.json.JSONObject;

import java.util.Map;

import javax.inject.Inject;

import retrofit.mime.TypedInput;

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
    public final void notifyOnMyWayBooking(String bookingId, TypeSafeMap<LocationKey> locationParams, final Callback<Booking> cb)
    {
        service.notifyOnMyWay(getUserId(), bookingId, locationParams.toStringMap(), new BookingHandyRetroFitCallback(cb));
    }

    @Override
    public final void notifyCheckInBooking(String bookingId, TypeSafeMap<LocationKey> locationParams, final Callback<Booking> cb)
    {
        service.checkIn(getUserId(), bookingId, locationParams.toStringMap(), new BookingHandyRetroFitCallback(cb));
    }

    @Override
    public final void notifyCheckOutBooking(String bookingId, TypeSafeMap<LocationKey> locationParams, final Callback<Booking> cb)
    {
        service.checkOut(getUserId(), bookingId, locationParams.toStringMap(), new BookingHandyRetroFitCallback(cb));
    }

    @Override
    public final void notifyUpdateArrivalTimeBooking(String bookingId, Booking.ArrivalTimeOption arrivalTimeOption, final Callback<Booking> cb)
    {
        service.updateArrivalTime(getUserId(), bookingId, arrivalTimeOption.getValue(), new BookingHandyRetroFitCallback(cb));
    }

    @Override
    public void reportNoShow(String bookingId, TypeSafeMap<NoShowKey> params, Callback<Booking> cb)
    {
        service.reportNoShow(getUserId(), bookingId, params.toStringMap(), new BookingHandyRetroFitCallback(cb));
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

    private String getUserId()
    {
        String id = prefsManager.getString(PrefsKey.USER_CREDENTIALS_ID, null);
        if (id == null)
        {
            Crashlytics.log("ID not found");
        }
        return id;
    }
}
