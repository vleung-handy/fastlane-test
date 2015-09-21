package com.handy.portal.model.payments;

import com.google.gson.annotations.SerializedName;
import com.handy.portal.util.CurrencyUtils;

import java.util.Date;

/**
 * Created by vleung on 9/14/15.
 */
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

    public int getDollarsEarnedByProvider()
    {
        return CurrencyUtils.centsToDollars(earnedByProvider);
    }
}
