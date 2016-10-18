package com.handy.portal.data;

import com.crashlytics.android.Crashlytics;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.handy.portal.bookings.model.Booking;
import com.handy.portal.bookings.model.BookingClaimDetails;
import com.handy.portal.bookings.model.BookingsListWrapper;
import com.handy.portal.bookings.model.BookingsWrapper;
import com.handy.portal.bookings.model.PostCheckoutInfo;
import com.handy.portal.dashboard.model.ProviderEvaluation;
import com.handy.portal.dashboard.model.ProviderFeedback;
import com.handy.portal.dashboard.model.ProviderRating;
import com.handy.portal.location.scheduler.model.LocationScheduleStrategies;
import com.handy.portal.logger.handylogger.model.EventLogResponse;
import com.handy.portal.model.ConfigurationResponse;
import com.handy.portal.model.LoginDetails;
import com.handy.portal.model.PinRequestDetails;
import com.handy.portal.model.Provider;
import com.handy.portal.model.ProviderProfile;
import com.handy.portal.model.ProviderProfileResponse;
import com.handy.portal.model.ProviderSettings;
import com.handy.portal.model.SuccessWrapper;
import com.handy.portal.model.ZipClusterPolygons;
import com.handy.portal.notification.model.NotificationMessages;
import com.handy.portal.onboarding.model.claim.JobClaimResponse;
import com.handy.portal.payments.model.AnnualPaymentSummaries;
import com.handy.portal.payments.model.BookingTransactions;
import com.handy.portal.payments.model.CreateDebitCardResponse;
import com.handy.portal.payments.model.PaymentBatches;
import com.handy.portal.payments.model.PaymentFlow;
import com.handy.portal.payments.model.PaymentOutstandingFees;
import com.handy.portal.payments.model.RequiresPaymentInfoUpdate;
import com.handy.portal.payments.model.StripeTokenResponse;
import com.handy.portal.retrofit.HandyRetrofitCallback;
import com.handy.portal.setup.SetupData;
import com.handy.portal.updater.model.UpdateDetails;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

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
        }
        catch (JsonSyntaxException e)
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


class PostCheckoutInfoHandyRetrofitCallback extends TypedHandyRetrofitCallback<PostCheckoutInfo>
{
    PostCheckoutInfoHandyRetrofitCallback(DataManager.Callback callback)
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


class PaymentOutstandingFeesRetroFitCallback extends TypedHandyRetrofitCallback<PaymentOutstandingFees>
{
    PaymentOutstandingFeesRetroFitCallback(DataManager.Callback callback)
    {
        super(callback);
    }
}


class NeedsToUpdatePaymentInfoRetroFitCallback extends TypedHandyRetrofitCallback<RequiresPaymentInfoUpdate>
{
    NeedsToUpdatePaymentInfoRetroFitCallback(DataManager.Callback callback)
    {
        super(callback);
    }
}


class BookingTransactionsRetroFitCallback extends TypedHandyRetrofitCallback<BookingTransactions>
{
    BookingTransactionsRetroFitCallback(DataManager.Callback callback)
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


class BookingsClaimHandyRetroFitCallback extends TypedHandyRetrofitCallback<JobClaimResponse>
{
    BookingsClaimHandyRetroFitCallback(DataManager.Callback callback)
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


class JobsCountHandyRetroFitCallback extends TypedHandyRetrofitCallback<HashMap<String, Object>>
{
    JobsCountHandyRetroFitCallback(DataManager.Callback callback)
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


class ProviderProfileRetrofitCallback extends TypedHandyRetrofitCallback<ProviderProfile>
{
    ProviderProfileRetrofitCallback(DataManager.Callback callback)
    {
        super(callback);
    }
}


class ProviderPersonalInfoHandyRetroFitCallback extends TypedHandyRetrofitCallback<ProviderProfileResponse>
{
    ProviderPersonalInfoHandyRetroFitCallback(DataManager.Callback callback)
    {
        super(callback);
    }
}


class GetProviderSettingsRetrofitCallback extends TypedHandyRetrofitCallback<ProviderSettings>
{
    GetProviderSettingsRetrofitCallback(final DataManager.Callback callback)
    {
        super(callback);
    }
}


class UpdateProviderSettingsRetroFitCallback extends TypedHandyRetrofitCallback<ProviderSettings>
{

