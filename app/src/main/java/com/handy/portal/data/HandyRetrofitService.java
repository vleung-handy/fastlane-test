package com.handy.portal.data;

import java.util.Map;

import retrofit.http.FieldMap;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Part;
import retrofit.http.Path;
import retrofit.http.Query;

public interface HandyRetrofitService
{
    //***************
    //PORTAL
    //***************

  /*
    GET     /api/portal/provider/:id/jobs(.:format) # available jobs
    GET     /api/portal/provider/:id/schedule(.:format) # provider's schedule
    POST   /api/portal/provider/:provider_id/bookings/:id/claim(.:format) # claim a booking
    GET     /api/portal/provider/:provider_id/bookings/:id(.:format) # booking details

    http://localhost:3000/api/portal/provider/8/jobs?apiver=1

    claim_api_portal_provider_booking POST      /api/portal/provider/:provider_id/bookings/:id/claim(.:format)                                           api/portal/v1/providers#claim {:format=>:json}
    on_my_way_api_portal_provider_booking POST  /api/portal/provider/:provider_id/bookings/:id/on_my_way(.:format)                                       api/portal/v1/providers#on_my_way {:format=>:json}
     check_in_api_portal_provider_booking POST  /api/portal/provider/:provider_id/bookings/:id/check_in(.:format)                                        api/portal/v1/providers#check_in {:format=>:json}
    check_out_api_portal_provider_booking POST  /api/portal/provider/:provider_id/bookings/:id/check_out(.:format)                                       api/portal/v1/providers#check_out {:format=>:json}
  */

    //http://localhost:3000/api/portal/v1/providers/8/bookings                  //scheduled
    //http://localhost:3000/api/portal/v1/providers/8/bookings?available=true   //available

    String PROVIDERS_PATH = "/providers/";
    String SESSIONS_PATH = "/sessions/";

    @GET("/check_updates")
    void checkUpdates(@Query("app_flavor") String appFlavor, @Query("version_code") int versionCode, HandyRetrofitCallback cb);

    @GET(PROVIDERS_PATH + "{provider_id}/check_terms")
    void checkTerms(@Path("provider_id") String providerId, HandyRetrofitCallback cb);

    @Multipart
    @POST(PROVIDERS_PATH + "{provider_id}/accept_terms")
    void acceptTerms(@Path("provider_id") String providerId, @Part("code") String termsCode, HandyRetrofitCallback handyRetrofitCallback);

    @FormUrlEncoded
    @POST("/send_version_info")
    void sendVersionInformation(@FieldMap Map<String,String> params, HandyRetrofitCallback cb);

    @GET(PROVIDERS_PATH + "{provider_id}/bookings?available=true")
    void getAvailableBookings(@Path("provider_id") String providerId, HandyRetrofitCallback cb);

    @GET(PROVIDERS_PATH + "{provider_id}/bookings")
    void getScheduledBookings(@Path("provider_id") String providerId, HandyRetrofitCallback cb);

    @PUT(PROVIDERS_PATH + "{provider_id}/bookings/{booking_id}/claim")
    void claimBooking(@Path("provider_id") String providerId, @Path("booking_id") String bookingId, HandyRetrofitCallback cb);

    @GET(PROVIDERS_PATH + "{provider_id}/bookings/{booking_id}")
    void getBookingDetails(@Path("provider_id") String providerId, @Path("booking_id") String bookingId, HandyRetrofitCallback cb);

    @POST(PROVIDERS_PATH + "{provider_id}/bookings/{booking_id}/on_my_way")
    void notifyOnMyWay(@Path("provider_id") String providerId, @Path("booking_id") String bookingId, HandyRetrofitCallback cb);

    @POST(PROVIDERS_PATH + "{provider_id}/bookings/{booking_id}/check_in")
    void checkIn(@Path("provider_id") String providerId, @Path("booking_id") String bookingId, HandyRetrofitCallback cb);

    @POST(PROVIDERS_PATH + "{provider_id}/bookings/{booking_id}/check_out")
    void checkOut(@Path("provider_id") String providerId, @Path("booking_id") String bookingId, HandyRetrofitCallback cb);

    @Multipart
    @POST(SESSIONS_PATH + "request_pin")
    void requestPinCode(@Part("phone") String phoneNumber, HandyRetrofitCallback cb);

    @Multipart
    @POST(SESSIONS_PATH + "log_in")
    void requestLogin(@Part("phone") String phoneNumber, @Part("pin_code") String pinCode, HandyRetrofitCallback cb);

}
