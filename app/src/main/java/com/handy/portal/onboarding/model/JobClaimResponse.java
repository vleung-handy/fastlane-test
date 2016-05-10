package com.handy.portal.onboarding.model;

import com.google.gson.annotations.SerializedName;
import com.handy.portal.bookings.model.BookingClaimDetails;

import java.io.Serializable;
import java.util.List;

/**
 */
public class JobClaimResponse implements Serializable
{
    @SerializedName("jobs")
    private List<BookingClaimDetails> mJobs;

    @SerializedName("message")
    private String mMessage;

    public List<BookingClaimDetails> getJobs()
    {
        return mJobs;
    }

    public void setJobs(final List<BookingClaimDetails> jobs)
    {
        mJobs = jobs;
    }

    public String getMessage()
    {
        return mMessage;
    }

    public void setMessage(final String message)
    {
        mMessage = message;
    }
}
