package com.handy.portal.onboarding.model.subflow;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

public class OnboardingSubflowDetails
{
    @SerializedName("type")
    private SubflowType mType;
    @SerializedName("status")
    private SubflowStatus mStatus;
    @SerializedName("data")
    private JsonObject mData;

    public SubflowType getType()
    {
        return mType;
    }

    public SubflowStatus getStatus()
    {
        return mStatus;
    }

    public Object getData()
    {
        final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();
        return gson.fromJson(mData, mType.getDataClass());
    }
}
