package com.handy.portal.terms;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class TermsDetailsGroup implements Serializable
{
    @SerializedName("terms")
    private List<TermsDetails> termsDetails;

    public List<TermsDetails> getTermsDetails()
    {
        return termsDetails;
    }

    public boolean hasTerms()
    {
        return termsDetails != null && !termsDetails.isEmpty();
    }
}
