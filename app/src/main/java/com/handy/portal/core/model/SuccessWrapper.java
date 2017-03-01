package com.handy.portal.core.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class SuccessWrapper implements Serializable {
    @SerializedName("success")
    private Boolean success;

    public SuccessWrapper(final Boolean success) { this.success = success; }

    public Boolean getSuccess() {
        return success;
    }
}
