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

    public static class FeedbackTip implements Serializable
    {
        public static final String DATA_TYPE_TEXT = "text";
        public static final String DATA_TYPE_VIDEO_LINK = "video_link";

        @SerializedName("type")
        private String mDataType;
        @SerializedName("data")
        private String mData;

        public FeedbackTip(final String dataType, final String data)
        {
            mDataType = dataType;
            mData = data;
        }

        public String getDataType()
        {
            return mDataType;
        }

        public String getData()
        {
            return mData;
        }
    }
}
