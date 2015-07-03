package com.handy.portal.data;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.handy.portal.core.BookingSummaryResponse;
import com.handy.portal.core.ConfigParams;
import com.handy.portal.core.LoginDetails;
import com.handy.portal.core.PinRequestDetails;
import com.handy.portal.core.SimpleResponse;
import com.handy.portal.core.TermsDetails;
import com.handy.portal.core.UpdateDetails;
import com.handy.portal.core.booking.Booking;

import org.json.JSONObject;

class TypedHandyRetrofitCallback<T> extends HandyRetrofitCallback
{
    protected static final Gson gsonBuilder = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();
    protected T returnData;

    TypedHandyRetrofitCallback(DataManager.Callback callback)
    {
        super(callback);
    }

    @Override
    void success(final JSONObject response)
    {
        try
        {
            TypeToken<T> typeToken = new TypeToken<T>(getClass()) {};
            returnData = gsonBuilder.fromJson((response.toString()), typeToken.getType());

        } catch (JsonSyntaxException e)
        {
            System.err.println("Can not parse " + e);
        }
        callback.onSuccess(returnData);
    }
}

//We need to trick the compiler into holding onto the generic type so we don't lose it to erasure
class BookingHandyRetroFitCallback extends TypedHandyRetrofitCallback<Booking>
{
    BookingHandyRetroFitCallback(DataManager.Callback callback)
    {
        super(callback);
    }
}

class BookingSummaryResponseHandyRetroFitCallback extends TypedHandyRetrofitCallback<BookingSummaryResponse>
{
    BookingSummaryResponseHandyRetroFitCallback(DataManager.Callback callback)
    {
        super(callback);
    }
}

class PinRequestDetailsResponseHandyRetroFitCallback extends TypedHandyRetrofitCallback<PinRequestDetails>
{
    PinRequestDetailsResponseHandyRetroFitCallback(DataManager.Callback callback)
    {
        super(callback);
    }
}

class LoginDetailsResponseHandyRetroFitCallback extends TypedHandyRetrofitCallback<LoginDetails>
{
    LoginDetailsResponseHandyRetroFitCallback(DataManager.Callback callback)
    {
        super(callback);
    }
}

class UpdateDetailsResponseHandyRetroFitCallback extends TypedHandyRetrofitCallback<UpdateDetails>
{
    UpdateDetailsResponseHandyRetroFitCallback(DataManager.Callback callback)
    {
        super(callback);
    }
}

class TermsDetailsResponseHandyRetroFitCallback extends TypedHandyRetrofitCallback<TermsDetails>
{
    TermsDetailsResponseHandyRetroFitCallback(DataManager.Callback callback)
    {
        super(callback);
    }
}

class ConfigParamResponseHandyRetroFitCallback extends TypedHandyRetrofitCallback<ConfigParams>
{
    ConfigParamResponseHandyRetroFitCallback(DataManager.Callback callback)
    {
        super(callback);
    }
}

class SimpleResponseHandyRetroFitCallback extends TypedHandyRetrofitCallback<SimpleResponse>
{
    SimpleResponseHandyRetroFitCallback(DataManager.Callback callback)
    {
        super(callback);
    }
}
