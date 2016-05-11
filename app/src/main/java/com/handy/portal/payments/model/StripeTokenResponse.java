package com.handy.portal.payments.model;

import com.google.gson.annotations.SerializedName;

public class StripeTokenResponse
{
    @SerializedName("id")
    private String stripeToken;

    public String getStripeToken()
    {
        return stripeToken;
    }
}
