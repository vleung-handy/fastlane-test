package com.handy.portal.onboarding.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class OnboardingDetails
{
    @SerializedName("steps")
    private ArrayList<OnboardingSubflowDetails> mSubflows;

    public ArrayList<OnboardingSubflowDetails> getSubflows()
    {
        return mSubflows;
    }
}
