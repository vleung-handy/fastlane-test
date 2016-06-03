package com.handy.portal.onboarding.model.status;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class LearningLinkDetails implements Serializable
{
    @SerializedName("title")
    private String mTitle;
    @SerializedName("links")
    private ArrayList<LearningLink> mLearningLinks;

    public String getTitle()
    {
        return mTitle;
    }

    public ArrayList<LearningLink> getLearningLinks()
    {
        return mLearningLinks;
    }
}
