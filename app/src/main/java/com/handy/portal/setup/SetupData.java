package com.handy.portal.setup;

import com.google.gson.annotations.SerializedName;
import com.handy.portal.model.ConfigurationResponse;
import com.handy.portal.model.ProviderProfile;
import com.handy.portal.model.TermsDetailsGroup;
import com.handy.portal.updater.model.UpdateDetails;

public class SetupData
{
    @SerializedName("update_details")
    UpdateDetails mUpdateDetails;
    @SerializedName("terms_details")
    TermsDetailsGroup mTermsDetailsGroup;
    @SerializedName("configuration")
    ConfigurationResponse mConfigurationResponse;
    @SerializedName("provider_profile")
    ProviderProfile mProviderProfile;

    public UpdateDetails getUpdateDetails()
    {
        return mUpdateDetails;
    }

    public TermsDetailsGroup getTermsDetails()
    {
        return mTermsDetailsGroup;
    }

    public ConfigurationResponse getConfigurationResponse()
    {
        return mConfigurationResponse;
    }

    public ProviderProfile getProviderProfile()
    {
        return mProviderProfile;
    }
}