    UpdateProviderSettingsRetroFitCallback(final DataManager.Callback callback)
    {
        super(callback);
    }
}


class ResupplyInfoRetrofitCallback extends TypedHandyRetrofitCallback<ProviderProfile>
{
    ResupplyInfoRetrofitCallback(DataManager.Callback callback)
    {
        super(callback);
    }
}


class StripeTokenRetroFitCallback extends TypedHandyRetrofitCallback<StripeTokenResponse>
{
    StripeTokenRetroFitCallback(DataManager.Callback callback)
    {
        super(callback);
    }
}


class LogEventsRetroFitCallback extends TypedHandyRetrofitCallback<EventLogResponse>
{
    LogEventsRetroFitCallback(DataManager.Callback callback)
    {
        super(callback);
    }
}


class CreateBankAccountRetroFitCallback extends TypedHandyRetrofitCallback<SuccessWrapper>
{
    CreateBankAccountRetroFitCallback(DataManager.Callback callback)
    {
        super(callback);
    }
}


class CreateDebitCardRecipientRetroFitCallback extends TypedHandyRetrofitCallback<SuccessWrapper>
{
    CreateDebitCardRecipientRetroFitCallback(DataManager.Callback callback)
    {
        super(callback);
    }
}


class CreateDebitCardRetroFitCallback extends TypedHandyRetrofitCallback<CreateDebitCardResponse>
{
    CreateDebitCardRetroFitCallback(DataManager.Callback callback)
    {
        super(callback);
    }
}


class GetPaymentFlowRetroFitCallback extends TypedHandyRetrofitCallback<PaymentFlow>
{
    GetPaymentFlowRetroFitCallback(DataManager.Callback callback)
    {
        super(callback);
    }
}


class GetZipClusterPolygonRetroFitCallback extends TypedHandyRetrofitCallback<ZipClusterPolygons>
{
    GetZipClusterPolygonRetroFitCallback(DataManager.Callback callback)
    {
        super(callback);
    }
}


class ConfigurationResponseHandyRetroFitCallback extends TypedHandyRetrofitCallback<ConfigurationResponse>
{
    ConfigurationResponseHandyRetroFitCallback(DataManager.Callback callback)
    {
        super(callback);
    }
}


class NotificationMessagesHandyRetroFitCallback extends TypedHandyRetrofitCallback<NotificationMessages>
{
    NotificationMessagesHandyRetroFitCallback(DataManager.Callback callback)
    {
        super(callback);
    }
}


class NotificationUnreadCountHandyRetroFitCallback extends TypedHandyRetrofitCallback<HashMap<String, Object>>
{
    NotificationUnreadCountHandyRetroFitCallback(DataManager.Callback callback)
    {
        super(callback);
    }
}


class GetProviderEvaluationRetrofitCallback extends TypedHandyRetrofitCallback<ProviderEvaluation>
{
    GetProviderEvaluationRetrofitCallback(DataManager.Callback callback)
    {
        super(callback);
    }
}


class GetProviderFiveStarRatingsRetrofitCallback extends TypedHandyRetrofitCallback<HashMap<String, List<ProviderRating>>>
{
    GetProviderFiveStarRatingsRetrofitCallback(DataManager.Callback callback)
    {
        super(callback);
    }
}


class GetProviderFeedbackRetrofitCallback extends TypedHandyRetrofitCallback<ProviderFeedback>
{
    GetProviderFeedbackRetrofitCallback(DataManager.Callback callback)
    {
        super(callback);
    }
}


class GetLocationScheduleRetrofitCallback extends TypedHandyRetrofitCallback<LocationScheduleStrategies>
{
    GetLocationScheduleRetrofitCallback(DataManager.Callback callback)
    {
        super(callback);
    }
}


class SetupDataRetrofitCallback extends TypedHandyRetrofitCallback<SetupData>
{
    SetupDataRetrofitCallback(DataManager.Callback callback)
    {
        super(callback);
    }
}


class FinishIDVerificationCallback extends TypedHandyRetrofitCallback<HashMap<String, String>>
{
    FinishIDVerificationCallback(final DataManager.Callback callback)
    {
        super(callback);
    }
}


class RequestPhotoUploadUrlCallback extends TypedHandyRetrofitCallback<HashMap<String, String>>
{
    RequestPhotoUploadUrlCallback(final DataManager.Callback callback)
    {
        super(callback);
    }
}
