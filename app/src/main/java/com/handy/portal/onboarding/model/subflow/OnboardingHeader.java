package com.handy.portal.onboarding.model.subflow;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class OnboardingHeader implements Serializable
{
    @SerializedName("title")
    private String mTitle;
    @SerializedName("description")
    private String mDescription;
    @SerializedName("image")
    private ImageType mImageType;

    public String getTitle()
    {
        return mTitle;
    }

    public String getDescription()
    {
        return mDescription;
    }

    public enum ImageType
    {
        @SerializedName("avatar-welcome")
        WELCOME,
        @SerializedName("avatar-success")
        SUCCESS,
        @SerializedName("avatar-error")
        ERROR,
    }
}
