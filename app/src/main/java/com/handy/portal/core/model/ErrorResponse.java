package com.handy.portal.core.model;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

public class ErrorResponse {

    @SerializedName("error")
    private boolean mError;
    @SerializedName("code")
    private Integer mErrorCode;
    @SerializedName("messages")
    private String[] mMessages;
    @SerializedName("invalid_inputs")
    private String[] mInvalidInputs;

    public boolean isError() {
        return mError;
    }

    @Nullable
    public Integer getErrorCode() {
        return mErrorCode;
    }

    @Nullable
    public String[] getMessages() {
        return mMessages;
    }

    @Nullable
    public String[] getInvalidInputs() {
        return mInvalidInputs;
    }
}
