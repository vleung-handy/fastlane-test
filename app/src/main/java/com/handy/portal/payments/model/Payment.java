package com.handy.portal.payments.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

public class Payment implements Serializable
{
    @SerializedName("date")
    private Date date;

    @SerializedName("amount")
    private int amount;

    @SerializedName("title")
    private String title;

    @SerializedName("subtitle")
    private String subTitle;

    @SerializedName("booking_id")
    private String bookingId;

    @SerializedName("booking_type")
    private String bookingType;

    @SerializedName("currency_symbol")
    private String mCurrencySymbol;

    public Date getDate()
    {
        return date;
    }

    public int getAmount()
    {
        return amount;
    }

    public String getTitle()
    {
        return title;
    }

    public String getSubTitle()
    {
        return subTitle;
    }

    public String getBookingId()
    {
        return bookingId;
    }

    public String getBookingType()
    {
        return bookingType;
    }

    public String getCurrencySymbol() {return mCurrencySymbol;}
}
