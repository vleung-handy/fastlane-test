package com.handy.portal.model;

import com.google.gson.annotations.SerializedName;

public class TermsDetailsGroup
{
    @SerializedName("terms")
    TermsDetails[] termsDetails;

    public TermsDetails[] getTermsDetails()
    {
        return termsDetails;
    }

    public boolean hasTerms()
    {
        return termsDetails != null && termsDetails.length > 0;
    }
}
