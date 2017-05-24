package com.handy.portal.bookings.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class PostCheckoutResponse implements Serializable {
    @SerializedName("jobs")
    private List<BookingClaimDetails> mJobs;
    @SerializedName("message")
    private String mMessage;

    public List<BookingClaimDetails> getJobs() {
        return mJobs;
    }

    public String getMessage() {
        return mMessage;
    }
}
