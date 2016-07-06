package com.handy.portal.retrofit.logevents;

import com.google.gson.JsonObject;
import com.handy.portal.retrofit.HandyRetrofitCallback;

import retrofit.http.Body;
import retrofit.http.POST;

public interface EventLogService
{
    @POST("/logevents")
    void postLogs(@Body JsonObject eventLogBundle, HandyRetrofitCallback cb);
}
