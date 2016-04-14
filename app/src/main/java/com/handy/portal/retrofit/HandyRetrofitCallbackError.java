package com.handy.portal.retrofit;

import com.handy.portal.data.DataManager;

import okhttp3.Response;

public class HandyRetrofitCallbackError extends RuntimeException
{
    public HandyRetrofitCallbackError(DataManager.Callback callback, Response cause)
    {
        super("FAILED request in " + callback.getClass().getName() + " using the following URL: " + cause.request().url());
    }
}
