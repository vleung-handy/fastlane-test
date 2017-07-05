package com.handy.portal.payments.model;

import com.google.gson.annotations.SerializedName;

public class DailyCashOutRequest {
    /**
     * used by server only for rate-limiting
     */
    @SerializedName("user_id")
    private String mUserId;

    /**
     * whether daily cash out should be enabled
     */
    @SerializedName("enabled")
    private boolean mDailyCashOutEnabled;

    public DailyCashOutRequest(final String userId,
                               final boolean dailyCashOutEnabled) {
        mUserId = userId;
        mDailyCashOutEnabled = dailyCashOutEnabled;
    }
}
