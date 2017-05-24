package com.handy.portal.bookings.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class PostCheckoutInfo implements Serializable {
    @SerializedName("customer")
    private User mCustomer;
    @SerializedName("suggested_jobs")
    private List<Booking> mSuggestedJobs;
    @SerializedName("total_potential_cents")
    private int mTotalPotentialCents;

    public User getCustomer() {
        return mCustomer;
    }

    public List<Booking> getSuggestedJobs() {
        return mSuggestedJobs;
    }

    public int getTotalPotentialCents() {
        return mTotalPotentialCents;
    }
}
