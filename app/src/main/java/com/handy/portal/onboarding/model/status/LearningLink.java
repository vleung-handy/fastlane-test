package com.handy.portal.onboarding.model.status;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class LearningLink implements Serializable {
    @SerializedName("title")
    private String mTitle;
    @SerializedName("url")
    private String mUrl;

    public String getTitle() {
        return mTitle;
    }

    public String getUrl() {
        return mUrl;
    }
}
