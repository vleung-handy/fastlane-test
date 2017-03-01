package com.handy.portal.bookings.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class PostCheckoutInfo implements Serializable {
    @SerializedName("customer")
    private Booking.User mCustomer;
    @SerializedName("suggested_jobs")
    private List<Booking> mSuggestedJobs;

    public Booking.User getCustomer() {
        return mCustomer;
    }

    public List<Booking> getSuggestedJobs() {
        return mSuggestedJobs;
    }
}
