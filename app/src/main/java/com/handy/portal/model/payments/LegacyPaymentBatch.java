package com.handy.portal.model.payments;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class LegacyPaymentBatch extends PaymentBatch
{
    @SerializedName("booking_id")
    private int bookingId;

    @SerializedName("date")
    private Date date;

    @SerializedName("status")
    private String status;

    @SerializedName("currency_symbol")
    private String currencySymbol;

    @SerializedName("earned_by_provider")
    private int earnedByProvider;

    public int getBookingId()
    {
        return bookingId;
    }

    public Date getDate()
    {
        return date;
    }

    public String getStatus()
    {
        return status;
    }

    public String getCurrencySymbol()
    {
        return currencySymbol;
    }

    public int getEarnedByProvider()
    {
        return earnedByProvider;
    }
}
