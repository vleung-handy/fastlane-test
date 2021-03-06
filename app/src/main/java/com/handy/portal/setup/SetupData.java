package com.handy.portal.setup;

import com.google.gson.annotations.SerializedName;
import com.handy.portal.core.model.ConfigurationResponse;
import com.handy.portal.core.model.ProviderProfile;
import com.handy.portal.onboarding.model.OnboardingDetails;
import com.handy.portal.terms.TermsDetailsGroup;
import com.handy.portal.updater.model.UpdateDetails;

public class SetupData {
    @SerializedName("update_details")
    private UpdateDetails mUpdateDetails;
    @SerializedName("terms_details")
    private TermsDetailsGroup mTermsDetailsGroup;
    @SerializedName("configuration")
    private ConfigurationResponse mConfigurationResponse;
    @SerializedName("provider_profile")
    private ProviderProfile mProviderProfile;
    @SerializedName("onboarding")
    private OnboardingDetails mOnboardingDetails;
    @SerializedName("startup_deeplink")
    private String mStartupDeeplink;

    public UpdateDetails getUpdateDetails() {
        return mUpdateDetails;
    }

    public TermsDetailsGroup getTermsDetails() {
        return mTermsDetailsGroup;
    }

    public ConfigurationResponse getConfigurationResponse() {
        return mConfigurationResponse;
    }

    public ProviderProfile getProviderProfile() {
        return mProviderProfile;
    }

    public OnboardingDetails getOnboardingDetails() {
        return mOnboardingDetails;
    }

    public String getStartupDeeplink() {
        return mStartupDeeplink;
    }
}
