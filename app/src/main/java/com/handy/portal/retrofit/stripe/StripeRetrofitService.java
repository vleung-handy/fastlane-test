package com.handy.portal.retrofit.stripe;

import com.handy.portal.retrofit.HandyRetrofitCallback;

import java.util.Map;

import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface StripeRetrofitService
{
    @FormUrlEncoded
    @POST("/v1/tokens")
    void getStripeToken(@FieldMap Map<String, String> params, HandyRetrofitCallback cb);

    //TODO: will we be needing more stripe services later? if not, reconsider whether this should have its own service

}
