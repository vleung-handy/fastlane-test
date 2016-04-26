package com.handy.portal.model.onboarding;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jtse on 4/22/16.
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
