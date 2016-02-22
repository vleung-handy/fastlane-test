package com.handy.portal.model.dashboard;


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class ProviderEvaluation implements Serializable
{
    @SerializedName("rolling")
    private Rating mRolling;
    @SerializedName("life_time")
    private Rating mLifeTime;
    @SerializedName("tier")
    private Tier mTier;
    @SerializedName("danger_rating_threshold")
    private double mDangerRatingThreshold;
    @SerializedName("five_star_ratings")
    private List<ProviderRating> mFiveStarRatings;
    @SerializedName("feedback")
    private List<ProviderFeedback> mProviderFeedback;

    public ProviderEvaluation(
            final Rating rolling, final Rating lifeTime, final Tier tier,
            final double dangerRatingThreshold, final List<ProviderRating> fiveStarRatings,
            final List<ProviderFeedback> providerFeedback)
    {
        mRolling = rolling;
        mLifeTime = lifeTime;
        mTier = tier;
        mDangerRatingThreshold = dangerRatingThreshold;
        mFiveStarRatings = fiveStarRatings;
        mProviderFeedback = providerFeedback;
    }

    public Rating getRolling()
    {
        return mRolling;
    }

    public Rating getLifeTime()
    {
        return mLifeTime;
    }

    public Tier getTier()
    {
        return mTier;
    }

    public double getDangerRatingThreshold()
    {
        return mDangerRatingThreshold;
    }

    public List<ProviderRating> getFiveStarRatings()
    {
        return mFiveStarRatings;
    }

    public List<ProviderFeedback> getProviderFeedback()
    {
        return mProviderFeedback;
    }

    public static class Rating
    {
        @SerializedName("rated_booking_count")
        private int mRatedBookingCount;
        @SerializedName("total_booking_count")
        private int mTotalBookingCount;
        @SerializedName("five_star_rated_booking_count")
        private int mFiveStarRatedBookingCount;
        @SerializedName("pro_rating")
        private double mProRating;
        @SerializedName("status")
        private String mStatus;
        @SerializedName("feedback")
        private String mFeedback;
        @SerializedName("start_date")
        private Date mStartDate;
        @SerializedName("end_date")
        private Date mEndDate;

        public Rating(
                final int ratedBookingCount, final int totalBookingCount,
                final int fiveStarRatedBookingCount, final double proRating, final String status,
                final String feedback, final Date startDate, final Date endDate)
        {
            mRatedBookingCount = ratedBookingCount;
            mTotalBookingCount = totalBookingCount;
            mFiveStarRatedBookingCount = fiveStarRatedBookingCount;
            mProRating = proRating;
            mStatus = status;
            mFeedback = feedback;
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

        public String getStatus()
        {
            return mStatus;
        }

        public String getFeedback()
        {
            return mFeedback;
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


    public static class Tier
    {
        @SerializedName("name")
        private String mName;
        @SerializedName("hourly_rate_in_cents")
        private int mHourlyRate;

        public Tier(final String name, final int hourlyRate)
        {
            mName = name;
            mHourlyRate = hourlyRate;
        }

        public String getName()
        {
            return mName;
        }

        public int getHourlyRate()
        {
            return mHourlyRate;
        }
    }
}
