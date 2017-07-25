package com.handy.portal.clients.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by sng on 7/24/17. This is the stats for a client
 */

public class Stats implements Serializable {
    @SerializedName("total_earnings")
    private Price mTotalEarnings;

    @SerializedName("total_jobs_count")
    private int mTotalJobsCount;

    public Price getTotalEarnings() {
        return mTotalEarnings;
    }

    public int getTotalJobsCount() {
        return mTotalJobsCount;
    }
}
