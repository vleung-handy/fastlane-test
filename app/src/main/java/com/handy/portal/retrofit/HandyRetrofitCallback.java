package com.handy.portal.retrofit;

import com.google.gson.annotations.SerializedName;
import com.handy.portal.data.DataManager;
import com.handy.portal.data.DataManager.DataManagerError;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;


public abstract class HandyRetrofitCallback implements Callback<Response>
{
    protected final DataManager.Callback callback;

    public HandyRetrofitCallback(DataManager.Callback callback)
    {
        this.callback = callback;
    }

    public abstract void success(JSONObject response);

    @Override
    public void onResponse(final Call<Response> call, final retrofit2.Response<Response> response)
    {
        // Error handling first
        if (response != null && !response.isSuccessful() && response.errorBody() != null)
        {
            ResponseBody errorResponseBody = response.errorBody();

//            Crashlytics.logException(new HandyRetrofitCallbackError(callback, errorResponseBody));//only log if not network error

            final DataManagerError err;
            int resp = response.code();

            if (resp >= 400 && resp <= 500)
            {
                if (response.errorBody().contentType().equals(MediaType.parse("application/json; charset=utf-8")))
                {
//                    Converter<RestError> converter =
//                            (Converter<RestError>) GsonConverterFactory.create()
//                                    .responseBodyConverter(RestError.class, RestError.class.getAnnotations(), null);
//                    RestError error = converter.convert(errorResponseBody);
//
//                    RestError restError = (RestError) error(RestError.class);
//                    String[] messages;
//                    if (restError != null)
//                    {
//                        if (restError.message != null)
//                        {
//                            err = new DataManagerError(DataManagerError.Type.CLIENT, restError.message);
//                        }
//                        else if ((messages = restError.messages) != null && messages.length > 0)
//                        {
//                            err = new DataManagerError(DataManagerError.Type.CLIENT, messages[0]);
//                        }
//                        err.setInvalidInputs(restError.invalidInputs);
//                    }
                }
            }
            else if (resp > 500 && resp < 600)
            {
                err = new DataManagerError(DataManagerError.Type.SERVER);
            }
            else
            {
                err = new DataManagerError(DataManagerError.Type.OTHER);
            }

//            callback.onError(err);
            return;
        }

        // Success
        final StringBuilder resp;
        JSONArray objArray = new JSONArray();
        JSONObject obj = new JSONObject();
        Boolean responseIsJSONArray = false;

        try
        {
            final BufferedReader br
                    = new BufferedReader(
                    new InputStreamReader(response.body().body().byteStream()));

            String line;
            resp = new StringBuilder();
            while ((line = br.readLine()) != null) { resp.append(line); }

            if (resp.length() > 0)
            {
                responseIsJSONArray = (resp.charAt(0) == '[');
            }

            if (responseIsJSONArray)
            {
                objArray = new JSONArray(resp.toString());
            }
            else
            {
                obj = new JSONObject(resp.toString());
            }

        }
        catch (Exception e)
        {
            throw new RuntimeException("Unable to parse API response body : " + e);
        }

        if (responseIsJSONArray)
        {
            //if we got an array response convert it back to an object for now
            //maybe we could support callback params with jsonobj or jsonarray?
            try
            {

                for (int i = 0; i < objArray.length(); i++)
                {
                    obj.put(Integer.toString(i), objArray.getJSONObject(i));
                }

            }
            catch (Exception e)
            {
                throw new RuntimeException("Unable to convert JSONArray to JSONObject : " + e);
            }
        }

        if (obj.has("error") && (obj.optBoolean("error") || obj.optInt("error") == 1))
        {
            final DataManagerError err;
            final JSONArray messages = obj.optJSONArray("messages");

            if (messages != null && messages.length() > 0)
            {
                err = new DataManagerError(DataManagerError.Type.CLIENT,
                        messages.isNull(0) ? null : messages.optString(0));
            }
            else
            {
                err = new DataManagerError(DataManagerError.Type.CLIENT);
            }

            final JSONArray invalidInputs = obj.optJSONArray("invalid_inputs");
            final ArrayList<String> inputs = new ArrayList<>();

            if (invalidInputs != null && invalidInputs.length() > 0)
            {
                for (int i = 0; i < invalidInputs.length(); i++)
                { inputs.add(invalidInputs.optString(i, "")); }

                err.setInvalidInputs(inputs.toArray(new String[inputs.size()]));
            }
            if (callback != null)
            {
                callback.onError(err);
            }
        }
        else
        {
            success(obj);
        }
    }

    @Override
    public void onFailure(final Call<Response> call, final Throwable t)
    {
        // Network Error
        if (callback != null)
        {
            callback.onError(new DataManagerError(DataManagerError.Type.NETWORK));
        }
    }

    protected final class RestError
    {
        @SerializedName("message")
        private String message;
        @SerializedName("messages")
        private String[] messages;
        @SerializedName("invalid_inputs")
        private String[] invalidInputs;
    }
}
