package com.handy.portal.model.dashboard;


import com.google.gson.annotations.SerializedName;
import com.handy.portal.R;
import com.handy.portal.util.TextUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProviderEvaluation implements Serializable
{
    @SerializedName("rolling")
    private Rating mRolling;
    @SerializedName("lifetime")
    private Rating mLifeTime;
    @SerializedName("tier")
    private Tier mTier;
    @SerializedName("danger_rating_threshold")
    private double mDangerRatingThreshold;
    @SerializedName("five_star_ratings")
    private List<ProviderRating> mFiveStarRatings;
    @SerializedName("feedback")
    private List<ProviderFeedback> mProviderFeedback;

    private List<ProviderRating> mFiveStarRatingsWithComments;

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

    public List<ProviderRating> getFiveStarRatingsWithComments()
    {
        if (mFiveStarRatingsWithComments == null)
        {
            mFiveStarRatingsWithComments = new ArrayList<>();
            for (ProviderRating rating : mFiveStarRatings)
            {
                if (!TextUtils.isNullOrEmpty(rating.getComment()))
                {
                    mFiveStarRatingsWithComments.add(rating);
                }
            }
        }
        return mFiveStarRatingsWithComments;
    }

    public List<ProviderFeedback> getProviderFeedback()
    {
        return mProviderFeedback;
    }

    public static class Rating implements Serializable
    {
        private static final String POSITIVE = "positive";
        private static final String NEUTRAL = "neutral";
        private static final String NEGATIVE = "negative";
        private static final String NA = "n_a";

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
        @SerializedName("rating_evaluation")
        private String mRatingEvaluation;
        @SerializedName("feedback")
        private String mFeedback;
        @SerializedName("start_date")
        private Date mStartDate;
        @SerializedName("end_date")
        private Date mEndDate;

        public Rating(
                final int ratedBookingCount, final int totalBookingCount,
                final int fiveStarRatedBookingCount, final double proRating, final String status,
                final String ratingEvaluation, final String feedback, final Date startDate,
                final Date endDate)
        {
            mRatedBookingCount = ratedBookingCount;
            mTotalBookingCount = totalBookingCount;
            mFiveStarRatedBookingCount = fiveStarRatedBookingCount;
            mProRating = proRating;
            mStatus = status;
            mRatingEvaluation = ratingEvaluation;
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

        public int getStatusColorId()
        {
            switch (mStatus)
            {
                case POSITIVE:
                    return R.color.requested_green;
                case NEUTRAL:
                    return R.color.handy_yellow;
                case NA:
                    return R.color.requested_green;
                case NEGATIVE:
                default:
                    return R.color.error_red;
            }
        }

        public String getRatingEvaluation()
        {
            return mRatingEvaluation;
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


    public static class Tier implements Serializable
    {
        @SerializedName("name")
        private String mName;
        @SerializedName("hourly_rate_in_cents")
        private int mHourlyRateInCents;
        @SerializedName("currency_symbol")
        private String mCurrencySymbol;


        public Tier(final String name, final int hourlyRateInCents, final String currencySymbol)
        {
            mName = name;
            mHourlyRateInCents = hourlyRateInCents;
            mCurrencySymbol = currencySymbol;
        }

        public String getName()
        {
            return mName;
        }

        public int getHourlyRateInCents()
        {
            return mHourlyRateInCents;
        }

        public String getCurrencySymbol()
        {
            return mCurrencySymbol;
        }
    }
}
