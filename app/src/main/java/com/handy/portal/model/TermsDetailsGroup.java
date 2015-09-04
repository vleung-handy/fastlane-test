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

    public String[] getCodeList() //used in accept terms payload
    {
        String[] codeList = new String[termsDetails.length];
        for (int i = 0; i < codeList.length; i++)
        {
            codeList[i] = termsDetails[i].getCode();
        }
        return codeList;
    }

    public boolean hasTerms()
    {
        return termsDetails != null && termsDetails.length > 0;
    }
}
