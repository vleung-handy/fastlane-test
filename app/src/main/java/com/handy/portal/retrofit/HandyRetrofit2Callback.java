package com.handy.portal.retrofit;


import android.support.annotation.NonNull;

import com.handy.portal.core.model.ErrorResponse;
import com.handy.portal.data.DataManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class HandyRetrofit2Callback<T extends ErrorResponse> implements Callback<T> {

    public abstract void onSuccess(@NonNull T response);

    public abstract void onError(@NonNull DataManager.DataManagerError error);

    protected boolean areCallbacksEnabled() {
        return true;
    }

    @Override
    public void onResponse(final Call<T> call, final Response<T> response) {
        if (!areCallbacksEnabled()) { return; }
        if (response.body() == null) {
            onError(new DataManager.DataManagerError(DataManager.DataManagerError.Type.SERVER));
        }
        else if (response.body().isError()) {
            T body = response.body();
            DataManager.DataManagerError error = new DataManager.DataManagerError(
                    DataManager.DataManagerError.Type.CLIENT,
                    body.getMessages() != null && body.getMessages().length >= 1
                            ? body.getMessages()[0] : null
            );
            error.setInvalidInputs(body.getInvalidInputs());
            onError(error);
        }
        else {
            onSuccess(response.body());
        }
    }

    @Override
    public void onFailure(final Call<T> call, final Throwable t) {
        if (!areCallbacksEnabled()) { return; }
        onError(new DataManager.DataManagerError(DataManager.DataManagerError.Type.NETWORK));
    }
}
