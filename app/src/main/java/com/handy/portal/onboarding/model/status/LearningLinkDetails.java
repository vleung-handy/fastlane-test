package com.handy.portal.onboarding.model.status;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class LearningLinkDetails implements Serializable
{
    @SerializedName("links")
    private ArrayList<LearningLink> mLearningLinks;

    public ArrayList<LearningLink> getLearningLinks()
    {
        return mLearningLinks;
    }
}
