package com.handy.portal.model.payments;

import com.google.gson.annotations.SerializedName;

public class CreateDebitCardResponse
{
    @SerializedName("card_details")
    private String cardDetails;

    public String getCardDetails()
    {
        return cardDetails;
    }
}
