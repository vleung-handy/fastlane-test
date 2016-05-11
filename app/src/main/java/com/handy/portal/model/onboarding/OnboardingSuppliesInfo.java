package com.handy.portal.model.onboarding;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class OnboardingSuppliesInfo implements Serializable
{
    @SerializedName("cost")
    private String mSuppliesCost;
    @SerializedName("charge_notice")
    private String mChargeNotice;
    @SerializedName("cost_section")
    private OnboardingSuppliesSection mCostSection;
    @SerializedName("delivery_section")
    private OnboardingSuppliesSection mDeliverySection;
    @SerializedName("products_section")
    private OnboardingSuppliesSection mProductsSection;

    public String getSuppliesCost()
    {
        return mSuppliesCost;
    }

    public String getChargeNotice()
    {
        return mChargeNotice;
    }

    public OnboardingSuppliesSection getCostSection()
    {
        return mCostSection;
    }

    public OnboardingSuppliesSection getDeliverySection()
    {
        return mDeliverySection;
    }

    public OnboardingSuppliesSection getProductsSection()
    {
        return mProductsSection;
    }
}
