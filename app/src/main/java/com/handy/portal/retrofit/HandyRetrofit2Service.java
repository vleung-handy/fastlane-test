package com.handy.portal.retrofit;

import android.support.annotation.Nullable;

import com.handy.portal.bookings.model.BookingsListWrapper;
import com.handy.portal.clients.model.ClientList;

import java.util.Date;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface HandyRetrofit2Service {

    String PROVIDERS_PATH = "providers/";
    String JOBS_PATH = "jobs/";

    @GET(JOBS_PATH + "available_jobs")
    Call<BookingsListWrapper> getAvailableBookings(
            @Query("dates[]") Date[] dates, @QueryMap Map<String, Object> options);


    /**
     * if clientId is null, it'll start at the beginning
     * https://hackmd.io/EwQwDAJgHAnFBGBaAZgYygdkQFgMyqxjGQkQFMNVUA2eYAVipnqA?view
     */
    @GET(PROVIDERS_PATH + "{id}/clients")
    Call<ClientList> getClientList(@Path(value = "id") String providerId,
                                   @Nullable @Query("starting_after") String clientId,
                                   @Nullable @Query("limit") int limitSize);
}
