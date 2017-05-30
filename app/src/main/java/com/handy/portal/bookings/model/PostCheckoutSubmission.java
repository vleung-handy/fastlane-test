package com.handy.portal.bookings.model;


import com.google.gson.annotations.SerializedName;
import com.handy.portal.onboarding.model.claim.JobClaim;

import java.io.Serializable;
import java.util.ArrayList;

public class PostCheckoutSubmission implements Serializable {
    @SerializedName("customer_preferred")
    private boolean mCustomerPreferred;
    @SerializedName("selected_jobs")
    private ArrayList<JobClaim> mJobClaims;
    @SerializedName("feedback")
    private String mFeedback;

    public PostCheckoutSubmission(
            final boolean customerPreferred,
            final ArrayList<JobClaim> jobClaims,
            final String feedback
    ) {
        mCustomerPreferred = customerPreferred;
        mJobClaims = jobClaims;
        mFeedback = feedback;
    }
}
