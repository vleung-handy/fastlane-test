package com.handy.portal.retrofit;

import com.handy.portal.bookings.model.BookingsListWrapper;

import java.util.Date;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface HandyRetrofit2Service {

    String JOBS_PATH = "jobs/";

    @GET(JOBS_PATH + "available_jobs")
    Call<BookingsListWrapper> getAvailableBookings(
            @Query("dates[]") Date[] dates, @QueryMap Map<String, Object> options);
}
