package com.handy.portal.onboarding.model.supplies;

import com.handy.portal.model.Designation;

import java.io.Serializable;

public class SuppliesOrderInfo implements Serializable
{
    private Designation mDesignation;
    private String mShippingText;
    private String mPaymentText;
    private String mOrderTotalText;

    public Designation getDesignation()
    {
        return mDesignation;
    }

    public String getShippingText()
    {
        return mShippingText;
    }

    public String getPaymentText()
    {
        return mPaymentText;
    }

    public String getOrderTotalText()
    {
        return mOrderTotalText;
    }

    public void setDesignation(final Designation designation)
    {
        mDesignation = designation;
    }

    public void setShippingText(final String shippingText)
    {
        mShippingText = shippingText;
    }

    public void setPaymentText(final String paymentText)
    {
        mPaymentText = paymentText;
    }

    public void setOrderTotalText(final String orderTotalText)
    {
        mOrderTotalText = orderTotalText;
    }
}
