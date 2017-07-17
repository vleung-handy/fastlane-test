package com.handy.portal.core.model;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

public class ErrorResponse {

    @SerializedName("error")
    private boolean mError;
    @SerializedName("code")
    private Integer mErrorCode;
    @SerializedName("messages")
    private String[] messages;
    @SerializedName("invalid_inputs")
    private String[] invalidInputs;

    public boolean isError() {
        return mError;
    }

    @Nullable
    public Integer getErrorCode() {
        return mErrorCode;
    }

    @Nullable
    public String[] getMessages() {
        return messages;
    }

    @Nullable
    public String[] getInvalidInputs() {
        return invalidInputs;
    }
}
