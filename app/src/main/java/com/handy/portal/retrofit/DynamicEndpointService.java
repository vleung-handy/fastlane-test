package com.handy.portal.retrofit;

import retrofit.http.Body;
import retrofit.http.Headers;
import retrofit.http.POST;
import retrofit.mime.TypedFile;

public interface DynamicEndpointService
{
    @Headers("Content-Type: image/jpeg")
    @POST("/")
    void uploadImage(@Body TypedFile file, HandyRetrofitCallback cb);
}
