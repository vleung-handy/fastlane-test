package com.handy.portal.onboarding.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 */
public class JobClaimRequest implements Serializable
{
    @SerializedName("jobs")
    public List<JobClaim> mJobs;

    public JobClaimRequest()
    {
        mJobs = new ArrayList<>();
    }
}
