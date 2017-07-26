package com.handy.portal.clients.model;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by sng on 7/24/17. This is the stats for a client
 */

public class Stats implements Serializable {
    @Nullable
    @SerializedName("total_earnings")
    private Price mTotalEarnings;

    @SerializedName("total_jobs_count")
    private int mTotalJobsCount;

    @Nullable
    public Price getTotalEarnings() {
        return mTotalEarnings;
    }

    public int getTotalJobsCount() {
        return mTotalJobsCount;
    }
}
