package com.handy.portal.retrofit;

import com.google.gson.JsonObject;
import com.handy.portal.announcements.model.CurrentAnnouncementsRequest;
import com.handy.portal.availability.model.Availability;
import com.handy.portal.bookings.model.CheckoutRequest;
import com.handy.portal.bookings.model.PostCheckoutSubmission;
import com.handy.portal.core.model.ProviderSettings;
import com.handy.portal.location.model.LocationBatchUpdate;
import com.handy.portal.onboarding.model.claim.JobClaimRequest;
import com.handy.portal.payments.model.AdhocCashOutRequest;
import com.handy.portal.payments.model.BatchPaymentReviewRequest;
import com.handy.portal.payments.model.BookingPaymentReviewRequest;
import com.handy.portal.payments.model.RecurringCashOutRequest;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
import retrofit.http.QueryMap;
import retrofit.mime.TypedInput;

public interface HandyRetrofitService {
    String SESSIONS_PATH = "/sessions/";
    String BOOKINGS_PATH = "/bookings/";
    String JOBS_PATH = "/jobs/";
    String PROVIDERS_PATH = "/providers/";
    String PAYMENTS_PATH = "/payments/";
    String STRIPE_PATH = "/stripe/";
    String ZIP_CLUSTER_POLYGONS_PATH = "/zipcluster_polygons/";


    @GET(PROVIDERS_PATH + "{id}/location_strategies")
    void getLocationStrategies(@Path("id") String providerId, HandyRetrofitCallback cb);

    @POST(PROVIDERS_PATH + "{id}/geolocation")
    void sendGeolocation(
            @Path("id") String providerId,
            @Body LocationBatchUpdate locationBatchUpdate,
            HandyRetrofitCallback cb);

    @POST("/announcements/current")
    void getCurrentAnnouncements
            (@Body CurrentAnnouncementsRequest currentAnnouncementsRequest,
             HandyRetrofitCallback cb);

    @GET("/setup")
    void getSetupData(HandyRetrofitCallback cb);

    @GET("/check_for_update")
    void checkUpdates(@Query("app_flavor") String appFlavor,
                      @Query("version_code") int versionCode,
                      HandyRetrofitCallback cb);

    @Multipart
    @POST("/accept_terms")
    void acceptTerms(@Part("code") String termsCode,
                     HandyRetrofitCallback handyRetrofitCallback);

    @GET("/config_params")
    void getConfigParams(@Query("key[]") String[] key,
                         HandyRetrofitCallback cb);

    @GET(JOBS_PATH + "available_jobs_count")
    void getJobsCount(@Query("dates[]") List<Date> dates,
                      @QueryMap Map<String, Object> options,
                      HandyRetrofitCallback cb);

    @GET(JOBS_PATH + "available_jobs")
    void getAvailableBookings(@Query("dates[]") Date[] dates,
                              @QueryMap Map<String, Object> options,
                              HandyRetrofitCallback cb);

    @GET(JOBS_PATH + "onboarding_jobs")
    void getOnboardingJobs(@Query("start_date") Date startDate,
                           @Query("preferred_zipclusters[]") ArrayList<String> zipclusterIds,
                           HandyRetrofitCallback cb);

    @GET(JOBS_PATH + "scheduled_jobs")
    void getScheduledBookings(@Query("dates[]") Date[] date,
                              HandyRetrofitCallback cb);

    @GET(JOBS_PATH + "nearby_jobs")
    void getNearbyBookings(@Query("region_id") int region_id,
                           @Query("latitude") double latitude,
                           @Query("longitude") double longitude,
                           HandyRetrofitCallback cb);

    @FormUrlEncoded
    @PUT(JOBS_PATH + "{id}/claim")
    void claimBooking(@Path("id") String bookingId,
                      @Field("type") String type,
                      @Field("claim_swap_job_id") String claimSwitchJobId,
                      @Field("claim_swap_job_type") String claimSwitchJobType,
                      HandyRetrofitCallback cb);

    @PUT(JOBS_PATH + "claim_jobs")
    void claimBookings(@Body JobClaimRequest jobClaimRequest,
                       HandyRetrofitCallback cb);

