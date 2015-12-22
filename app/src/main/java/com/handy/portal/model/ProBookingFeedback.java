package com.handy.portal.model;

import com.google.gson.annotations.SerializedName;

public class ProBookingFeedback
{
    @SerializedName("rating")
    private int mBookingRating;
    @SerializedName("review_text")
    private String mBookingReviewText;

    public ProBookingFeedback()
    {
        mBookingRating = -1;
        mBookingReviewText = "";
    }

    public ProBookingFeedback(int rating, String bookingReviewText)
    {
        mBookingRating = rating;
        mBookingReviewText = bookingReviewText;
    }
}