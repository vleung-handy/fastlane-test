package com.handy.portal.model.onboarding;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class OnboardingSuppliesSection implements Serializable
{
    @SerializedName("title")
    private String mTitle;
    @SerializedName("description")
    private String mDescription;
    @SerializedName("list")
    private List<String> mList;

    public String getTitle()
    {
        return mTitle;
    }

    public String getDescription()
    {
        return mDescription;
    }

    public List<String> getList()
    {
        return mList;
    }
}
