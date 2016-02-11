package com.handy.portal.model.dashboard;


import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class ProviderRatings
{
    @SerializedName("id")
    private int id;
    @SerializedName("user_id")
    private int mUserId;
    @SerializedName("rating")
    private int mRating;
    @SerializedName("booking_id")
    private int mBookingId;
    @SerializedName("date_rating")
    private Date mDateRating;
    @SerializedName("source")
    private String mSource;
    @SerializedName("comment")
    private String mComment;

    public int getId()
    {
        return id;
    }

    public int getUserId()
    {
        return mUserId;
    }

    public int getRating()
    {
        return mRating;
    }

    public int getBookingId()
    {
        return mBookingId;
    }

    public Date getDateRating()
    {
        return mDateRating;
    }

    public String getSource()
    {
        return mSource;
    }

    public String getComment()
    {
        return mComment;
    }
}
