package com.handy.portal.dashboard.model;


import com.google.gson.annotations.SerializedName;
import com.handy.portal.R;
import com.handy.portal.library.util.TextUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProviderEvaluation implements Serializable {
    @SerializedName("rolling")
    private Rating mRolling;
    @SerializedName("lifetime")
    private Rating mLifeTime;
    @SerializedName("weekly")
    private Rating mWeeklyRating;
    @SerializedName("pay_rates")
    private PayRates mPayRates;
    @SerializedName("danger_rating_threshold")
    private double mDangerRatingThreshold;
    @SerializedName("five_star_ratings")
    private List<ProviderRating> mFiveStarRatings;
    @SerializedName("feedback")
    private List<ProviderFeedback> mProviderFeedback;

    private List<ProviderRating> mFiveStarRatingsWithComments;

    public ProviderEvaluation(final Rating rolling, final Rating lifeTime,
                              final Rating weeklyRating, final PayRates payRates,
                              final double dangerRatingThreshold,
                              final List<ProviderRating> fiveStarRatings,
                              final List<ProviderFeedback> providerFeedback,
                              final List<ProviderRating> fiveStarRatingsWithComments) {
        mRolling = rolling;
        mLifeTime = lifeTime;
        mWeeklyRating = weeklyRating;
        mPayRates = payRates;
        mDangerRatingThreshold = dangerRatingThreshold;
        mFiveStarRatings = fiveStarRatings;
        mProviderFeedback = providerFeedback;
        mFiveStarRatingsWithComments = fiveStarRatingsWithComments;
    }

    public Rating getRolling() {
        return mRolling;
    }

    public Rating getLifeTime() {
        return mLifeTime;
    }

    public Rating getWeeklyRating() {
        return mWeeklyRating;
    }

    public PayRates getPayRates() {
        return mPayRates;
    }

    public double getDangerRatingThreshold() {
        return mDangerRatingThreshold;
    }

    public List<ProviderRating> getFiveStarRatings() {
        return mFiveStarRatings;
    }

    public List<ProviderFeedback> getProviderFeedback() {
        return mProviderFeedback;
    }

    public List<ProviderRating> getFiveStarRatingsWithComments() {
        if (mFiveStarRatingsWithComments == null) {
            mFiveStarRatingsWithComments = new ArrayList<>();
            for (ProviderRating rating : mFiveStarRatings) {
                if (!TextUtils.isNullOrEmpty(rating.getComment())) {
                    mFiveStarRatingsWithComments.add(rating);
                }
            }
        }
        return mFiveStarRatingsWithComments;
    }


    public static class PayRates implements Serializable {
        @SerializedName("incentives")
        private List<Incentive> mIncentives;
        @SerializedName("service_descriptions")
        private List<TiersServiceDescription> mTiersServiceDescriptions;

        public PayRates(final List<Incentive> incentives, final List<TiersServiceDescription> tiersServiceDescriptions) {
            mIncentives = incentives;
            mTiersServiceDescriptions = tiersServiceDescriptions;
        }

        public List<Incentive> getIncentives() {
            return mIncentives;
        }

        public List<TiersServiceDescription> getTiersServiceDescriptions() {
            return mTiersServiceDescriptions;
        }
    }


    public static class TiersServiceDescription implements Serializable {
        @SerializedName("title")
        private String mTitle;
        @SerializedName("body")
        private String mBody;

        public TiersServiceDescription(final String title, final String body) {
            mTitle = title;
            mBody = body;
        }

        public String getTitle() {
            return mTitle;
        }

        public String getBody() {
            return mBody;
        }
    }


    public static class Rating implements Serializable {
        private static final String POSITIVE = "positive";
        private static final String NEUTRAL = "neutral";
        private static final String NEGATIVE = "negative";
        private static final String NA = "n_a";

        @SerializedName("rated_booking_count")
        private int mRatedBookingCount;
        @SerializedName("total_booking_count")
        private int mTotalBookingCount;
        @SerializedName("completed_bookings")
        private int mCompletedBookings;
        @SerializedName("five_star_rated_booking_count")
        private int mFiveStarRatedBookingCount;
        @SerializedName("pro_rating")
        private double mProRating;
        @SerializedName("status")
        private String mStatus;
        @SerializedName("rating_evaluation")
        private String mRatingEvaluation;
        @SerializedName("start_date")
        private Date mStartDate;
        @SerializedName("end_date")
        private Date mEndDate;

        public Rating(final int ratedBookingCount, final int totalBookingCount,
                      final int completedBookings, final int fiveStarRatedBookingCount,
                      final double proRating, final String status, final String ratingEvaluation,
                      final Date startDate, final Date endDate) {
            mRatedBookingCount = ratedBookingCount;
            mTotalBookingCount = totalBookingCount;
            mCompletedBookings = completedBookings;
            mFiveStarRatedBookingCount = fiveStarRatedBookingCount;
            mProRating = proRating;
            mStatus = status;
            mRatingEvaluation = ratingEvaluation;
            mStartDate = startDate;
            mEndDate = endDate;
        }

        public int getRatedBookingCount() {
            return mRatedBookingCount;
        }

        public int getTotalBookingCount() {
            return mTotalBookingCount;
        }

        public int getCompletedBookings() {
            return mCompletedBookings;
        }

        public int getFiveStarRatedBookingCount() {
            return mFiveStarRatedBookingCount;
        }

        public double getProRating() {
            return mProRating;
        }

        public String getStatus() {
            return mStatus;
        }

        public int getStatusColorId() {
            switch (mStatus) {
                case POSITIVE:
                    return R.color.cleaner_green;
                case NEUTRAL:
                    return R.color.electrician_yellow;
                case NA:
                    return R.color.cleaner_green;
                case NEGATIVE:
                default:
                    return R.color.plumber_red;
            }
        }

        public String getRatingEvaluation() {
            return mRatingEvaluation;
        }

        public Date getStartDate() {
            return mStartDate;
        }

        public Date getEndDate() {
            return mEndDate;
        }
    }


    public static class Incentive implements Serializable {
        @SerializedName("region_name")
        private String mRegionName;
        @SerializedName("service_name")
        private String mServiceName;
        @SerializedName("tiers")
        private List<Tier> mTiers;
        @SerializedName("current_tier")
        private int mCurrentTier; // 1,2,3, 0 = not available
        @SerializedName("jobs_until_next_tier")
        private int mJobsUntilNextTier;
        @SerializedName("currency_symbol")
        private String mCurrencySymbol;
        @SerializedName("type")
        private String mType; // flat rate/1 tier("incentive_type_flat_rate") or multiple tiers("incentive_type_tiered")

        public static final String TIERED_TYPE = "incentive_type_tiered";
        public static final String ROLLING_TYPE = "incentive_type_rolling";
        public static final String HANDYMEN_ROLLING_TYPE = "incentive_type_handyman_rolling";
        public static final String HANDYMEN_TIERED_TYPE = "incentive_type_handyman_tiered";

        public Incentive(final String regionName, final String serviceName, final List<Tier> tiers,
                         final int currentTier, final int jobsUntilNextTier,
                         final String currencySymbol, final String type) {
            mRegionName = regionName;
            mServiceName = serviceName;
            mTiers = tiers;
            mCurrentTier = currentTier;
            mJobsUntilNextTier = jobsUntilNextTier;
            mCurrencySymbol = currencySymbol;
            mType = type;
        }

        public String getRegionName() {
            return mRegionName;
        }

        public String getServiceName() {
            return mServiceName;
        }

        public List<Tier> getTiers() {
            return mTiers;
        }

        public int getCurrentTier() {
            return mCurrentTier;
        }

        public int getJobsUntilNextTier() {
            return mJobsUntilNextTier;
        }

        public String getCurrencySymbol() {
            return mCurrencySymbol;
        }

        public String getType() {
            return mType;
        }
    }


    public static class Tier implements Serializable {
        @SerializedName("name")
        private String mName;
        @SerializedName("job_requirement_range_minimum")
        private int mJobRequirementRangeMinimum;
        @SerializedName("job_requirement_range_maximum")
        private int mJobRequirementRangeMaximum;
        @SerializedName("hourly_rate_in_cents")
        private int mHourlyRateInCents;

        public Tier(final String name, final int jobRequirementRangeMinimum,
                    final int jobRequirementRangeMaximum, final int hourlyRateInCents) {
            mName = name;
            mJobRequirementRangeMinimum = jobRequirementRangeMinimum;
            mJobRequirementRangeMaximum = jobRequirementRangeMaximum;
            mHourlyRateInCents = hourlyRateInCents;
        }

        public String getName() {
            return mName;
        }

        public int getJobRequirementRangeMinimum() {
            return mJobRequirementRangeMinimum;
        }

        public int getJobRequirementRangeMaximum() {
            return mJobRequirementRangeMaximum;
        }

        public int getHourlyRateInCents() {
            return mHourlyRateInCents;
        }
    }
}
