package com.handy.portal.onboarding.model.supplies;

import com.google.gson.annotations.SerializedName;
import com.handy.portal.core.model.Designation;

import java.io.Serializable;

public class SuppliesInfo implements Serializable
{
    @SerializedName("cost")
    private String mCost;
    @SerializedName("charge_notice")
    private String mChargeNotice;
    @SerializedName("cost_section")
    private SuppliesSection mCostSection;
    @SerializedName("delivery_section")
    private SuppliesSection mDeliverySection;
    @SerializedName("products_section")
    private SuppliesSection mProductsSection;
    @SerializedName("provider_wants_supplies")
    private Designation mDesignation;
    @SerializedName("is_card_required")
    private boolean isCardRequired;
    @SerializedName("fees_help_link")
    private String mFeesHelpLink;

    public String getCost()
    {
        return mCost;
    }

    public String getChargeNotice()
    {
        return mChargeNotice;
    }

    public SuppliesSection getCostSection()
    {
        return mCostSection;
    }

    public SuppliesSection getDeliverySection()
    {
        return mDeliverySection;
    }

    public SuppliesSection getProductsSection()
    {
        return mProductsSection;
    }

    public Designation getDesignation()
    {
        return mDesignation;
    }

    public boolean isCardRequired()
    {
        return isCardRequired;
    }

    public String getFeesHelpLink()
    {
        return mFeesHelpLink;
    }
}
