package com.handy.portal.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;
import com.handy.portal.constant.LocationKey;

import java.util.List;

public class CheckoutRequest
{
    //The server is expecting location data at the top level of the object, otherwise would just pass location data object
    @SerializedName("latitude")
    private String mLatitude;
    @SerializedName("longitude")
    private String mLongitude;
    @SerializedName("accuracy")
    private String mAccuracy;
    @SerializedName("pro_feedback")
    private ProBookingFeedback mProFeedback;
    @SerializedName("booking_instructions")
    private List<Booking.BookingInstructionUpdateRequest> mCustomerPreferences;


    public CheckoutRequest(@NonNull LocationData locationData, ProBookingFeedback feedback,
                           @Nullable List<Booking.BookingInstructionUpdateRequest> customerPreferences)
    {
        mLatitude = locationData.getLocationMap().get(LocationKey.LATITUDE);
        mLongitude = locationData.getLocationMap().get(LocationKey.LONGITUDE);
        mAccuracy = locationData.getLocationMap().get(LocationKey.ACCURACY);
        mProFeedback = feedback;
        mCustomerPreferences = customerPreferences;
    }
}
