package com.handy.portal.onboarding.model.subflow;

import com.google.gson.annotations.SerializedName;
import com.handy.portal.bookings.model.Booking;
import com.handy.portal.model.onboarding.SuppliesInfo;
import com.handy.portal.onboarding.model.claim.StartDateRange;
import com.handy.portal.onboarding.model.claim.Zipcluster;
import com.handy.portal.onboarding.model.status.ApplicationStatus;
import com.handy.portal.onboarding.model.status.LearningLinkDetails;
import com.handy.portal.onboarding.model.status.StatusButton;

import java.io.Serializable;
import java.util.ArrayList;

public class SubflowData implements Serializable
{
    // Generic Subflow Data
    @SerializedName("supplies_info")
    private SuppliesInfo mSuppliesInfo; // belongs to Status and Supplies subflows
    @SerializedName("header")
    private OnboardingHeader mHeader; // belongs to Status and Confirmation subflows

    public SuppliesInfo getSuppliesInfo()
    {
        return mSuppliesInfo;
    }

    public OnboardingHeader getHeader()
    {
        return mHeader;
    }

    // Status Subflow Data
    @SerializedName("application_status")
    private ApplicationStatus mApplicationStatus;
    @SerializedName("learning_links")
    private LearningLinkDetails mLearningLinkDetails;
    @SerializedName("claims")
    private ArrayList<Booking> mClaims;
    @SerializedName("button")
    private StatusButton mButton;

    public ApplicationStatus getApplicationStatus()
    {
        return mApplicationStatus;
    }

    public LearningLinkDetails getLearningLinkDetails()
    {
        return mLearningLinkDetails;
    }

    public ArrayList<Booking> getClaims()
    {
        return mClaims;
    }

    public StatusButton getButton()
    {
        return mButton;
    }

    // Claim Subflow Data
    @SerializedName("claim_header")
    private OnboardingHeader mClaimHeader;
    @SerializedName("schedule_header")
    private OnboardingHeader mScheduleHeader;
    @SerializedName("date_range")
    private StartDateRange mStartDateRange;
    @SerializedName("locations")
    private ArrayList<Zipcluster> mZipclusters;


    public OnboardingHeader getClaimHeader()
    {
        return mClaimHeader;
    }

    public OnboardingHeader getScheduleHeader()
    {
        return mScheduleHeader;
    }

    public StartDateRange getStartDateRange()
    {
        return mStartDateRange;
    }

    public ArrayList<Zipcluster> getZipclusters()
    {
        return mZipclusters;
    }

    // Supplies Subflow Data
    @SerializedName("supplies_header")
    private OnboardingHeader mSuppliesHeader;
    @SerializedName("payment_header")
    private OnboardingHeader mPaymentHeader;

    public OnboardingHeader getSuppliesHeader()
    {
        return mSuppliesHeader;
    }

    public OnboardingHeader getPaymentHeader()
    {
        return mPaymentHeader;
    }
}
