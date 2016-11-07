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

    public boolean isCardRequired()
    {
        return mIsCardRequired;
    }

    public String getCardLast4Digits()
    {
        return mCardLast4Digits;
    }

    public BackgroundCheckFeeInfo()
    {
        //tODO revert test only
        mIsCardRequired = true;
        mFeeFormatted = "$50";
        mSubHeaderText = "In order to process your application, you’re responsible for a $50 background check fee.";
        mHeaderText = "Background Check Fee";
        mHelpUrl = "http://handy.com";
    }
    @SerializedName("card_last4")
    String mCardLast4Digits;
    @SerializedName("is_card_required")
    boolean mIsCardRequired;
    @SerializedName("fee_formatted")
    String mFeeFormatted; //$10
    @SerializedName("subheader_text")
    String mSubHeaderText;
    @SerializedName("header_text")
    String mHeaderText;
    @SerializedName("help_url")
    String mHelpUrl;
}
