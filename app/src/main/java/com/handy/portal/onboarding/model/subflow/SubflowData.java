package com.handy.portal.onboarding.model.subflow;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;
import com.handy.portal.bookings.model.Booking;
import com.handy.portal.onboarding.model.claim.StartDateRange;
import com.handy.portal.onboarding.model.claim.Zipcluster;
import com.handy.portal.onboarding.model.status.LearningLinkDetails;
import com.handy.portal.onboarding.model.status.StatusButton;
import com.handy.portal.onboarding.model.supplies.SuppliesInfo;

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
    private String mApplicationStatus;
    @SerializedName("learning_links")
    private LearningLinkDetails mLearningLinkDetails;
    @SerializedName("claims")
    private ArrayList<Booking> mClaims;
    @SerializedName("button")
    private StatusButton mButton;
    @SerializedName("header")
    private StatusHeader mHeader;

    // ID Verification
    @SerializedName("full_name")
    private String mFullName;
    @SerializedName("candidate_id")
    private String mCandidateId;
    @SerializedName("jumio_token")
    private String mJumioToken;
    @SerializedName("jumio_secret")
    private String mJumioSecret;
    @SerializedName("jumio_url")
    private String mJumioUrl;
    @SerializedName("before_start")
    private String mBeforeIdVerificationStartUrl;
    @SerializedName("after_finish")
    private String mAfterIdVerificationFinishUrl;
    @SerializedName("first_job_content_enabled")
    private boolean mFirstJobContentEnabled;

    public String getApplicationStatus()
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

    public String getFullName()
    {
        return mFullName;
    }

    public String getCandidateId()
    {
        return mCandidateId;
    }

    public String getJumioToken()
    {
        return mJumioToken;
    }

    public String getJumioSecret()
    {
        return mJumioSecret;
    }

    public String getJumioURL()
    {
        return mJumioUrl;
    }

    public String getBeforeIdVerificationStartUrl()
    {
        return mBeforeIdVerificationStartUrl;
    }

    public String getAfterIdVerificationFinishUrl()
    {
        return mAfterIdVerificationFinishUrl;
    }

    public boolean isFirstJobContentEnabled()
    {
        return mFirstJobContentEnabled;
    }
}
