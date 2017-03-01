package com.handy.portal.onboarding.model.status;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class StatusButton implements Serializable {
    @SerializedName("title")
    private String mTitle;
    @SerializedName("deeplink")
    private String mUrl;
    @SerializedName("type")
    private Type mType;

    public String getTitle() {
        return mTitle;
    }

    public String getUrl() {
        return mUrl;
    }

    public Type getType() {
        return mType;
    }

    public enum Type {
        @SerializedName("normal")
        NORMAL,
        @SerializedName("error")
        ERROR,
    }
}
