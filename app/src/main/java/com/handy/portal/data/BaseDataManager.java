package com.handy.portal.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.handy.portal.R;
import com.handy.portal.core.BookingSummary;
import com.handy.portal.core.BookingSummaryResponse;
import com.handy.portal.core.LoginDetails;
import com.handy.portal.core.LoginManager;
import com.handy.portal.core.PinRequestDetails;
import com.handy.portal.core.TermsDetails;
import com.handy.portal.core.SimpleResponse;
import com.handy.portal.core.UpdateDetails;
import com.handy.portal.core.booking.Booking;
import com.securepreferences.SecurePreferences;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

public final class BaseDataManager extends DataManager
{
    private final HandyRetrofitService service;
    private final HandyRetrofitEndpoint endpoint;
    private final SecurePreferences prefs;
    private final Gson gsonBuilder;

    @Inject
    public BaseDataManager(final HandyRetrofitService service, final HandyRetrofitEndpoint endpoint, final SecurePreferences prefs)
    {
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
    public final void removeBooking(String bookingId, final Callback<Booking> cb)
    {
        service.removeBooking(getProviderId(), bookingId, new HandyRetrofitCallback(cb)
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
                    cb.onError(new DataManagerError(DataManagerError.Type.OTHER));
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
    public final void notifyOnMyWayBooking(String bookingId, final Callback<Booking> cb)
    {
        service.notifyOnMyWay(getProviderId(), bookingId, new HandyRetrofitCallback(cb)
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
                    cb.onError(new DataManagerError(DataManagerError.Type.OTHER));
                }
                cb.onSuccess(booking);
            }
        });
    }

    @Override
    public final void notifyCheckInBooking(String bookingId, final Callback<Booking> cb)
    {
        service.checkIn(getProviderId(), bookingId, new HandyRetrofitCallback(cb)
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
                    cb.onError(new DataManagerError(DataManagerError.Type.OTHER));
                }
                cb.onSuccess(booking);
            }
        });
    }

    @Override
    public final void notifyCheckOutBooking(String bookingId, final Callback<Booking> cb)
    {
        service.checkOut(getProviderId(), bookingId, new HandyRetrofitCallback(cb)
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
                    cb.onError(new DataManagerError(DataManagerError.Type.OTHER));
                }
                cb.onSuccess(booking);
            }
        });
    }

    public enum ArrivalTimeOption
    {
        EARLY_30_MINUTES(R.string.arrival_time_early_30, "-30"),
        EARLY_15_MINUTES(R.string.arrival_time_early_15, "-15"),
        LATE_10_MINUTES(R.string.arrival_time_late_10, "10"),
        LATE_15_MINUTES(R.string.arrival_time_late_15, "15"),
        LATE_30_MINUTES(R.string.arrival_time_late_30, "30"),;

        private String value;
        private int stringId;

        ArrivalTimeOption(int stringId, String value)
        {
            this.stringId = stringId;
            this.value = value;
        }

        public String getValue()
        {
            return value;
        }

        public int getStringId()
        {
            return stringId;
        }
    }

    @Override
    public final void notifyUpdateArrivalTimeBooking(String bookingId, ArrivalTimeOption arrivalTimeOption, final Callback<Booking> cb)
    {
        service.updateArrivalTime(getProviderId(), bookingId, arrivalTimeOption.getValue(), new HandyRetrofitCallback(cb)
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
                    cb.onError(new DataManagerError(DataManagerError.Type.OTHER));
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
    public void checkForTerms(final Callback<TermsDetails> cb)
    {
        service.checkTerms(getProviderId(), new HandyRetrofitCallback(cb)
        {
            @Override
            void success(JSONObject response)
            {
                TermsDetails termsDetails = null;
                try
                {
                    termsDetails = gsonBuilder.fromJson((response.toString()), new TypeToken<TermsDetails>()
                    {
                    }.getType());
                } catch (Exception e)
                {
                    System.err.println("Can not parse TermsDetails " + e);
                }

                cb.onSuccess(termsDetails);
            }
        });
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

    public final void sendVersionInformation(Map<String, String> versionInfo, final Callback<SimpleResponse> cb)
    {
        service.sendVersionInformation(versionInfo, new HandyRetrofitCallback(cb)
        {
            @Override
            void success(JSONObject response)
            {
                SimpleResponse simpleResponse = null;
                try
                {
                    simpleResponse = gsonBuilder.fromJson((response.toString()), new TypeToken<SimpleResponse>()
                    {
                    }.getType());
                } catch (Exception e)
                {
                    System.err.println("Can not parse response " + e);
                }
                cb.onSuccess(simpleResponse);
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
