package com.handy.portal.core.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class SuccessWrapper implements Serializable {
    @SerializedName("success")
    private Boolean success;

    @SerializedName("message")
    private String mMessage;

    public SuccessWrapper(final Boolean success) { this.success = success; }

    public Boolean getSuccess() {
        return success;
    }

    public String getMessage() {
        return mMessage;
    }
}
