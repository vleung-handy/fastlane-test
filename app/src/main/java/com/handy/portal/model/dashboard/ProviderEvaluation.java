package com.handy.portal.model.dashboard;


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

public class ProviderEvaluation implements Serializable
{
    @SerializedName("rolling")
    private Rolling mRolling;
    @SerializedName("life_time")
    private LifeTime mLifeTime;

    @SerializedName("danger_rating_threshold")
    private double mDangerRatingThreshold;
    @SerializedName("five_star_rated_comment")
    private String mFiveStarRatedComment;
    @SerializedName("top_feedback")
    private String mTopFeedback;


    public static class Rolling
    {
        @SerializedName("rated_booking_count")
        private int mRatedBookingCount;
        @SerializedName("total_booking_count")
        private int mTotalBookingCount;
        @SerializedName("five_star_rated_booking_count")
        private int mFiveStarRatedBookingCount;
        @SerializedName("pro_rating")
        private double mProRating;
        @SerializedName("rating_evaluation")
        private String mRatingEvaluation;

        @SerializedName("start_date")
        private Date mStartDate;
        @SerializedName("end_date")
        private Date mEndDate;

        public int getRatedBookingCount()
        {
            return mRatedBookingCount;
        }

        public int getTotalBookingCount()
        {
            return mTotalBookingCount;
        }

        public int getFiveStarRatedBookingCount()
        {
            return mFiveStarRatedBookingCount;
        }

        public double getProRating()
        {
            return mProRating;
        }

        public String getRatingEvaluation()
        {
            return mRatingEvaluation;
        }

        public Date getStartDate()
        {
            return mStartDate;
        }

        public Date getEndDate()
        {
            return mEndDate;
        }
    }


    public static class LifeTime
    {
        @SerializedName("rated_booking_count")
        private int mRatedBookingCount;
        @SerializedName("total_booking_count")
        private int mTotalBookingCount;
        @SerializedName("five_star_rated_booking_count")
        private int mFiveStarRatedBookingCount;
        @SerializedName("pro_rating")
        private double mProRating;
        @SerializedName("rating_evaluation")
        private String mRatingEvaluation;
        @SerializedName("start_date")
        private Date mStartDate;
        @SerializedName("end_date")
        private Date mEndDate;

        public int getRatedBookingCount()
        {
            return mRatedBookingCount;
        }

        public int getTotalBookingCount()
        {
            return mTotalBookingCount;
        }

        public int getFiveStarRatedBookingCount()
        {
            return mFiveStarRatedBookingCount;
        }

        public double getProRating()
        {
            return mProRating;
        }

        public String getRatingEvaluation()
        {
            return mRatingEvaluation;
        }

        public Date getStartDate()
        {
            return mStartDate;
        }

        public Date getEndDate()
        {
            return mEndDate;
        }
    }

    public Rolling getRolling()
    {
        return mRolling;
    }

    public void setRolling(final Rolling rolling)
    {
        mRolling = rolling;
    }

    public LifeTime getLifeTime()
    {
        return mLifeTime;
    }

    public void setLifeTime(final LifeTime lifeTime)
    {
        mLifeTime = lifeTime;
    }

    public double getDangerRatingThreshold()
    {
        return mDangerRatingThreshold;
    }

    public void setDangerRatingThreshold(final double dangerRatingThreshold)
    {
        mDangerRatingThreshold = dangerRatingThreshold;
    }

    public String getFiveStarRatedComment()
    {
        return mFiveStarRatedComment;
    }

    public void setFiveStarRatedComment(final String fiveStarRatedComment)
    {
        mFiveStarRatedComment = fiveStarRatedComment;
    }

    public String getTopFeedback()
    {
        return mTopFeedback;
    }

    public void setTopFeedback(final String topFeedback)
    {
        mTopFeedback = topFeedback;
    }
}
