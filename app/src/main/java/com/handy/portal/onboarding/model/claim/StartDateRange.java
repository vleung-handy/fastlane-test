package com.handy.portal.onboarding.model.claim;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

public class StartDateRange implements Serializable {
    @SerializedName("start")
    private Date mStartDate;
    @SerializedName("end")
    private Date mEndDate;

    public Date getStartDate() {
        return mStartDate;
    }

    public Date getEndDate() {
        return mEndDate;
    }
}
