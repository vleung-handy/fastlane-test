package com.handy.portal.onboarding.model.status;

import com.google.gson.annotations.SerializedName;

public enum ApplicationStatus
{
    @SerializedName("welcome")
    WELCOME,
    @SerializedName("pending")
    PENDING,
    @SerializedName("accepted")
    ACCEPTED,
    @SerializedName("rejected")
    REJECTED,
    @SerializedName("unverified")
    UNVERIFIED,
    @SerializedName("long_running_report")
    LONG_RUNNING_REPORT,
}
