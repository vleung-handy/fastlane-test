package com.handy.portal.model.payments;

import com.google.gson.annotations.SerializedName;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by vleung on 9/14/15.
 */
public class LegacyPayment extends PaymentBatch
{
    @SerializedName("booking_id")
    private int bookingId;

    @SerializedName("booking_date")
    private String bookingDate; //TODO: change to Date once the server-side changes are made

//    @SerializedName("booking_date")
//    private Date bookingDate;

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

    public Date getBookingDate()
    {
        //TODO: remove once server-side changes are made
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMMM d, yyyy");
        try
        {
            Date d = simpleDateFormat.parse(bookingDate);
            return d;
        } catch (ParseException e)
        {
            e.printStackTrace();
        }
        return null;
//        return bookingDate;
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
        return earnedByProvider/100;
    }
}
