package com.handy.portal.onboarding.model;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;
import com.handy.portal.onboarding.model.subflow.OnboardingSubflowDetails;
import com.handy.portal.onboarding.model.subflow.SubflowData;
import com.handy.portal.onboarding.model.subflow.SubflowStatus;
import com.handy.portal.onboarding.model.subflow.SubflowType;

import java.io.Serializable;
import java.util.ArrayList;

public class OnboardingDetails implements Serializable
{
    @SerializedName("steps")
    private ArrayList<OnboardingSubflowDetails> mSubflows;
    @SerializedName("percent_complete")
    private float mPercentComplete;

    public ArrayList<OnboardingSubflowDetails> getSubflows()
    {
        return mSubflows;
    }

    @Nullable
    public SubflowData getSubflowDataByType(final SubflowType type)
    {
        for (final OnboardingSubflowDetails details : mSubflows)
        {
            if (details.getType() == type)
            {
                return details.getData();
            }
        }
        return null;
    }

    public ArrayList<OnboardingSubflowDetails> getSubflowsByStatus(final SubflowStatus status)
    {
        final ArrayList<OnboardingSubflowDetails> subflows = new ArrayList<>();
        for (final OnboardingSubflowDetails subflow : mSubflows)
        {
            if (subflow.getStatus() == status)
            {
                subflows.add(subflow);
            }
        }
        return subflows;
    }

    public float getPercentComplete()
    {
        return mPercentComplete;
    }
}
