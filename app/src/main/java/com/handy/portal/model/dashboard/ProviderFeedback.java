package com.handy.portal.model.dashboard;


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class ProviderFeedback implements Serializable
{
    @SerializedName("title")
    private String mTitle;
    @SerializedName("feedback")
    private String mFeedback;
    @SerializedName("feedback_highlights")
    private List<String> mFeedbackHighlights;
    @SerializedName("feedback_tips")
    private List<String> mFeedbackTips;

    public String getTitle()
    {
        return mTitle;
    }

    public String getFeedback()
    {
        return mFeedback;
    }

    public List<String> getFeedbackHighlights()
    {
        return mFeedbackHighlights;
    }

    public List<String> getFeedbackTips()
    {
        return mFeedbackTips;
    }
}
