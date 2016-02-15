package com.handy.portal.model.dashboard;


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

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
    @SerializedName("low_star_reasons")
    private List<String> mLowStarReasons;

    public ProviderEvaluation(final Rolling rolling, final LifeTime lifeTime, final double dangerRatingThreshold, final String fiveStarRatedComment, final List<String> lowStarReasons)
    {
        mRolling = rolling;
        mLifeTime = lifeTime;
        mDangerRatingThreshold = dangerRatingThreshold;
        mFiveStarRatedComment = fiveStarRatedComment;
        mLowStarReasons = lowStarReasons;
    }

    public Rolling getRolling()
    {
        return mRolling;
    }

    public LifeTime getLifeTime()
    {
        return mLifeTime;
    }

    public double getDangerRatingThreshold()
    {
        return mDangerRatingThreshold;
    }

    public String getFiveStarRatedComment()
    {
        return mFiveStarRatedComment;
    }

    public List<String> getLowStarReasons()
    {
        return mLowStarReasons;
    }

    public static class Rolling extends LifeTime
    {
        @SerializedName("tier")
        private Tier mTier;

        public Rolling(final int ratedBookingCount, final int totalBookingCount, final int fiveStarRatedBookingCount, final double proRating, final String ratingEvaluation, final Date startDate, final Date endDate, final Tier tier)
        {
            super(ratedBookingCount, totalBookingCount, fiveStarRatedBookingCount, proRating, ratingEvaluation, startDate, endDate);
            mTier = tier;
        }

        public Tier getTier()
        {
            return mTier;
        }

        public static class Tier
        {
            public Tier(final String name, final String hourlyRate)
            {
                mName = name;
                mHourlyRate = hourlyRate;
            }

            @SerializedName("name")
            private String mName;
            @SerializedName("hourly_rate")
            private String mHourlyRate;

            public String getName()
            {
                return mName;
            }

            public String getHourlyRate()
            {
                return mHourlyRate;
            }
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

        public LifeTime(final int ratedBookingCount, final int totalBookingCount, final int fiveStarRatedBookingCount, final double proRating, final String ratingEvaluation, final Date startDate, final Date endDate)
        {
            mRatedBookingCount = ratedBookingCount;
            mTotalBookingCount = totalBookingCount;
            mFiveStarRatedBookingCount = fiveStarRatedBookingCount;
            mProRating = proRating;
            mRatingEvaluation = ratingEvaluation;
            mStartDate = startDate;
            mEndDate = endDate;
        }

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

}
