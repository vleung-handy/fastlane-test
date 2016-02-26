package com.handy.portal.model.dashboard;


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

public class ProviderRating implements Serializable
{
    @SerializedName("id")
    private int mId;
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

    public ProviderRating(final int id, final int userId, final int rating, final int bookingId,
                          final Date dateRating, final String source, final String comment)
    {
        mId = id;
        mUserId = userId;
        mRating = rating;
        mBookingId = bookingId;
        mDateRating = dateRating;
        mSource = source;
        mComment = comment;
    }

    public int getId()
    {
        return mId;
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
