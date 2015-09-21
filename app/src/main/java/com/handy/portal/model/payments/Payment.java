package com.handy.portal.model.payments;

import com.google.gson.annotations.SerializedName;
import com.handy.portal.util.CurrencyUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by vleung on 9/14/15.
 */
public class Payment implements Serializable
{
    public Date getDate()
    {
        return date;
    }

    public int getDollarAmount()
    {
        return CurrencyUtils.centsToDollars(getAmount());
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


}
