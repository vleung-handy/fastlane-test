package com.handy.portal.onboarding.model.subflow;

import com.google.gson.annotations.SerializedName;

public enum SubflowStatus {
    @SerializedName("complete")
    COMPLETE,
    @SerializedName("incomplete")
    INCOMPLETE,
    @SerializedName("inactive")
    INACTIVE,
}
