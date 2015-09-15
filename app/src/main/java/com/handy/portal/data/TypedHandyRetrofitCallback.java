package com.handy.portal.data;

import com.crashlytics.android.Crashlytics;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.handy.portal.model.Booking;
import com.handy.portal.model.BookingClaimDetails;
import com.handy.portal.model.BookingsListWrapper;
import com.handy.portal.model.BookingsWrapper;
import com.handy.portal.model.ConfigParams;
import com.handy.portal.model.HelpNodeWrapper;
import com.handy.portal.model.LoginDetails;
import com.handy.portal.model.PinRequestDetails;
import com.handy.portal.model.Provider;
import com.handy.portal.model.SuccessWrapper;
import com.handy.portal.model.TermsDetailsGroup;
import com.handy.portal.model.UpdateDetails;
import com.handy.portal.model.payments.AnnualPaymentSummaries;
import com.handy.portal.model.payments.PaymentBatches;
import com.handy.portal.retrofit.HandyRetrofitCallback;

import org.json.JSONObject;

public abstract class TypedHandyRetrofitCallback<T> extends HandyRetrofitCallback
{
    protected static final Gson gsonBuilder = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();
    protected T returnData;

    TypedHandyRetrofitCallback(DataManager.Callback callback)
    {
        super(callback);
    }

    @Override
    public void success(final JSONObject response)
    {
        try
        {
            TypeToken<T> typeToken = new TypeToken<T>(getClass()) {};
            returnData = gsonBuilder.fromJson((response.toString()), typeToken.getType());
            if (callback != null)
            {
                callback.onSuccess(returnData);
            }
        } catch (JsonSyntaxException e)
        {
            Crashlytics.logException(e);
            callback.onError(new DataManager.DataManagerError(DataManager.DataManagerError.Type.SERVER, e.getMessage()));
        }


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

class PaymentBatchesRetroFitCallback extends TypedHandyRetrofitCallback<PaymentBatches>
{
    PaymentBatchesRetroFitCallback(DataManager.Callback callback)
    {
        super(callback);
    }
}

class AnnualPaymentSummariesRetroFitCallback extends TypedHandyRetrofitCallback<AnnualPaymentSummaries>
{
    AnnualPaymentSummariesRetroFitCallback(DataManager.Callback callback)
    {
        super(callback);
    }
}

class BookingClaimHandyRetroFitCallback extends TypedHandyRetrofitCallback<BookingClaimDetails>
{
    BookingClaimHandyRetroFitCallback(DataManager.Callback callback)
    {
        super(callback);
    }
}

class BookingsListWrapperHandyRetroFitCallback extends TypedHandyRetrofitCallback<BookingsListWrapper>
{
    BookingsListWrapperHandyRetroFitCallback(DataManager.Callback callback)
    {
        super(callback);
    }
}


class BookingsWrapperRetroFitCallback extends TypedHandyRetrofitCallback<BookingsWrapper>
{
    public BookingsWrapperRetroFitCallback(DataManager.Callback<BookingsWrapper> callback)
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

class ProviderResponseHandyRetroFitCallback extends TypedHandyRetrofitCallback<Provider>
{
    ProviderResponseHandyRetroFitCallback(DataManager.Callback callback)
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

class TermsDetailsGroupResponseHandyRetroFitCallback extends TypedHandyRetrofitCallback<TermsDetailsGroup>
{
    TermsDetailsGroupResponseHandyRetroFitCallback(DataManager.Callback callback)
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

class HelpNodeResponseHandyRetroFitCallback extends TypedHandyRetrofitCallback<HelpNodeWrapper>
{
    HelpNodeResponseHandyRetroFitCallback(DataManager.Callback callback)
    {
        super(callback);
    }
}

class EmptyHandyRetroFitCallback extends TypedHandyRetrofitCallback<Void>
{
    EmptyHandyRetroFitCallback(DataManager.Callback callback)
    {
        super(callback);
    }
}

class SuccessWrapperRetroFitCallback extends TypedHandyRetrofitCallback<SuccessWrapper>
{
    SuccessWrapperRetroFitCallback(DataManager.Callback callback)
    {
        super(callback);
    }
}


