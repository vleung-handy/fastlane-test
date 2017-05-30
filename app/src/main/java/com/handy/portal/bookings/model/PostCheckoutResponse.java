package com.handy.portal.bookings.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class PostCheckoutResponse implements Serializable {
    @SerializedName("jobs")
    private List<BookingClaimDetails> mClaims;
    @SerializedName("message")
    private String mMessage;

    public List<BookingClaimDetails> getClaims() {
        return mClaims;
    }

    public String getMessage() {
        return mMessage;
    }
}
