package com.handy.portal.retrofit;

import java.util.Date;
import java.util.Map;

import retrofit.http.Body;
import retrofit.http.FieldMap;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Part;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.mime.TypedInput;

public interface HandyRetrofitService
{
    String SESSIONS_PATH = "/sessions/";
    String BOOKINGS_PATH = "/bookings/";
    String JOBS_PATH = "/jobs/";

    @GET("/check_for_update")
    void checkUpdates(@Query("app_flavor") String appFlavor, @Query("version_code") int versionCode, HandyRetrofitCallback cb);

    @GET("/check_terms")
    void checkTerms(HandyRetrofitCallback cb);

    @Multipart
    @POST("/accept_terms")
    void acceptTerms(@Part("code") String termsCode, HandyRetrofitCallback handyRetrofitCallback);

    @FormUrlEncoded
    @POST("/log_version_info")
    void sendVersionInformation(@FieldMap Map<String, String> params, HandyRetrofitCallback cb);

    @GET("/config_params")
    void getConfigParams(@Query("key[]") String[] key, HandyRetrofitCallback cb);

    @GET(JOBS_PATH + "/available_jobs")
    void getAvailableBookings(@Query("dates[]") Date[] dates, HandyRetrofitCallback cb);

    @GET(JOBS_PATH + "/scheduled_jobs")
    void getScheduledBookings(@Query("dates[]") Date[] date, HandyRetrofitCallback cb);

    @PUT(JOBS_PATH + "{id}/claim")
    void claimBooking(@Path("id") String bookingId, @Query("type") String type, HandyRetrofitCallback cb);

    @PUT(JOBS_PATH + "{id}/remove")
    void removeBooking(@Path("id") String bookingId, @Query("type") String type, HandyRetrofitCallback cb);

    @GET(JOBS_PATH + "{id}")
    void getBookingDetails(@Path("id") String bookingId, @Query("type") String type, HandyRetrofitCallback cb);

    @GET(JOBS_PATH + "{id}/complementary_jobs")
    void getComplementaryBookings(@Path("id") String bookingId, @Query("type") String type, HandyRetrofitCallback cb);

    @FormUrlEncoded
    @POST(BOOKINGS_PATH + "{booking_id}/on_my_way")
    void notifyOnMyWay(@Path("booking_id") String bookingId, @FieldMap Map<String, String> locationParams, HandyRetrofitCallback cb);

    @FormUrlEncoded
    @POST(BOOKINGS_PATH + "{booking_id}/check_in")
    void checkIn(@Path("booking_id") String bookingId, @FieldMap Map<String, String> locationParams, HandyRetrofitCallback cb);

    @FormUrlEncoded
    @POST(BOOKINGS_PATH + "{booking_id}/check_out")
    void checkOut(@Path("booking_id") String bookingId, @FieldMap Map<String, String> locationParams, HandyRetrofitCallback cb);

    @FormUrlEncoded
    @POST(BOOKINGS_PATH + "{booking_id}/customer_no_show")
    void reportNoShow(@Path("booking_id") String bookingId, @FieldMap Map<String, String> params, HandyRetrofitCallback cb);

    @Multipart
    @POST(BOOKINGS_PATH + "{booking_id}/eta")
    void updateArrivalTime(@Path("booking_id") String bookingId, @Part("lateness") String latenessValue, HandyRetrofitCallback cb);

    @Multipart
    @POST(SESSIONS_PATH + "request_pin")
    void requestPinCode(@Part("phone") String phoneNumber, HandyRetrofitCallback cb);

    @Multipart
    @POST(SESSIONS_PATH + "log_in")
    void requestLogin(@Part("phone") String phoneNumber, @Part("pin_code") String pinCode, HandyRetrofitCallback cb);

    @GET(SESSIONS_PATH + "provider_info")
    void getProviderInfo(HandyRetrofitCallback cb);

    //********Help Center********
    String SELF_SERVICE_PATH = "/self_service/";

    @GET(SELF_SERVICE_PATH + "node_details")
    void getHelpInfo(@Query("id") String nodeId,
                     @Query("booking_id") String bookingId,
                     HandyRetrofitCallback cb);

    @GET(SELF_SERVICE_PATH + "booking_node_details")
    void getHelpBookingsInfo(@Query("id") String nodeId,
                             @Query("booking_id") String bookingId,
                             HandyRetrofitCallback cb);

    @POST(SELF_SERVICE_PATH + "create_case")
    void createHelpCase(@Body TypedInput body, HandyRetrofitCallback cb);
    //********End Help Center********

}
