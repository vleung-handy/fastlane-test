package com.handy.portal.retrofit;

import com.handy.portal.data.DataManager;

import retrofit.RetrofitError;

public class HandyRetrofitCallbackError extends RuntimeException
{
    public HandyRetrofitCallbackError(DataManager.Callback callback, RetrofitError cause)
    {
        super("Failed request in " + callback.getClass().getName() + " using the following URL: " + cause.getUrl(), cause);
    }
}
