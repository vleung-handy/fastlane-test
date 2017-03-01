package com.handy.portal.core.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ProBookingFeedback implements Serializable {
    @SerializedName("rating")
    private int mBookingRating;
    @SerializedName("review_text")
    private String mBookingReviewText;

    public ProBookingFeedback() {
        mBookingRating = -1;
        mBookingReviewText = "";
    }

    public ProBookingFeedback(int rating, String bookingReviewText) {
        mBookingRating = rating;
        mBookingReviewText = bookingReviewText;
    }
}
