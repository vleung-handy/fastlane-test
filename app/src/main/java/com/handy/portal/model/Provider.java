package com.handy.portal.model;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;
import com.handy.portal.constant.Country;

public class Provider
{
    @SerializedName("id")
    private String id;
    @SerializedName("email")
    private String email;
    @SerializedName("first_name")
    private String firstName;
    @SerializedName("last_name")
    private String lastName;
    @SerializedName("country")
    private String country;
    @SerializedName("complementary_jobs_enabled")
    private boolean complementaryJobsEnabled;
    @SerializedName("block_cleaner")
    private boolean isBlockCleaner;
    //TODO: reorganize
    @SerializedName("payment_currency_code")
    private String paymentCurrencyCode;
    @SerializedName("recommended_payment_flow")
    private String recommendedPaymentFlow;
    @SerializedName("version_track")
    private String mVersionTrack;

    public boolean isBlockCleaner()
    {
        return isBlockCleaner;
    }

    public enum RecommendedPaymentFlow{
        STRIPE_DEBIT("stripe_debit"), STRIPE("stripe");
        private String value;
        RecommendedPaymentFlow(String value)
        {
            this.value = value;
        }

        public String getValue()
        {
            return value;
        }
    }
    public String getId()
    {
        return id;
    }

    public String getEmail()
    {
        return email;
    }

    public String getFirstName()
    {
        return firstName;
    }

    public String getLastName()
    {
        return lastName;
    }

    public String getAbbreviatedName()
    {
        return firstName + (lastName.isEmpty() ? "" : " " + lastName.charAt(0) + ".");
    }

    public String getFullName()
    {
        return firstName + " " + lastName;
    }

    public String getCountry()
    {
        return country;
    }

    public boolean isUK()
    {
        return Country.GB.equalsIgnoreCase(getCountry());
    }

    public boolean isUS()
    {
        return Country.US.equalsIgnoreCase(getCountry());
    }

    public boolean isComplementaryJobsEnabled()
    {
        return complementaryJobsEnabled;
    }

    public String getPaymentCurrencyCode()
    {
        return paymentCurrencyCode;
    }

    public String getRecommendedPaymentFlow()
    {
        return recommendedPaymentFlow;
    }

    @Nullable
    public String getVersionTrack() { return mVersionTrack; }
}
