package com.handy.portal.onboarding.model.subflow;

import com.google.gson.annotations.SerializedName;

public enum SubflowType
{
    @SerializedName("status")
    STATUS,
    @SerializedName("claim")
    CLAIM,
    @SerializedName("supplies")
    SUPPLIES,
    @SerializedName("confirmation")
    CONFIRMATION,
}
