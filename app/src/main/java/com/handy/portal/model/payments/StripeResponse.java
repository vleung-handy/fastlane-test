package com.handy.portal.model.payments;

import com.google.gson.annotations.SerializedName;

public class StripeResponse
{
    @SerializedName("id")
    private String stripeToken;

    public String getStripeToken()
    {
        return stripeToken;
    }
}
