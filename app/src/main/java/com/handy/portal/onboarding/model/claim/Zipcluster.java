package com.handy.portal.onboarding.model.claim;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Zipcluster implements Serializable {
    @SerializedName("id")
    private String mId;
    @SerializedName("name")
    private String mName;

    public String getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }
}
