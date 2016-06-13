package com.handy.portal.onboarding.model.subflow;

import android.support.annotation.Nullable;

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

    public SuppliesInfo getSuppliesInfo()
    {
        return mSuppliesInfo;
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
    @SerializedName("header")
    private StatusHeader mHeader;

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

    @Nullable
    public StatusButton getButton()
    {
        return mButton;
    }

    public StatusHeader getHeader()
    {
        return mHeader;
    }

    // Claim Subflow Data
    @SerializedName("date_range")
    private StartDateRange mStartDateRange;
    @SerializedName("locations")
    private ArrayList<Zipcluster> mZipclusters;

    public StartDateRange getStartDateRange()
    {
        return mStartDateRange;
    }

    public ArrayList<Zipcluster> getZipclusters()
    {
        return mZipclusters;
    }
}
