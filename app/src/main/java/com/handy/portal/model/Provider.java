package com.handy.portal.model;

import com.google.gson.annotations.SerializedName;

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
    @SerializedName("onboarding_enabled")
    private boolean onboardingEnabled;
    @SerializedName("complementary_jobs_enabled")
    private boolean complementaryJobsEnabled;

    //TODO: reorganize
    @SerializedName("payment_currency_code")
    private String paymentCurrencyCode;
    @SerializedName("recommended_payment_flow")
    private String recommendedPaymentFlow;

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

    public boolean isOnboardingEnabled()
    {
        return onboardingEnabled;
    }

    public String getCountry()
    {
        return country;
    }

    public boolean isUK()
    {
        return "GB".equalsIgnoreCase(getCountry());
    }

    public boolean isUS()
    {
        return "US".equalsIgnoreCase(getCountry());
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
}
