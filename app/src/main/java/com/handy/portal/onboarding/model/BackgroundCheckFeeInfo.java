package com.handy.portal.onboarding.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * todo discuss api contract
 *
 * background check fee info in the context of the current provider
 */
public class BackgroundCheckFeeInfo implements Serializable
{
    public String getFeeFormatted()
    {
        return mFeeFormatted;
    }

    public String getSubHeaderText()
    {
        return mSubHeaderText;
    }

    public String getHelpUrl()
    {
        return mHelpUrl;
    }

    public String getHeaderText()
    {
        return mHeaderText;
    }

    public BackgroundCheckFeeInfo()
    {
        //tODO revert test only
        mFeeFormatted = "$50";
        mSubHeaderText = "In order to process your application, you’re responsible for a $50 background check fee.";
        mHeaderText = "Background Check Fee";
        mHelpUrl = "http://handy.com";
    }
    @SerializedName("fee_formatted")
    String mFeeFormatted; //$10
    @SerializedName("subheader_text")
    String mSubHeaderText;
    @SerializedName("header_text")
    String mHeaderText;
    @SerializedName("help_url")
    String mHelpUrl;
}
