package com.handy.portal.payments.model;

import com.google.gson.annotations.SerializedName;

public class ToggleDailyCashOutRequest {
    /**
     * used by server only for rate-limiting
     */
    @SerializedName("user_id")
    private String mUserId;

    /**
     * whether daily cash out should be enabled
     */
    @SerializedName("enabled")
    private boolean mEnableDailyCashOut;

    public ToggleDailyCashOutRequest(final String userId,
                                     final boolean enableDailyCashOut) {
        mUserId = userId;
        mEnableDailyCashOut = enableDailyCashOut;
    }
}
