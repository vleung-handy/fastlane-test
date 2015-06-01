package com.handy.portal.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.handy.portal.core.BookingSummary;
import com.handy.portal.core.BookingSummaryResponse;
import com.handy.portal.core.LoginDetails;
import com.handy.portal.core.LoginManager;
import com.handy.portal.core.PinRequestDetails;
import com.handy.portal.core.UpdateDetails;
import com.handy.portal.core.booking.Booking;
import com.securepreferences.SecurePreferences;
import com.squareup.otto.Bus;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public final class BaseDataManager extends DataManager
{
    private final HandyRetrofitService service;
    private final HandyRetrofitEndpoint endpoint;
    private final SecurePreferences prefs;
    private final Gson gsonBuilder;

    @Inject
    public BaseDataManager(final HandyRetrofitService service, final HandyRetrofitEndpoint endpoint,
                           final Bus bus, final SecurePreferences prefs)
    {
        super(bus);
        this.service = service;
        this.endpoint = endpoint;
        this.prefs = prefs;
        this.gsonBuilder = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();
    }

    @Override
    public String getBaseUrl()
    {
        return endpoint.getBaseUrl();
    }

    @Override
    public final void getAvailableBookings(final Callback<List<BookingSummary>> cb)
    {
        service.getAvailableBookings(getProviderId(), new HandyRetrofitCallback(cb)
        {
            @Override
            void success(final JSONObject response)
            {
                List<BookingSummary> bookings = new ArrayList<BookingSummary>();
                final BookingSummaryResponse summaryResponse;
                try
                {
                    BookingSummaryResponse bsr = gsonBuilder.fromJson(
                            (
                                    response.toString()
                            ),
                            new TypeToken<BookingSummaryResponse>()
                            {
                            }.getType()
                    );
                    bookings = bsr.getBookingSummaries();
                } catch (Exception e)
                {
                    System.err.println("Can not parse BookingSummary " + e);
                }

                cb.onSuccess(bookings);
            }
        });
    }

    @Override
    public final void getScheduledBookings(final Callback<List<BookingSummary>> cb)
    {
        service.getScheduledBookings(getProviderId(), new HandyRetrofitCallback(cb)
        {
            @Override
            void success(final JSONObject response)
            {
                List<BookingSummary> bookings = new ArrayList<BookingSummary>();
                final BookingSummaryResponse summaryResponse;
                try
                {
                    BookingSummaryResponse bsr = gsonBuilder.fromJson(
                            (
                                    response.toString()
                            ),
                            new TypeToken<BookingSummaryResponse>()
                            {
                            }.getType()
                    );
                    bookings = bsr.getBookingSummaries();
                } catch (Exception e)
                {
                    System.err.println("Can not parse BookingSummary " + e);
                }

                cb.onSuccess(bookings);
            }
        });
    }

    @Override
    public final void claimBooking(String bookingId, final Callback<Booking> cb)
    {
        service.claimBooking(getProviderId(), bookingId, new HandyRetrofitCallback(cb)
        {
            @Override
            void success(final JSONObject response)
            {
                Booking booking = null;
                try
                {
                    booking = gsonBuilder.fromJson((response.toString()),
                            new TypeToken<Booking>()
                            {
                            }.getType());
                } catch (Exception e)
                {
                    System.err.println("Can not parse Booking" + e);
                }
                cb.onSuccess(booking);
            }
        });
    }

    @Override
    public final void getBookingDetails(String bookingId, final Callback<Booking> cb)
    {
        service.getBookingDetails(getProviderId(), bookingId, new HandyRetrofitCallback(cb)
        {
            @Override
            void success(final JSONObject response)
            {
                Booking booking = null;
                try
                {
                    booking = gsonBuilder.fromJson((response.toString()),
                            new TypeToken<Booking>()
                            {
                            }.getType());
                } catch (Exception e)
                {
                    System.err.println("Can not parse Booking" + e);
                }
                cb.onSuccess(booking);
            }
        });
    }

    @Override
    public final void requestPinCode(String phoneNumber, final Callback<PinRequestDetails> cb)
    {
        service.requestPinCode(phoneNumber, new HandyRetrofitCallback(cb)
        {
            @Override
            void success(final JSONObject response)
            {
                PinRequestDetails pinRequestDetails = null;
                try
                {
                    pinRequestDetails = gsonBuilder.fromJson((response.toString()),
                            new TypeToken<PinRequestDetails>()
                            {
                            }.getType());
                } catch (Exception e)
                {
                    System.err.println("Can not parse PinRequestDetails " + e);
                }
                cb.onSuccess(pinRequestDetails);
            }
        });
    }

    @Override
    public final void checkForUpdates(String appFlavor, int versionCode, final Callback<UpdateDetails> cb)
    {
        service.checkUpdates(appFlavor, versionCode, new HandyRetrofitCallback(cb)
        {
            @Override
            void success(JSONObject response)
            {
                UpdateDetails updateDetails = null;
                try
                {
                    updateDetails = gsonBuilder.fromJson((response.toString()), new TypeToken<UpdateDetails>()
                    {
                    }.getType());
                } catch (Exception e)
                {
                    System.err.println("Can not parse UpdateDetails " + e);
                }
                cb.onSuccess(updateDetails);
            }
        });
    }

    @Override
    public final void requestLogin(String phoneNumber, String pinCode, final Callback<LoginDetails> cb)
    {
        service.requestLogin(phoneNumber, pinCode, new HandyRetrofitCallback(cb)
        {
            @Override
            void success(final JSONObject response)
            {
                LoginDetails loginDetails = null;
                try
                {
                    loginDetails = gsonBuilder.fromJson((response.toString()),
                            new TypeToken<LoginDetails>()
                            {
                            }.getType());
                } catch (Exception e)
                {
                    System.err.println("Can not parse LoginDetails " + e);
                }
                cb.onSuccess(loginDetails);
            }
        });
    }

    private String getProviderId()
    {
        String id = prefs.getString(LoginManager.USER_CREDENTIALS_ID_KEY, null);
        if (id == null)
        {
            System.err.println("ID not found");
        }
        return id;
    }
}
