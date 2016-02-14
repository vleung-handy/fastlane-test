package com.handy.portal.retrofit.logevents;

import com.handy.portal.model.logs.EventLogBundle;
import com.handy.portal.retrofit.HandyRetrofitCallback;

import retrofit.http.Body;
import retrofit.http.POST;

public interface EventLogService
{
    @POST("/logevents")
    void postLogs(@Body EventLogBundle eventLogBundle, HandyRetrofitCallback cb);
}
