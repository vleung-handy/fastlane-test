package com.handy.portal.onboarding.model.claim;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class JobClaimRequest implements Serializable
{
    @SerializedName("jobs")
    private ArrayList<JobClaim> mJobs;

    public JobClaimRequest(final ArrayList<JobClaim> jobs)
    {
        mJobs = jobs;
    }

    public ArrayList<JobClaim> getJobs()
    {
        return mJobs;
    }
}