    @FormUrlEncoded
    @PUT(JOBS_PATH + "{id}/remove")
    void removeBooking(@Path("id") String bookingId,
                       @Field("type") String type,
                       HandyRetrofitCallback cb);

    @GET(JOBS_PATH + "{id}")
    void getBookingDetails(@Path("id") String bookingId,
                           @Query("type") String type,
                           HandyRetrofitCallback cb);

    @FormUrlEncoded
    @POST(JOBS_PATH + "{id}/dismiss")
    void dismissJob(@Path("id") String bookingId,
                    @Field("type") String bookingType,
                    @Field("customer_id") String customerId,
                    @Field("dismissal_reason_machine_name") String dismissalReason,
                    HandyRetrofitCallback cb);

    @GET(PAYMENTS_PATH + "cash_out")
    void getAdhocCashOutInfo(HandyRetrofitCallback cb);

    @POST(PAYMENTS_PATH + "cash_out")
    void requestAdhocCashOut(@Body AdhocCashOutRequest adhocCashOutRequest, HandyRetrofitCallback cb);

    @POST(PAYMENTS_PATH + "cash_out/recurring")
    void requestRecurringCashOut(@Body RecurringCashOutRequest recurringCashOutRequest, HandyRetrofitCallback cb);

    @GET(PAYMENTS_PATH)
    void getPaymentBatches(@Query("date_range_start") Date startDate,
                           @Query("date_range_end") Date endDate,
                           HandyRetrofitCallback cb);

    @GET(PAYMENTS_PATH + "requires_update")
    void getNeedsToUpdatePaymentInfo(HandyRetrofitCallback cb);

    @GET(PAYMENTS_PATH + "outstanding_fees")
    void getPaymentOutstandingFees(HandyRetrofitCallback cb);

    @GET(PAYMENTS_PATH + "booking_details")
    void getBookingTransactions(@Query("booking_id") String bookingId,
                                @Query("booking_type") String bookingType,
                                HandyRetrofitCallback cb);

    @POST(PAYMENTS_PATH + "batch_review")
    void submitBatchPaymentReviewRequest(@Body BatchPaymentReviewRequest paymentSupportRequest,
                                         HandyRetrofitCallback cb);

    @POST(PAYMENTS_PATH + "booking_review")
    void submitBookingPaymentReviewRequest
            (@Body BookingPaymentReviewRequest bookingPaymentReviewRequest,
             HandyRetrofitCallback cb);

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

