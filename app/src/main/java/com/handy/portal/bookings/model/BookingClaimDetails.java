package com.handy.portal.bookings.model;

import com.google.gson.annotations.SerializedName;
import com.handy.portal.model.PaymentInfo;

import java.io.Serializable;

public class BookingClaimDetails implements Serializable
{
    @SerializedName("job_details")
    private Booking booking;

    @SerializedName("claim_target_info")
    private ClaimTargetInfo claimTargetInfo;

    public Booking getBooking()
    {
        return booking;
    }

    public ClaimTargetInfo getClaimTargetInfo()
    {
        return claimTargetInfo;
    }

    public boolean shouldShowClaimTarget()
    {
        return claimTargetInfo != null && claimTargetInfo.shouldShowClaimTarget();
    }

    public static class ClaimTargetInfo implements Serializable
    {
        @SerializedName("num_jobs_claimed")
        private Integer numJobsClaimed; //number of jobs the provider has ever claimed (including cancelled)
        @SerializedName("claim_target_num_bookings_threshold")
        private Integer numBookingsThreshold; //if user has claimed more than this, do not show claim target
        @SerializedName("claim_target_days_expected_payment")
        private Integer numDaysExpectedPayment; //number of days the expected provider pay represents
        @SerializedName("expected_payment_to_provider_next_x_days")
        private PaymentInfo paymentInfo;

        public Integer getNumJobsClaimed()
        {
            return numJobsClaimed;
        }

        public Integer getNumBookingsThreshold()
        {
            return numBookingsThreshold;
        }

        public Integer getNumDaysExpectedPayment()
        {
            return numDaysExpectedPayment;
        }

        public boolean shouldShowClaimTarget()
        {
            return !(numJobsClaimed == null || numBookingsThreshold == null || paymentInfo == null)
                    && numJobsClaimed <= numBookingsThreshold;
        }

        public PaymentInfo getPaymentInfo()
        {
            return paymentInfo;
        }
    }
}
