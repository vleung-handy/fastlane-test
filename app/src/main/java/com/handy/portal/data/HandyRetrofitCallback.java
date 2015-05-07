package com.handy.portal.data;

import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

import retrofit.RetrofitError;
import retrofit.client.Response;

abstract class HandyRetrofitCallback implements retrofit.Callback<Response> {
    private final DataManager.Callback callback;

    HandyRetrofitCallback(DataManager.Callback callback) {
        this.callback = callback;
    }

    abstract void success(JSONObject response);

    @Override
    public final void success(final Response response, final Response raw) {
        final StringBuilder resp;

        JSONArray objArray = new JSONArray();
        JSONObject obj = new JSONObject();
        Boolean responseIsJSONArray = false;
        try {
            final BufferedReader br
                    = new BufferedReader(new InputStreamReader(raw.getBody().in()));

            String line;
            resp = new StringBuilder();
            while ((line = br.readLine()) != null) resp.append(line);

            if(resp.length() > 0) {
                responseIsJSONArray = (resp.charAt(0) == '[');
            }

            if(responseIsJSONArray)
            {
                objArray = new JSONArray(resp.toString());
            }
            else
            {
                obj = new JSONObject(resp.toString());
            }

        } catch (Exception e) {
            throw new RuntimeException("Unable to parse API response body : " + e);
        }

        if(responseIsJSONArray) {
            //if we got an array response convert it back to an object for now
                //maybe we could support callback params with jsonobj or jsonarray?
            try {

                for (int i = 0; i < objArray.length(); i++) {
                    obj.put(Integer.toString(i), objArray.getJSONObject(i));
                }

            } catch (Exception e) {
                throw new RuntimeException("Unable to convert JSONArray to JSONObject : " + e);
            }
        }


        if (obj.has("error") && (obj.optBoolean("error") || obj.optInt("error") == 1)) {
            final DataManager.DataManagerError err;
            final JSONArray messages = obj.optJSONArray("messages");

            if (messages != null && messages.length() > 0) {
                err = new DataManager.DataManagerError(DataManager.Type.CLIENT,
                        messages.isNull(0) ? null : messages.optString(0));
            }
            else {
                err = new DataManager.DataManagerError(DataManager.Type.CLIENT);
            }

            final JSONArray invalidInputs = obj.optJSONArray("invalid_inputs");
            final ArrayList<String> inputs = new ArrayList<>();

            if (invalidInputs != null && invalidInputs.length() > 0) {
                for (int i = 0; i < invalidInputs.length(); i++)
                    inputs.add(invalidInputs.optString(i, ""));

                err.setInvalidInputs(inputs.toArray(new String[inputs.size()]));
            }
            callback.onError(err);
        }
        else success(obj);
    }

    @Override
    public final void failure(final RetrofitError error) {
        if (callback != null) {
            final DataManager.DataManagerError err;
            if (error.isNetworkError()) err = new DataManager.DataManagerError(DataManager.Type.NETWORK);
            else {
                final int resp = error.getResponse().getStatus();
                if (resp >= 400 && resp < 500) {
                    if (error.getResponse().getBody().mimeType().contains("json")) {
                        RestError restError = (RestError)error.getBodyAs(RestError.class);
                        String[] messages;

                        if ((messages = restError.messages) != null && messages.length > 0) {
                            err = new DataManager.DataManagerError(DataManager.Type.CLIENT, messages[0]);
                        }
                        else err = new DataManager.DataManagerError(DataManager.Type.CLIENT);
                        err.setInvalidInputs(restError.invalidInputs);
                    }
                    else err = new DataManager.DataManagerError(DataManager.Type.CLIENT);
                }
                else if (resp >= 500 && resp < 600) {
                    err = new DataManager.DataManagerError(DataManager.Type.SERVER);
                }
                else err = new DataManager.DataManagerError(DataManager.Type.OTHER);
            }
            callback.onError(err);
        }
    }

    private final class RestError {
        @SerializedName("messages") private String[] messages;
        @SerializedName("invalid_inputs") private String[] invalidInputs;
    }
}
