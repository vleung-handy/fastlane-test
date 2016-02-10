package com.handy.portal.retrofit;

import com.handy.portal.model.CheckoutRequest;
import com.handy.portal.model.ProviderSettings;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import retrofit.http.Body;
import retrofit.http.Field;
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
    String PROVIDERS_PATH = "/providers/";
    String PAYMENTS_PATH = "/payments/";
    String STRIPE_PATH = "/stripe/";
    String ZIP_CLUSTER_POLYGONS_PATH = "/zipcluster_polygons/";

    @GET("/check_for_update")
    void checkUpdates(@Query("app_flavor") String appFlavor,
                      @Query("version_code") int versionCode,
                      HandyRetrofitCallback cb);

    @GET("/check_all_pending_terms")
    void checkAllPendingTerms(HandyRetrofitCallback cb);

    @Multipart
    @POST("/accept_terms")
    void acceptTerms(@Part("code") String termsCode,
                     HandyRetrofitCallback handyRetrofitCallback);

    @FormUrlEncoded
    @POST("/log_version_info")
    void sendVersionInformation(@FieldMap Map<String, String> params,
                                HandyRetrofitCallback cb);

    @GET("/config_params")
    void getConfigParams(@Query("key[]") String[] key,
                         HandyRetrofitCallback cb);

    @GET(JOBS_PATH + "available_jobs")
    void getAvailableBookings(@Query("dates[]") Date[] dates,
                              HandyRetrofitCallback cb);

    @GET(JOBS_PATH + "scheduled_jobs")
    void getScheduledBookings(@Query("dates[]") Date[] date,
                              HandyRetrofitCallback cb);

    @GET(JOBS_PATH + "nearby_jobs")
    void getNearbyBookings(@Query("region_id") int region_id,
                           @Query("latitude") double latitude,
                           @Query("longitude") double longitude,
                           HandyRetrofitCallback cb);

    @PUT(JOBS_PATH + "{id}/claim")
    void claimBooking(@Path("id") String bookingId,
                      @Query("type") String type,
                      HandyRetrofitCallback cb);

    @PUT(JOBS_PATH + "{id}/remove")
    void removeBooking(@Path("id") String bookingId,
                       @Query("type") String type,
                       HandyRetrofitCallback cb);

    @GET(JOBS_PATH + "{id}")
    void getBookingDetails(@Path("id") String bookingId,
                           @Query("type") String type,
                           HandyRetrofitCallback cb);

    @GET(JOBS_PATH + "{id}/complementary_jobs")
    void getComplementaryBookings(@Path("id") String bookingId,
                                  @Query("type") String type,
                                  HandyRetrofitCallback cb);

    @GET(PAYMENTS_PATH)
    void getPaymentBatches(@Query("date_range_start") Date startDate,
                           @Query("date_range_end") Date endDate,
                           HandyRetrofitCallback cb);

    @GET(PAYMENTS_PATH + "annual_summaries")
    void getAnnualPaymentSummaries(HandyRetrofitCallback cb);

    @GET(PAYMENTS_PATH + "requires_update")
    void getNeedsToUpdatePaymentInfo(HandyRetrofitCallback cb);

    @FormUrlEncoded
    @POST(STRIPE_PATH + "create_bank_account")
    void createBankAccount(@FieldMap Map<String, String> params,
                           HandyRetrofitCallback cb);

    @FormUrlEncoded
    @POST(STRIPE_PATH + "create_debit_card_recipient")
    void createDebitCardRecipient(@FieldMap Map<String, String> params,
                                  HandyRetrofitCallback cb);

    @FormUrlEncoded
    @POST(STRIPE_PATH + "create_debit_card_for_charge")
    void createDebitCardForCharge(@Field("token") String stripeToken,
                                  HandyRetrofitCallback cb);

    @GET(PROVIDERS_PATH + "{id}/payment_flow")
    void getPaymentFlow(@Path("id") String providerId,
                        HandyRetrofitCallback cb);

    @GET(PROVIDERS_PATH + "{id}/send_income_verification")
    void sendIncomeVerification(@Path("id") String providerId,
                                HandyRetrofitCallback cb);

    @GET(ZIP_CLUSTER_POLYGONS_PATH + "{id}")
    void getZipClusterPolygon(@Path("id") String zipClusterPolygonId,
                              HandyRetrofitCallback cb);

    @GET(PROVIDERS_PATH + "{id}")
    void getProviderProfile(@Path("id") String providerId,
                            HandyRetrofitCallback cb);

    @FormUrlEncoded
    @PUT(PROVIDERS_PATH + "{id}")
    void updateProviderProfile(@Path("id") String providerId,
                               @FieldMap Map<String, String> params,
                               HandyRetrofitCallback cb);

    @PUT(PROVIDERS_PATH + "{id}/settings")
    void putUpdateProviderSettings(@Path("id") String providerId,
                                   @Body ProviderSettings providerSettings,
                                   HandyRetrofitCallback cb);

    @GET(PROVIDERS_PATH + "{id}/settings")
    void getProviderSettings(@Path("id") String providerId, HandyRetrofitCallback cb);

    // This is temporary; we will be changing back to 'send_resupply_kit' after we
    // are able to change the endpoint to send back the updated provider profile
    @POST(PROVIDERS_PATH + "{id}/temp_send_resupply_kit")
    void getResupplyKit(@Path("id") String providerId,
                        HandyRetrofitCallback cb);

    @FormUrlEncoded
    @POST(BOOKINGS_PATH + "{booking_id}/on_my_way")
    void notifyOnMyWay(@Path("booking_id") String bookingId,
                       @FieldMap Map<String, String> locationParams,
                       HandyRetrofitCallback cb);

    @FormUrlEncoded
    @POST(BOOKINGS_PATH + "{booking_id}/check_in")
    void checkIn(@Path("booking_id") String bookingId,
                 @Query("auto") boolean isAuto,
                 @FieldMap Map<String, String> locationParams,
                 HandyRetrofitCallback cb);

    @POST(BOOKINGS_PATH + "{booking_id}/check_out")
    void checkOut(
            @Path("booking_id") String bookingId,
            @Query("auto") boolean isAuto,
            @Body CheckoutRequest request,
            HandyRetrofitCallback cb);

    @FormUrlEncoded
    @POST(BOOKINGS_PATH + "{booking_id}/customer_no_show")
    void reportNoShow(@Path("booking_id") String bookingId,
                      @FieldMap Map<String, String> params,
                      HandyRetrofitCallback cb);

    @Multipart
    @POST(BOOKINGS_PATH + "{booking_id}/eta")
    void updateArrivalTime(@Path("booking_id") String bookingId,
                           @Part("lateness") String latenessValue,
                           HandyRetrofitCallback cb);

    @Multipart
    @POST(SESSIONS_PATH + "request_pin")
    void requestPinCode(@Part("phone") String phoneNumber,
                        HandyRetrofitCallback cb);

    @Multipart
    @POST(SESSIONS_PATH + "log_in")
    void requestLogin(@Part("phone") String phoneNumber,
                      @Part("pin_code") String pinCode,
                      HandyRetrofitCallback cb);

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

    @GET(SELF_SERVICE_PATH + "payments_faq_node")
    void getHelpPayments(HandyRetrofitCallback cb);

    @POST(SELF_SERVICE_PATH + "create_case")
    void createHelpCase(@Body TypedInput body, HandyRetrofitCallback cb);
    //********End Help Center********

    //Configuration wrapper, eventually replacing direct config params access
    @GET("/configuration")
    void getConfiguration(HandyRetrofitCallback cb);

    @GET(PROVIDERS_PATH + "{id}/notifications")
    void getNotifications(@Path("id") String providerId,
                          @Query("since_id") Integer sinceId,
                          @Query("until_id") Integer untilId,
                          @Query("count") Integer count,
                          HandyRetrofitCallback cb);

    @FormUrlEncoded
    @POST(PROVIDERS_PATH + "{id}/notifications/mark_as_read")
    void postMarkNotificationsAsRead(@Path("id") String providerId,
                                     @Field("notification_ids[]") ArrayList<Integer> notificationIds,
                                     HandyRetrofitCallback cb);

    // Dashboard
    @GET(PROVIDERS_PATH + "evaluation/{id}")
    void getProviderEvaluation(@Path("id") String providerId, HandyRetrofitCallback cb);

    @GET(PROVIDERS_PATH + "rating/{id}?min_star=5")
    void getProviderFiveStarRatings(@Path("id") String providerId, HandyRetrofitCallback cb);

    @GET(PROVIDERS_PATH + "feedback/{id}")
    void getProviderFeedback(@Path("id") String providerId, HandyRetrofitCallback cb);
}
