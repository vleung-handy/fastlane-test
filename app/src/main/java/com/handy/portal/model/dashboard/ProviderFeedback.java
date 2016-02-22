package com.handy.portal.model.dashboard;


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class ProviderFeedback implements Serializable
{
    @SerializedName("title")
    private String mTitle;
    @SerializedName("subtitle")
    private String mSubtitle;
    @SerializedName("feedback_tips")
    private List<FeedbackTip> mFeedbackTips;

    public ProviderFeedback(final String title, final String subtitle, final List<FeedbackTip> feedbackTips)
    {
        mTitle = title;
        mSubtitle = subtitle;
        mFeedbackTips = feedbackTips;
    }

    public String getTitle()
    {
        return mTitle;
    }

    public String getSubtitle()
    {
        return mSubtitle;
    }

    public List<FeedbackTip> getFeedbackTips()
    {
        return mFeedbackTips;
    }

    public static class FeedbackTip
    {
        @SerializedName("type")
        private String mType;
        @SerializedName("data")
        private String mData;

        public FeedbackTip(final String type, final String data)
        {
            mType = type;
            mData = data;
        }

        public String getType()
        {
            return mType;
        }

        public String getData()
        {
            return mData;
        }
    }
}
