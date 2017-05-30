package com.handy.portal.onboarding.model.claim;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class JobClaimRequest implements Serializable {
    public static class Source {
        public static final String ONBOARDING = "onboarding";
    }

    @SerializedName("jobs")
    private ArrayList<JobClaim> mJobs;
    @SerializedName("source")
    private String mSource;

    public JobClaimRequest(final ArrayList<JobClaim> jobs, final String source) {
        mJobs = jobs;
        mSource = source;
    }

    public ArrayList<JobClaim> getJobs() {
        return mJobs;
    }
}
