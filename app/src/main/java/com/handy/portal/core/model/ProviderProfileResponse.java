package com.handy.portal.core.model;

import com.google.gson.annotations.SerializedName;

public class ProviderProfileResponse {
    @SerializedName("provider_profile")
    private ProviderProfile mProviderProfile;

    public ProviderProfile getProviderProfile() { return mProviderProfile; }
}
