package com.handy.portal.payments.model;

import com.google.gson.annotations.SerializedName;

public class CreateDebitCardResponse {
    @SerializedName("card_details")
    private String cardDetails;

    public String getCardDetails() {
        return cardDetails;
    }
}
