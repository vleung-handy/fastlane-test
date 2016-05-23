package com.handy.portal.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TermsDetailsGroup
{
    @SerializedName("terms")
    List<TermsDetails> termsDetails;

    public List<TermsDetails> getTermsDetails()
    {
        return termsDetails;
    }

    public boolean hasTerms()
    {
        return termsDetails != null && !termsDetails.isEmpty();
    }
}
