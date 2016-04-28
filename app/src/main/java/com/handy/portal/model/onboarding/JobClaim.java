package com.handy.portal.model.onboarding;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 */
public class JobClaim implements Serializable
{

    @SerializedName("id")
    private String mBookingId;

    @SerializedName("type")
    private String mJobType;

    public JobClaim(final String bookingId, final String jobType)
    {
        mBookingId = bookingId;
        mJobType = jobType;
    }
}