    @FormUrlEncoded
    @PUT(STRIPE_PATH + "update_credit_card")
    void updateCreditCard(@Field("token") String token,
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

    @GET(PROVIDERS_PATH + "{id}/availability_timeline")
    void getAvailability(@Path("id") String providerId, HandyRetrofitCallback cb);

    @POST(PROVIDERS_PATH + "{id}/availability_timeline")
    void saveAvailability(@Path("id") String providerId,
                          @Body Availability.Wrapper.AdhocTimelines timelinesWrapper,
                          HandyRetrofitCallback cb);


    @GET(PROVIDERS_PATH + "{id}/availability_template")
    void getAvailabilityTemplate(@Path("id") String providerId, HandyRetrofitCallback cb);

    @POST(PROVIDERS_PATH + "{id}/availability_template")
    void saveAvailabilityTemplate(@Path("id") String providerId,
                                  @Body Availability.Wrapper.TemplateTimelines timelinesWrapper,
                                  HandyRetrofitCallback cb);

    @FormUrlEncoded
    @POST(PROVIDERS_PATH + "{id}/provider_requests/{request_id}/send_times")
    void sendAvailability(@Path("id") String providerId,
                          @Path("request_id") String requestId,
                          @Field("response_text") String response,
                          HandyRetrofitCallback cb);

    @FormUrlEncoded
    @POST(BOOKINGS_PATH + "{booking_id}/on_my_way")
    void notifyOnMyWay(@Path("booking_id") String bookingId,
                       @FieldMap Map<String, String> locationParams,
                       HandyRetrofitCallback cb);

    @FormUrlEncoded
    @POST(BOOKINGS_PATH + "{booking_id}/check_in")
    void checkIn(@Path("booking_id") String bookingId,
                 @FieldMap Map<String, String> locationParams,
                 HandyRetrofitCallback cb);

    @POST(BOOKINGS_PATH + "{booking_id}/check_out")
    void checkOut(
            @Path("booking_id") String bookingId,
            @Body CheckoutRequest request,
            HandyRetrofitCallback cb);

    @GET(BOOKINGS_PATH + "{booking_id}/post_checkout")
    void requestPostCheckoutInfo(@Path("booking_id") String bookingId, HandyRetrofitCallback cb);

    @POST(BOOKINGS_PATH + "{booking_id}/post_checkout")
    void submitPostCheckoutInfo(
            @Path("booking_id") String bookingId,
            @Body PostCheckoutSubmission postCheckoutSubmission,
            HandyRetrofitCallback cb
    );

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

    @FormUrlEncoded
    @POST(BOOKINGS_PATH + "{booking_id}/rate_customer")
    void rateCustomer(@Path("booking_id") String bookingId,
                      @Field("rating") int rating,
                      @Field("review_text") String reviewText,
                      HandyRetrofitCallback cb);

    @Multipart
    @POST(SESSIONS_PATH + "request_user_pin")
    void requestPinCode(@Part("phone") String phoneNumber, HandyRetrofitCallback cb);

    @Multipart
    @POST(SESSIONS_PATH + "request_slt")
    void requestSlt(@Part("phone") String phoneNumber, HandyRetrofitCallback cb);

    @Multipart
    @POST(SESSIONS_PATH + "log_in_with_slt")
    void requestLoginWithSlt(
            @Part("n") String n,
            @Part("sig") String sig,
            @Part("slt") String slt,
            HandyRetrofitCallback cb);

    @Multipart
    @POST(SESSIONS_PATH + "log_in")
    void requestLogin(@Part("phone") String phoneNumber,
                      @Part("pin_code") String pinCode,
                      HandyRetrofitCallback cb);

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

    @FormUrlEncoded
    @POST(PROVIDERS_PATH + "{id}/notifications/mark_as_interacted")
    void postMarkNotificationsAsInteracted(@Path("id") String providerId,
                                           @Field("notification_ids[]") ArrayList<Integer> notificationIds,
                                           HandyRetrofitCallback cb);

    @GET(PROVIDERS_PATH + "{id}/notifications/unread_count")
    void getNotificationsUnreadCount(@Path("id") String providerId, HandyRetrofitCallback cb);

    // Dashboard
    @GET(PROVIDERS_PATH + "{id}/evaluation")
    void getProviderEvaluation(@Path("id") String providerId, HandyRetrofitCallback cb);

    @GET(PROVIDERS_PATH + "{id}/ratings?commented_only=true")
    void getProviderFiveStarRatings(@Path("id") String providerId,
                                    @Query("min_star") Integer minStar,
                                    @Query("until_booking_date") String untilBookingDate,
                                    @Query("since_booking_date") String sinceBookingDate,
                                    HandyRetrofitCallback cb);

    @GET(PROVIDERS_PATH + "{id}/feedback")
    void getProviderFeedback(@Path("id") String providerId, HandyRetrofitCallback cb);

    @FormUrlEncoded
    @POST(PROVIDERS_PATH + "{id}/onboarding_supplies")
    void requestOnboardingSupplies(@Path("id") String providerId,
                                   @Field("onboarding_supplies") Boolean value,
                                   HandyRetrofitCallback cb);


    @POST("/{before_start_url}")
    void beforeStartIdVerification(@Path(value = "before_start_url", encode = false) String beforeIdVerificationStartUrl,
                                   @Body HashMap<String, String> map,
                                   HandyRetrofitCallback cb);

    @FormUrlEncoded
    @POST("/{after_finish_url}")
    void finishIdVerification(@Path(value = "after_finish_url", encode = false) String afterIdVerificationFinishUrl,
                              @Field("scan_reference") String scanReference,
                              @Field("status") String status,
                              HandyRetrofitCallback cb);

    @FormUrlEncoded
    @POST(PROVIDERS_PATH + "{id}/profile_photo")
    void requestPhotoUploadUrl(@Path("id") String providerId,
                               @Field("mime_type") String imageMimeType,
                               HandyRetrofitCallback cb);

    //For logging
    @POST("/events")
    void postLogs(@Body JsonObject eventLogBundle, HandyRetrofitCallback cb);
}
